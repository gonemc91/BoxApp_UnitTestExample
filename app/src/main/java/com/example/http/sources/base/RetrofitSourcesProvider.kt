package com.example.http.sources.base

import com.example.http.app.model.SourcesProvider
import com.example.http.app.model.accounts.AccountsSources
import com.example.http.app.model.boxes.BoxesSource
import com.example.http.sources.accounts.RetrofitAccountSource
import com.example.http.sources.boxes.RetrofitBoxesSources


class RetrofitSourcesProvider (
    private val config: RetrofitConfig
):SourcesProvider {

    override fun getAccountsSource(): AccountsSources {
        return RetrofitAccountSource(config)
    }

    override fun getBoxesSources(): BoxesSource {
      return RetrofitBoxesSources(config)
    }
}