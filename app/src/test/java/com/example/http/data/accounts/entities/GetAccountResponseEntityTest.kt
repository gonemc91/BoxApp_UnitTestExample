package com.example.http.data.accounts.entities

import com.example.http.domain.accounts.entities.Account
import org.junit.Assert.assertEquals
import org.junit.Test

class GetAccountResponseEntityTest {


    @Test
    fun toAccountMapsToInAppEntity(){
        val responseEntity = GetAccountResponseEntity(
            id = 3,
            email = "some-email",
            username = "some-email",
            createdAt = 123
        )

        val inAppEntity = responseEntity.toAccount()


        val expectedInAppEntity = Account(
            id = 3,
            email = "some-email",
            username = "some-email",
            createdAt = 123
        )
        assertEquals(expectedInAppEntity, inAppEntity)
    }


}
