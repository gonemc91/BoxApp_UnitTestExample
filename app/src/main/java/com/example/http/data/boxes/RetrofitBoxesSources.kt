package com.example.http.data.boxes

import com.example.http.data.base.BaseRetrofitSource
import com.example.http.data.base.RetrofitConfig
import com.example.http.data.boxes.entities.UpdateBoxRequestEntity
import com.example.http.domain.boxes.BoxesSource
import com.example.http.domain.boxes.entities.BoxAndSettings
import com.example.http.domain.boxes.entities.BoxesFilter
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class RetrofitBoxesSources @Inject constructor(
    config: RetrofitConfig
): BaseRetrofitSource(config), BoxesSource {

    private val boxesApi = retrofit.create(BoxesApi::class.java)

    override suspend fun setIsActive(
        boxId: Long,
        isActive: Boolean
    ) = wrapRetrofitExceptions{
        val updateBoxRequestEntity = UpdateBoxRequestEntity(
           isActive = isActive
        )
        boxesApi.setIsActive(boxId, updateBoxRequestEntity)
    }

    override suspend fun getBoxes(
        boxesFilter: BoxesFilter
    ):
            List<BoxAndSettings> = wrapRetrofitExceptions {
        delay(500)
        val isActive: Boolean? = if(boxesFilter == BoxesFilter.ONLY_ACTIVE)
            true
        else
            null
        boxesApi.getBoxes(isActive)
            .map { it.toBoxAndSettings() }
    }
}