package com.karanchuk.roman.testtranslate.data.network;

import com.karanchuk.roman.testtranslate.data.database.model.TranslationResponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by roman on 15.6.17.
 */

public interface YandexTranslateApi {

    @GET("api/v1.5/tr.json/translate?")
    Single<TranslationResponse> getTranslation(@Query("key") String key,
                                               @Query("text") String text,
                                               @Query("lang") String lang);
}
