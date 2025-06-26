import com.example.rifas.data.model.AuctionSummary
import com.example.rifas.data.repository.AuctionRepository
import com.example.rifas.presentation.viewmodel.AuctionViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class AuctionViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var repository: AuctionRepository
    private lateinit var viewModel: AuctionViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher) // 👈 Importante
        repository = mock()
        viewModel = AuctionViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // 👈 Limpieza
    }

    @Test
    fun `loadAuctions on success emits list to auctionList`() = runTest {
        val dummyAuctions = listOf(
            AuctionSummary(1, "Test 1", "2025-06-24", 100, 2, null, false, null),
            AuctionSummary(2, "Test 2", "2025-07-01", 200, 5, null, true, 42)
        )

        whenever(repository.getAuctions(null)).thenReturn(Result.success(dummyAuctions))

        viewModel.loadAuctions(null)

        advanceUntilIdle() // Espera a que terminen las corutinas

        assertEquals(dummyAuctions, viewModel.auctionList.value)
    }

    @Test
    fun `loadAuctions on failure leaves auctionList empty`() = runTest {
        whenever(repository.getAuctions(null)).thenReturn(Result.failure(Exception("fail")))

        viewModel.loadAuctions(null)

        advanceUntilIdle()

        assertEquals(emptyList<AuctionSummary>(), viewModel.auctionList.value)
    }
}
