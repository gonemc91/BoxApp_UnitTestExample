package com.example.http.app.model

import com.example.http.app.model.accounts.AccountsSources
import com.example.http.app.model.boxes.BoxesSource

/**
 * Factory class for all network sources.
 *
 */

interface SourcesProvider {

    /**
     * Create [AccountSources] which is responsible for reading/writing
     * user accounts data.
     *
     */

    fun getAccountsSource(): AccountsSources

    /**
     * Create [BoxesSource] which is responsible for reading/updating
     * boxes data.
     */

    fun getBoxesSources(): BoxesSource


}