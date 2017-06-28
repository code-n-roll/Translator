package com.karanchuk.roman.testtranslate;

import android.content.Context;

import com.google.gson.JsonObject;
import com.karanchuk.roman.testtranslate.utils.JsonUtils;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by roman on 27.6.17.
 */

@RunWith(MockitoJUnitRunner.class)
public class JsonUtilsUnitTest {
    @Mock
    Context mMockContext;

    @Mock
    String mFileName;

    @Test
    public void readJsonObjectFromAssetsFileTest(){
        String filename = "test_yandex_api_response.json";
        try {
            InputStream is = new FileInputStream(filename);
            when(mMockContext.getAssets().open(mFileName)).thenReturn(is);
        } catch (IOException e){
            e.printStackTrace();
        }
        JsonObject result = JsonUtils.getJsonObjectFromAssetsFile(
                mMockContext, "test_yandex_api_response.json");
        JsonObject mustBeResult = new JsonObject();

        JsonObject weight = new JsonObject();
        JsonObject height = new JsonObject();
        JsonObject firstname = new JsonObject();
        JsonObject lastname = new JsonObject();
        JsonObject age = new JsonObject();
        weight.addProperty("weight", "62");
        height.addProperty("height", "180");
        firstname.addProperty("firstname", "roman");
        lastname.addProperty("lastname", "karanchuk");
        age.addProperty("age", "20");

        mustBeResult.add("user", weight);
        mustBeResult.add("user", height);
        mustBeResult.add("user", age);
        mustBeResult.add("user", firstname);
        mustBeResult.add("user", lastname);
        assertThat(result, is(mustBeResult));
    }
}
