package com.example.http.sources.boxes

import com.example.http.app.model.boxes.BoxesSource
import com.example.http.app.model.boxes.entities.BoxAndSettings
import com.example.http.app.model.boxes.entities.BoxesFilter
import com.example.http.sources.base.BaseOkHttpSource
import com.example.http.sources.base.OkHttpConfig
import com.example.http.sources.boxes.entities.GetBoxesResponseEntity
import com.example.http.sources.boxes.entities.UpdateBoxRequestEntity
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.delay
import okhttp3.Request

// todo #7: implement methods:
//          - setIsActive() -> for making box active or inactive
//          - getBoxes() -> for fetching boxes data
class OkHttpBoxesSource(
    config: OkHttpConfig
) : BaseOkHttpSource(config), BoxesSource {

    override suspend fun setIsActive(boxId: Long, isActive: Boolean) {
        // Call "PUT /boxes/{boxId}" endpoint.
        // Use UpdateBoxRequestEntity.
        val updateBoxRequestEntity = UpdateBoxRequestEntity(isActive)
        val request = Request.Builder()
            .put(updateBoxRequestEntity.toJsonRequestBody())
            .endpoint("/boxes/${boxId}")
            .build()
        val call = client.newCall(request)
        call.suspendEnqueue()
    }

    override suspend fun getBoxes(boxesFilter: BoxesFilter): List<BoxAndSettings> {
        delay(500)
        // Call "GET /boxes?active=true" if boxesFilter = ONLY_ACTIVE.
        // Call "GET /boxes" if boxesFilter = ALL.
        // Hint: use TypeToken for converting server response into List<GetBoxResponseEntity>
        // Hint: use GetBoxResponseEntity.toBoxAndSettings for mapping GetBoxResponseEntity into BoxAndSettings
        val args = if(boxesFilter == BoxesFilter.ONLY_ACTIVE)
            "?active=true"
        else
            ""
        val request = Request.Builder()
            .get()
            .endpoint("/boxes$args")
            .build()
        val call = client.newCall(request)
        val typeToken = object : TypeToken<List<GetBoxesResponseEntity>>(){} //вывод массива данных через gson
        val response = call.suspendEnqueue().parseJsonResponse(typeToken)
        return response.map { it.toBoxAndSettings() }
    }

}