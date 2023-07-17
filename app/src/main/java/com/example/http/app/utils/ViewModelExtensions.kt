package com.example.http.app.utils

import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ScrollView
import androidx.core.view.children
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.http.app.Result
import com.example.http.app.Success
import com.example.http.app.screens.base.BaseFragment
import com.example.http.app.views.ResultView

fun <T> LiveData<T>.requireValue(): T{
    return this.value ?: throw java.lang.IllegalStateException("Value is empty")
}


fun <T> LiveData<Result<T>>.observeResults(
    fragment: BaseFragment,
    root: View,
    resultView: ResultView,
    onSuccess: (T) -> Unit){
    observe(fragment.viewLifecycleOwner){result->
        resultView.setResult(fragment, result)
        val rootView: View = if (root is ScrollView)
            root.getChildAt(0)
        else
            root
        if (rootView is ViewGroup && rootView !is RecyclerView && rootView !is AbsListView) {
            rootView.children
                .filter { it != resultView }
                .forEach {
                    it.isVisible = result is Success<*>
                }
        }
        if (result is Success) onSuccess.invoke(result.value)

    }
}


