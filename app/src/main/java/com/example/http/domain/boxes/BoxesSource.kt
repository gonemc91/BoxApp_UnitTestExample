package com.example.http.domain.boxes

import com.example.http.domain.boxes.entities.BoxAndSettings
import com.example.http.domain.boxes.entities.BoxesFilter

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