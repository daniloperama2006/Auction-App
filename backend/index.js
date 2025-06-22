const express = require("express");
const cors = require("cors");
const bodyParser = require("body-parser");

const app = express();
app.use(cors());
app.use(bodyParser.json());

let auctions = [];
let currentId = 1;

// GET all auctions
app.get("/auctions", (req, res) => {
	res.json(auctions);
});

// GET auction by ID
app.get("/auctions/:id", (req, res) => {
	const auction = auctions.find((a) => a.id === parseInt(req.params.id));
	auction ? res.json(auction) : res.status(404).send("Not found");
});

// POST create auction
app.post("/auctions", (req, res) => {
	const { name, date, matrix } = req.body;
	console.log("POST /auctions body:", req.body);

	if (typeof name !== "string" || typeof date !== "string") {
		return res.status(400).json({ message: "Invalid name or date" });
	}
	if (!Array.isArray(matrix) || matrix.some((row) => !Array.isArray(row))) {
		return res
			.status(400)
			.json({ message: "Invalid matrix format: expected 2D array" });
	}
	// opcional: validar dimensiones 10x10
	if (matrix.length !== 10 || matrix.some((row) => row.length !== 10)) {
		return res.status(400).json({ message: "Matrix must be 10x10" });
	}

	const auction = {
		id: currentId++,
		name,
		date,
		matrix,
		winnerNumber: null,
	};
	auctions.push(auction);
	res.status(201).json(auction);
});

// PUT update auction (name, date, matrix)
app.put("/auctions/:id", (req, res) => {
	const id = parseInt(req.params.id);
	const auction = auctions.find((a) => a.id === id);
	if (!auction) {
		return res.status(404).send("Not found");
	}
	const { name, date, matrix } = req.body;
	if (typeof name !== "string" || typeof date !== "string") {
		return res.status(400).json({ message: "Invalid name or date" });
	}
	if (!Array.isArray(matrix) || matrix.some((row) => !Array.isArray(row))) {
		return res.status(400).json({ message: "Invalid matrix format" });
	}
	if (matrix.length !== 10 || matrix.some((row) => row.length !== 10)) {
		return res.status(400).json({ message: "Matrix must be 10x10" });
	}
	auction.name = name;
	auction.date = date;
	auction.matrix = matrix;
	res.json(auction);
});

// PUT assign winner
app.put("/auctions/:id/winner", (req, res) => {
	const id = parseInt(req.params.id);
	const auction = auctions.find((a) => a.id === id);
	if (!auction) {
		return res.status(404).send("Not found");
	}
	const { winnerNumber } = req.body;
	if (
		typeof winnerNumber !== "number" ||
		winnerNumber < 0 ||
		winnerNumber > 99
	) {
		return res.status(400).json({ message: "Invalid winnerNumber" });
	}
	// opcional: validar que matrix[row][col] == 1
	const row = Math.floor(winnerNumber / 10);
	const col = winnerNumber % 10;
	if (!Array.isArray(auction.matrix) || auction.matrix[row][col] !== 1) {
		return res
			.status(400)
			.json({ message: "Winner number not selected in matrix" });
	}
	auction.winnerNumber = winnerNumber;
	res.status(200).json(auction);
});

// Root for health check
app.get("/", (req, res) => {
	res.send("API running. Use /auctions");
});

// Start server
const PORT = 3000;
app.listen(PORT, () => {
	console.log(`✅ API running at http://localhost:${PORT}`);
});
