package com.karanchuk.roman.testtranslate.domain.executor;

import io.reactivex.Scheduler;

/**
 * Created by roman on 28.6.17.
 */

public interface PostExecutionThread {
    Scheduler getScheduler();
}
