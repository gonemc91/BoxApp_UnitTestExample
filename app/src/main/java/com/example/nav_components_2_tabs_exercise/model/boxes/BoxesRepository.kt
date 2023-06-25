package com.example.nav_components_2_tabs_exercise.model.boxes

import com.example.nav_components_2_tabs_exercise.model.boxes.entities.Box
import com.example.nav_components_2_tabs_exercise.model.boxes.entities.BoxAndSettings
import kotlinx.coroutines.flow.Flow

interface BoxesRepository {

    /**
     * Get the list of boxes.
     * @param onlyActive if set to 'true' then only active boxes are emitted.
     */

    suspend fun getBoxesAndSettings(onlyActive: Boolean = false): Flow<List<BoxAndSettings>>

    /**
     * Mark the specified box as active. Only active boxes are displayed in dashboard screen.
     * @throws StorageExeption
     */

    suspend fun activateBox(box: Box)

    /**
     * Mark the specified box as inactive. Inactive boxes are not displayed in dashboard screen.
     * @throws StorageExeption
     */

    suspend fun deactivateBox(box: Box)

}