// index.js
const express = require("express");
const cors = require("cors");
const bodyParser = require("body-parser");
const multer = require("multer");
const path = require("path");
const fs = require("fs");

const app = express();
app.use(cors());
app.use(bodyParser.json());

// ------------------------------
// Configuración de uploads
// ------------------------------
const uploadDir = path.join(__dirname, "uploads");
if (!fs.existsSync(uploadDir)) {
	fs.mkdirSync(uploadDir);
}
// Servir carpeta uploads de forma estática
app.use("/uploads", express.static(uploadDir));

// Configuración de multer
const storage = multer.diskStorage({
	destination: (req, file, cb) => {
		cb(null, uploadDir);
	},
	filename: (req, file, cb) => {
		const uniqueSuffix = Date.now() + "-" + Math.round(Math.random() * 1e9);
		const ext = path.extname(file.originalname);
		cb(null, file.fieldname + "-" + uniqueSuffix + ext);
	},
});
const upload = multer({ storage: storage });

// ------------------------------
// Datos en memoria
// ------------------------------
let auctions = [];
let currentId = 1;

// Helper para crear matriz 10x10 inicializada en 0
function createEmptyMatrix() {
	const matrix = [];
	for (let i = 0; i < 10; i++) {
		matrix.push(new Array(10).fill(0));
	}
	return matrix;
}

// ------------------------------
// Endpoints
// ------------------------------

// Health check / root
app.get("/", (req, res) => {
	res.send("API de subastas/rifas corriendo. Usa /auctions");
});

// GET /auctions?search=   → Lista de summaries, con filtro opcional por nombre o fecha substring
app.get("/auctions", (req, res) => {
	const { search } = req.query;
	let list = auctions.map((a) => {
		return {
			id: a.id,
			name: a.name,
			date: a.date,
			currentMaxOffer: a.currentMaxOffer,
			inscritos: a.bids.length,
			imageUrl: a.imageUrl,
			isFinished: a.isFinished,
			winnerNumber: a.winnerNumber,
		};
	});
	if (search) {
		const s = search.toLowerCase();
		list = list.filter(
			(item) => item.name.toLowerCase().includes(s) || item.date.includes(s)
		);
	}
	res.json(list);
});

// GET /auctions/:id  → Detalle completo de la subasta
app.get("/auctions/:id", (req, res) => {
	const id = parseInt(req.params.id);
	const a = auctions.find((x) => x.id === id);
	if (!a) {
		return res.status(404).json({ message: "Subasta no encontrada" });
	}
	const detail = {
		id: a.id,
		name: a.name,
		date: a.date,
		matrix: a.matrix,
		currentMaxOffer: a.currentMaxOffer,
		minOffer: a.minOffer,
		bids: a.bids,
		inscritos: a.bids.length,
		imageUrl: a.imageUrl,
		isFinished: a.isFinished,
		winnerNumber: a.winnerNumber,
	};
	res.json(detail);
});

// POST /auctions  → Crear subasta con multipart (campo "image")
app.post("/auctions", upload.single("image"), (req, res) => {
	const { name, date, minOffer } = req.body;
	const minOfferNum = Number(minOffer);
	if (
		typeof name !== "string" ||
		typeof date !== "string" ||
		isNaN(minOfferNum)
	) {
		return res
			.status(400)
			.json({ message: "Campos inválidos: name, date o minOffer" });
	}
	// Opcional: validar formato de date (e.g. YYYY-MM-DD)
	// Inicializar matrix y resto de campos
	const matrix = createEmptyMatrix();
	let imageUrl = null;
	if (req.file) {
		imageUrl = `${req.protocol}://${req.get("host")}/uploads/${
			req.file.filename
		}`;
	}
	const newAuction = {
		id: currentId++,
		name,
		date,
		matrix,
		minOffer: minOfferNum,
		bids: [],
		currentMaxOffer: 0,
		imageUrl,
		isFinished: false,
		winnerNumber: null,
		createdAt: new Date().toISOString(),
		updatedAt: new Date().toISOString(),
	};
	auctions.push(newAuction);
	// Devolver summary o detalle según prefieras; aquí summary
	res.status(201).json({
		id: newAuction.id,
		name: newAuction.name,
		date: newAuction.date,
		currentMaxOffer: newAuction.currentMaxOffer,
		inscritos: newAuction.bids.length,
		imageUrl: newAuction.imageUrl,
		isFinished: newAuction.isFinished,
		winnerNumber: newAuction.winnerNumber,
	});
});

// POST /auctions/:id/bids  → Enviar puja
app.post("/auctions/:id/bids", (req, res) => {
	const id = parseInt(req.params.id);
	const a = auctions.find((x) => x.id === id);
	if (!a) {
		return res.status(404).json({ message: "Subasta no encontrada" });
	}
	if (a.isFinished) {
		return res.status(400).json({ message: "La subasta ya finalizó" });
	}
	const { number, amount } = req.body;
	const num = Number(number);
	const amt = Number(amount);
	if (isNaN(num) || num < 0 || num > 99 || isNaN(amt)) {
		return res.status(400).json({ message: "Número o monto inválido" });
	}
	const row = Math.floor(num / 10);
	const col = num % 10;
	if (a.matrix[row][col] !== 0) {
		return res.status(400).json({ message: "Número no disponible" });
	}
	if (amt < a.minOffer) {
		return res
			.status(400)
			.json({ message: `La oferta debe ser >= minOffer (${a.minOffer})` });
	}
	if (amt <= a.currentMaxOffer) {
		return res
			.status(400)
			.json({
				message: `La oferta debe ser > oferta máxima actual (${a.currentMaxOffer})`,
			});
	}
	// Registrar puja
	a.matrix[row][col] = 1;
	const bid = {
		number: num,
		amount: amt,
		timestamp: new Date().toISOString(),
	};
	a.bids.push(bid);
	a.currentMaxOffer = amt;
	a.updatedAt = new Date().toISOString();
	return res
		.status(201)
		.json({ message: "Puja aceptada", currentMaxOffer: a.currentMaxOffer });
});

// PUT /auctions/:id/finalize  → Finalizar subasta y asignar ganador
app.put("/auctions/:id/finalize", (req, res) => {
	const id = parseInt(req.params.id);
	const a = auctions.find((x) => x.id === id);
	if (!a) {
		return res.status(404).json({ message: "Subasta no encontrada" });
	}
	if (a.isFinished) {
		return res.status(400).json({ message: "La subasta ya está finalizada" });
	}
	const { winnerNumber } = req.body;
	const wn = Number(winnerNumber);
	if (isNaN(wn) || wn < 0 || wn > 99) {
		return res.status(400).json({ message: "winnerNumber inválido" });
	}
	const row = Math.floor(wn / 10);
	const col = wn % 10;
	if (a.matrix[row][col] !== 1) {
		return res
			.status(400)
			.json({ message: "El número ganador no fue reservado" });
	}
	a.isFinished = true;
	a.winnerNumber = wn;
	a.updatedAt = new Date().toISOString();
	return res.json({ message: "Subasta finalizada", winnerNumber: wn });
});

// DELETE /auctions/:id  → Eliminar subasta
app.delete("/auctions/:id", (req, res) => {
	const id = parseInt(req.params.id);
	const idx = auctions.findIndex((x) => x.id === id);
	if (idx === -1) {
		return res.status(404).json({ message: "Subasta no encontrada" });
	}
	auctions.splice(idx, 1);
	return res.json({ message: "Subasta eliminada" });
});

// PUT /auctions/:id  → Editar nombre o fecha antes de finalizar
app.put("/auctions/:id", (req, res) => {
	const id = parseInt(req.params.id);
	const a = auctions.find((x) => x.id === id);
	if (!a) {
		return res.status(404).json({ message: "Subasta no encontrada" });
	}
	if (a.isFinished) {
		return res
			.status(400)
			.json({ message: "No se puede editar una subasta finalizada" });
	}
	const { name, date } = req.body;
	if (typeof name !== "string" || typeof date !== "string") {
		return res.status(400).json({ message: "Campos inválidos: name o date" });
	}
	a.name = name;
	a.date = date;
	a.updatedAt = new Date().toISOString();
	return res.json({ message: "Subasta actualizada" });
});

// ------------------------------
// Iniciar servidor
// ------------------------------
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
	console.log(`✅ API corriendo en http://localhost:${PORT}`);
});
