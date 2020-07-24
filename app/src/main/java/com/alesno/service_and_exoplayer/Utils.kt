package com.alesno.service_and_exoplayer

fun <T> uiLazy(initializer: () -> T) = lazy(LazyThreadSafetyMode.NONE) { initializer() }
