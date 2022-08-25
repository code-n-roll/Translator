package com.romankaranchuk.translator.common

import com.romankaranchuk.translator.BuildConfig

object EnvironmentHolder {

    const val YANDEX_DICTIONARY_API_KEY = BuildConfig.YANDEX_DICTIONARY_API_KEY_PROD
    const val YANDEX_TRANSLATE_API_KEY = BuildConfig.YANDEX_TRANSLATE_API_KEY_PROD

    const val YANDEX_DICTIONARY_API_BASE_URL = "https://dictionary.yandex.net/"
    const val YANDEX_TRANSLATE_API_BASE_URL = "https://translate.yandex.net/"
}