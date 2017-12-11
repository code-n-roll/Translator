package com.karanchuk.roman.testtranslate.common.yandexapi;

import com.karanchuk.roman.testtranslate.common.model.DictDefinition;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by roman on 17.6.17.
 */

public interface DictionaryService {
    String API_BASE_URL_DICTIONARY = "https://dictionary.yandex.net/";

    @GET("api/v1/dicservice.json/lookup?")
    Observable<DictDefinition> fetchDictDefinition(@Query("key") String key,
                                                   @Query("text") String text,
                                                   @Query("lang") String lang);
}
