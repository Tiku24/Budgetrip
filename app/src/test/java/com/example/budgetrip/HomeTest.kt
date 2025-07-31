package com.example.budgetrip


import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.budgetrip.data.model.AddTripRequest
import com.example.budgetrip.data.model.AddTripResponse
import com.example.budgetrip.data.model.BudgetripResponse
import com.example.budgetrip.data.model.BudgetripResponseItem
import com.example.budgetrip.data.model.UpdateTripRequest
import com.example.budgetrip.data.model.UpdateTripResponse
import com.example.budgetrip.data.network.BudgetripApi
import com.example.budgetrip.data.network.ResultResource
import com.example.budgetrip.data.repository.HomeRepository
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import retrofit2.Response

@Config(manifest = Config.NONE)
@RunWith(AndroidJUnit4::class)
class HomeTest {

    val fakeApi = FakeBudgetApi()
    var repo = HomeRepository(fakeApi)

    @Before
    fun setUp(){
        val fakeApi = FakeBudgetApi()
        repo = HomeRepository(fakeApi)
    }

    @Test
    fun test_getTrips_success() = runBlocking {
        val trip = AddTripRequest(name = "Test trip", destination = "Test destination", totalBudget = 1000.0, spentAmount = 500.0, category = "Test category", startDate = "2023-01-01", endDate = "2023-01-02", notes = "Test notes")
        repo.addTrip(trip)
        val res = repo.getTrips()
        val expected = ResultResource.Success(
            BudgetripResponse().apply {
                add(
                    BudgetripResponseItem(
                        id = "1",
                        name = "Test trip",
                        destination = "Test destination",
                        totalBudget = 1000,
                        spentAmount = 500,
                        category = "Test category",
                        startDate = "2023-01-01",
                        endDate = "2023-01-02",
                        notes = "Test notes"
                    )
                )
            }
        )
        assertEquals(expected, res)
    }
}


class FakeBudgetApi: BudgetripApi {
    private val trips = mutableListOf<BudgetripResponseItem>()
    override suspend fun getTrips(): Response<BudgetripResponse> {
        val res = BudgetripResponse()
        res.addAll(trips)
        return Response.success(res)
    }

    override suspend fun addTrips(addTripRequest: AddTripRequest): Response<AddTripResponse> {
        val item  = BudgetripResponseItem(
            name = addTripRequest.name,
            destination = addTripRequest.destination,
            endDate = addTripRequest.endDate,
            id = (trips.size + 1).toString(),
            notes = addTripRequest.notes,
            spentAmount = addTripRequest.spentAmount.toInt(),
            startDate = addTripRequest.startDate,
            totalBudget = addTripRequest.totalBudget.toInt(),
            category = addTripRequest.category
        )
        trips.add(item)
        return Response.success(AddTripResponse(
            name = addTripRequest.name,
            destination = addTripRequest.destination,
            endDate = addTripRequest.endDate,
            id = (trips.size + 1).toString(),
            notes = addTripRequest.notes,
            spentAmount = addTripRequest.spentAmount,
            startDate = addTripRequest.startDate
            ,totalBudget = addTripRequest.totalBudget,
            category = addTripRequest.category
        ))
    }

    override suspend fun updateTrip(
        id: String,
        updateTripRequest: UpdateTripRequest
    ): Response<UpdateTripResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteTrip(id: String): Response<Unit> {
        TODO("Not yet implemented")
    }

}