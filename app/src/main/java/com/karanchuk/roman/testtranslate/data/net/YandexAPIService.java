package com.karanchuk.roman.testtranslate.data.net;

//import com.karanchuk.roman.testtranslate.presentation.model.DictDefinition;

import com.karanchuk.roman.testtranslate.presentation.model.DictDefinition;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by roman on 15.6.17.
 */

public interface YandexAPIService {
    String API_BASE_URL_DICTIONARY = "https://dictionary.yandex.net/";
    String API_BASE_URL_TRANSLATOR = "https://translate.yandex.net/";

    @GET("api/v1.5/tr.json/translate?key={key}&text={text}&lang={lang}")
    Observable<String> getTranslation(@Query("key") String key,
                                      @Query("text") String text,
                                      @Query("lang") String lang);

    @GET("api/v1/dicservice.json/lookup?key={key}&text={text}&lang={lang}")
    Observable<DictDefinition> getDictDefinition(@Query("key") String key,
                                                 @Query("text") String text,
                                                 @Query("lang") String lang);
}
