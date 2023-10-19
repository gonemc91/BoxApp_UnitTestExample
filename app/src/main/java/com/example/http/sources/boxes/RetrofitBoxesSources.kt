package com.example.http.sources.boxes

import com.example.http.app.model.boxes.BoxesSource
import com.example.http.app.model.boxes.entities.BoxAndSettings
import com.example.http.app.model.boxes.entities.BoxesFilter
import com.example.http.sources.base.BaseRetrofitSource
import com.example.http.sources.base.RetrofitConfig
import kotlinx.coroutines.delay

// todo #8: implemented BoxesSource methods:
//          -setIsActive() -> should call 'PUT 'boxes/{boxI}'
//          -getBoxes() -> should call 'GET/boxes[?active=true[false]'
//                                      and return the little of BoxAndSettings entities
class RetrofitBoxesSources(
    config: RetrofitConfig
): BaseRetrofitSource(config), BoxesSource {

    override suspend fun setIsActive(
        boxId: Long,
        isActive: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun getBoxes(
        boxesFilter: BoxesFilter):
            List<BoxAndSettings> = wrapRetrofitExceptions {
        delay(500)
        TODO("Not yet implemented")
    }


}