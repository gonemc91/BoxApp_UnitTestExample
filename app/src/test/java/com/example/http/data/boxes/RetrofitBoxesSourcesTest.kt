package com.example.http.data.boxes

import android.graphics.Color
import com.example.http.data.base.RetrofitConfig
import com.example.http.data.boxes.entities.GetBoxesResponseEntity
import com.example.http.data.boxes.entities.UpdateBoxRequestEntity
import com.example.http.domain.AppException
import com.example.http.domain.BackendException
import com.example.http.domain.ConnectionException
import com.example.http.domain.ParseBackendResponseException
import com.example.http.domain.boxes.entities.Box
import com.example.http.domain.boxes.entities.BoxAndSettings
import com.example.http.domain.boxes.entities.BoxesFilter
import com.example.http.testutils.arranged
import com.example.http.testutils.catch
import com.example.http.testutils.wellDone
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.JsonEncodingException
import com.squareup.moshi.Moshi
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit4.MockKRule
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException


@ExperimentalCoroutinesApi
class RetrofitBoxesSourcesTest {


     @get: Rule
     val rule = MockKRule(this)


    @RelaxedMockK
    lateinit var boxesApi: BoxesApi


    private lateinit var retrofitBoxesSources: RetrofitBoxesSources

    @Before
    fun setUp(){
        retrofitBoxesSources = createRetrofitBoxesSources()
    }


    @Test
    fun getBoxesWithOnlyBoxesFilterInvokeEndpoint() = runTest{
        val filter = BoxesFilter.ONLY_ACTIVE

        retrofitBoxesSources.getBoxes(filter)

        coVerify(exactly = 1) {
            boxesApi.getBoxes(true)
        }
        confirmVerified(boxesApi)

    }

    @Test
    fun getBoxesWithAllFilterInvokeEndPoint() = runTest {
        val filter = BoxesFilter.ALL

        retrofitBoxesSources.getBoxes(filter)

        coVerify(exactly = 1) {
            boxesApi.getBoxes(isNull())
        }
        confirmVerified(boxesApi)
    }

    @Test
    fun getBoxesReturnBoxesFromEndpoint() = runTest {
        val expectedBox1 = BoxAndSettings(
            box = Box(id = 1, colorName = "Red", colorValue = Color.RED),
            isActive = true
        )
        val expectedBox2 = BoxAndSettings(
            box = Box(id = 3, colorName = "Green", colorValue = Color.GREEN),
            isActive = true
        )

        val box1Response = mockk<GetBoxesResponseEntity>()
        val box2Response = mockk<GetBoxesResponseEntity>()
        every { box1Response.toBoxAndSettings() } returns expectedBox1
        every { box2Response.toBoxAndSettings() } returns expectedBox2

        coEvery { boxesApi.getBoxes(any()) } returns listOf(box1Response, box2Response)

        val boxes = retrofitBoxesSources.getBoxes(BoxesFilter.ALL)

        assertEquals(boxes.size, 2)
        assertEquals(boxes[0], expectedBox1)
        assertEquals(boxes[1], expectedBox2)
    }

    @Test
    fun getBoxesWithAppExceptionRethrowsException() = runTest {
        val expectedException = AppException()
        coEvery { boxesApi.getBoxes(any()) } throws expectedException

        val exception: AppException = catch {
            retrofitBoxesSources.getBoxes(BoxesFilter.ALL)
        }

        assertSame(expectedException, exception)
    }

    @Test
    fun getBoxesWithJsonDataExceptionThrowsParseBackendResponseException() = runTest {
        coEvery { boxesApi.getBoxes(any()) } throws JsonDataException()

        catch<ParseBackendResponseException> {
            retrofitBoxesSources.getBoxes(BoxesFilter.ALL)
        }

        wellDone()
    }

    @Test
    fun getBoxesWithJsonEncodingExceptionThrowsParseBackendResponseException()= runTest {
        coEvery { boxesApi.getBoxes(any()) } throws  JsonEncodingException("Oops")

        catch<ParseBackendResponseException> {
            retrofitBoxesSources.getBoxes(BoxesFilter.ALL)
        }

        wellDone()
    }

    @Test
    fun getBoxesWithIOExceptionThrowsConnectionException() = runTest {
        coEvery { boxesApi.getBoxes(any()) } throws IOException()

        catch<ConnectionException> {
            retrofitBoxesSources.getBoxes(BoxesFilter.ALL)
        }

        wellDone()
    }


    @Test
    fun getBoxesWithHttpExceptionThrowsBackendException() = runTest {
        val httpException = mockk<HttpException>()
        val response = mockk<Response<*>>()
        val errorBody = mockk<ResponseBody>()
        val errorJson = "{\"error\": \"Oops\"}"
        coEvery { boxesApi.getBoxes(any()) } throws httpException
        every { httpException.response() } returns response
        every { httpException.code() } returns 409
        every { response.errorBody() } returns errorBody
        every { errorBody.string() } returns errorJson

        val exception: BackendException = catch {
            retrofitBoxesSources.getBoxes(BoxesFilter.ALL)
        }

        assertEquals("Oops", exception.message)
        assertEquals(409, exception.code)
    }

    @Test
    fun setIsActiveCallsEndPoint() = runTest {
        arranged()

        retrofitBoxesSources.setIsActive(2, true)

        coVerify(exactly = 1) {
            boxesApi.setIsActive(2, UpdateBoxRequestEntity(true))
        }
        confirmVerified(boxesApi)
    }

    @Test
    fun setIsActiveWithAppExceptionRethrowsException() = runTest{
        val expectedException = AppException()
        coEvery { boxesApi.setIsActive(any(), any()) } throws expectedException

        val exception: AppException = catch {
            retrofitBoxesSources.setIsActive(1, true)
        }
        assertSame(expectedException, exception)
    }

    @Test
    fun setIsActiveWithJsonDataExceptionThrowParseBackendResponseException() = runTest {
        coEvery { boxesApi.setIsActive(any(),any()) } throws JsonDataException()

        catch<ParseBackendResponseException> {
            retrofitBoxesSources.setIsActive(1, true)
        }

        wellDone()
    }

    @Test
    fun setIsActiveWithJsonEncodingExceptionThrowParseBackendResponseException() = runTest {
        coEvery { boxesApi.setIsActive(any(),any()) } throws JsonEncodingException("Oops")

        catch<ParseBackendResponseException> {
            retrofitBoxesSources.setIsActive(1, true)
        }

        wellDone()
    }


    @Test
    fun setIsActiveWithHttpExceptionThrowsBackendException() = runTest {
        val httpException = mockk<HttpException>()
        val response = mockk<Response<*>>()
        val errorBody = mockk<ResponseBody>()
        val errorJson = "{\"error\": \"Oops\"}"
        coEvery { boxesApi.setIsActive(any(),any()) } throws httpException
        every { httpException.response() } returns response
        every { httpException.code() } returns 409
        every { response.errorBody() } returns errorBody
        every { errorBody.string() } returns errorJson

        val exception: BackendException = catch {
            retrofitBoxesSources.setIsActive(1, true)
        }

        assertEquals("Oops", exception.message)
        assertEquals(409, exception.code)
    }

//---------------------------------------------------------------
    private fun createRetrofitBoxesSources(
        retrofit: Retrofit = createRetrofit(),
        moshi: Moshi = createMoshi()): RetrofitBoxesSources{
        val config = RetrofitConfig(
            retrofit = retrofit,
            moshi = moshi
        )
        return RetrofitBoxesSources(config)
    }

    private fun createRetrofit(): Retrofit{
        val retrofit = mockk<Retrofit>()
        every { retrofit.create(BoxesApi::class.java) } returns boxesApi
        return retrofit
    }

    private fun createMoshi():Moshi {
       return Moshi.Builder().build()
    }

    
}