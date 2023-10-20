package com.example.http.sources.boxes

import com.example.http.sources.boxes.entities.GetBoxesResponseEntity
import com.example.http.sources.boxes.entities.UpdateBoxRequestEntity
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

//todo #6 add 2 methods for making requests related to boxes:
//                - 'GET/boxes[?active=true[false]'
//                - 'PUT/boxes/{boxId}'
//               Hint: use entities located in '*.sources.boxes.entities' package
interface BoxesApi{

    @PUT("boxes/{boxId}")
    suspend fun setIsActive(
        @Path("boxId")boxId: Long,  //argument Path for put in placeholder boxId
        @Body updateBoxRequestEntity: UpdateBoxRequestEntity //body for convert to JSON request
    )

    @GET("boxes")
    suspend fun getBoxes(
        @Query("active") isActive: Boolean? // argument to request true/false/null active/no active/all: box
    ): List<GetBoxesResponseEntity>
}