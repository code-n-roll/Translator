package com.karanchuk.roman.testtranslate.data.network;

import com.karanchuk.roman.testtranslate.data.database.model.DictDefinition;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by roman on 17.6.17.
 */

public interface YandexDictionaryApi {

    @GET("api/v1/dicservice.json/lookup?")
    Single<DictDefinition> getValueFromDictionary(@Query("key") String key,
                                                  @Query("text") String text,
                                                  @Query("lang") String lang);
}
