package com.karanchuk.roman.testtranslate.utils;

/**
 * Created by roman on 24.4.17.
 */

public final class Assertion {

    private Assertion(){}

    public static void nonNull(final Object... objects){
        for (int i = 0; i < objects.length; ++i){
            if (objects[i] == null){
                throw new NullPointerException("object "+objects[i].toString()+" in position = "+i+" is null.");
            }
        }
    }
}
