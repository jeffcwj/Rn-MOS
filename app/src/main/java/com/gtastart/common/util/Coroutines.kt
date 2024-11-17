package com.gtastart.common.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

object Coroutines {

    /**
     * 在后台线程中执行一个异步操作，然后在主线程中处理结果
     * 
     */
    fun<T: Any> ioThenMain(work: suspend (() -> T?), callback: ((T?) -> Unit)) =
        CoroutineScope(Dispatchers.Main).launch {
            val data = CoroutineScope(Dispatchers.IO).async rt@{
                return@rt work()
            }.await()
            callback(data)
        }

    /**
     * 在主线程中执行一个挂起函数
     */
    fun main(work: suspend (() -> Unit)) = CoroutineScope(Dispatchers.Main).launch {
        work()
    }

}