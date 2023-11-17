package com.example.http.data.boxes.entities

import android.graphics.Color
import com.example.http.domain.boxes.entities.Box
import com.example.http.domain.boxes.entities.BoxAndSettings
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GetBoxResponseEntityTest {

    @Before
    fun setUp(){
        mockkStatic(Color::class)
    }

    @After
    fun tearDown(){
        unmockkStatic(Color::class)
    }


    @Test
    fun toBoxMapsToInAppEntity(){
        val responseEntity = GetBoxesResponseEntity(
            id = 2,
            colorName = "Red",
            colorValue = "#ff0000",
            isActive = true)
        every { Color.parseColor(any())}  returns Color.RED

        val inAppEntity = responseEntity.toBoxAndSettings()

        val expectedAppEntity = BoxAndSettings(
            box = Box(id= 2, colorName="Red",colorValue = Color.RED),
            isActive = true
        )

        assertEquals(expectedAppEntity, inAppEntity)

    }

}