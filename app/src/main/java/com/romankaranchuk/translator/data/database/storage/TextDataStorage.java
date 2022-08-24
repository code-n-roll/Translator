package com.romankaranchuk.translator.data.database.storage;

import java.util.Map;

/**
 * Created by roman on 22.6.17.
 */

public interface TextDataStorage {
    void saveToSharedPreferences(Map<String, Object> savedData);
}
