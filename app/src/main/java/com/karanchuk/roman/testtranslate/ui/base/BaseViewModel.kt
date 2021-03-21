package com.karanchuk.roman.testtranslate.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

open class BaseViewModel : ViewModel()

fun BaseViewModel.launchOnDefault(action : suspend CoroutineScope.() -> Unit) =
    viewModelScope.launch { withContext(Dispatchers.Default) { action() } }

fun BaseViewModel.launchOnIO(action : suspend CoroutineScope.() -> Unit) =
    viewModelScope.launch { withContext(Dispatchers.IO) { action() } }

fun BaseViewModel.launchOnMain(action : suspend CoroutineScope.() -> Unit) =
    viewModelScope.launch { withContext(Dispatchers.Main) { action() } }

suspend fun <T> BaseViewModel.switchToUi(action : suspend CoroutineScope.() -> T) =
    withContext(Dispatchers.Main) { action() }

suspend fun <T> BaseViewModel.switchToIO(action : suspend CoroutineScope.() -> T) =
    withContext(Dispatchers.IO) { action() }

suspend fun <T> BaseViewModel.switchToDefault(action : suspend CoroutineScope.() -> T) =
    withContext(Dispatchers.Default) { action() }