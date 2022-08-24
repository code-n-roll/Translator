package com.romankaranchuk.translator.data.database.storage;

import java.util.Map;


public interface TextDataStorage {
    void saveToSharedPreferences(Map<String, Object> savedData);
}
