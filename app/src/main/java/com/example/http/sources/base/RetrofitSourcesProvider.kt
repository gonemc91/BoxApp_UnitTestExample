package com.example.http.sources.base

import com.example.http.app.model.SourcesProvider
import com.example.http.app.model.accounts.AccountsSources
import com.example.http.app.model.boxes.BoxesSource

//todo #9 create AccountSources and BoxesSource.
class RetrofitSourcesProvider (
    private val config: RetrofitConfig
):SourcesProvider {

    override fun getAccountsSource(): AccountsSources {
        TODO("Not yet implemented")
    }

    override fun getBoxesSources(): BoxesSource {
        TODO("Not yet implemented")
    }
}