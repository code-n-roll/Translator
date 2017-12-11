package com.karanchuk.roman.testtranslate.common.yandexapi;

//import com.karanchuk.roman.testtranslate.common.model.DictDefinition;

import com.karanchuk.roman.testtranslate.common.model.TranslationResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by roman on 15.6.17.
 */

public interface TranslatorService {
    String API_BASE_URL_TRANSLATOR = "https://translate.yandex.net/";

    @GET("api/v1.5/tr.json/translate?")
    Observable<TranslationResponse> fetchTranslation(@Query("key") String key,
                                                     @Query("text") String text,
                                                     @Query("lang") String lang);
}
