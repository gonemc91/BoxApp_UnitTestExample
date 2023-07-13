package com.example.http.app.model.boxes

import com.example.http.app.model.boxes.entities.Box
import com.example.http.app.model.boxes.entities.BoxAndSettings
import com.example.http.app.model.boxes.entities.BoxesFilter
import kotlinx.coroutines.flow.Flow

interface BoxesSource {

    /**
     * Get the list of all boxes for the current logged-in user.
     * @throws BackendException
     * @throws ConnectionException
     * @throws ParseBackendResponseException
     */

    suspend fun getBoxes(boxesFilter: BoxesFilter): List<BoxAndSettings>

    /**
     * Set isActive flag for the specified box.
     * @throws BackendException
     * @throws ConnectionException
     * @throws ParseBackendResponseException
     */

    suspend fun setIsActive(boxId: Long, isActive: Boolean)

}