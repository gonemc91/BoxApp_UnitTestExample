package com.example.http.sources.base

import com.example.http.app.model.SourcesProvider
import com.example.http.app.model.accounts.AccountsSources
import com.example.http.app.model.boxes.BoxesSource
import com.example.http.sources.accounts.OkHttpAccountSource
import com.example.http.sources.boxes.OkHttpBoxesSource

class OkHttpSourcesProvider(
    private val config: OkHttpConfig
): SourcesProvider {

    override fun getAccountsSource(): AccountsSources {
        return OkHttpAccountSource(config)
    }

    override fun getBoxesSources(): BoxesSource {
        return OkHttpBoxesSource(config)
    }


}