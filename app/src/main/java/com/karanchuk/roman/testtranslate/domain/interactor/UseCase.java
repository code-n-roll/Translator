package com.karanchuk.roman.testtranslate.domain.interactor;

import com.karanchuk.roman.testtranslate.domain.executor.PostExecutionThread;
import com.karanchuk.roman.testtranslate.domain.executor.ThreadExecutor;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by roman on 28.6.17.
 */

public abstract class UseCase<T, Param1, Param2, Param3> {
    private final ThreadExecutor mThreadExecutor;
    private final PostExecutionThread mPostExecutionThread;
    private final CompositeDisposable mDisposables;


    public UseCase(ThreadExecutor threadExecutor,
                   PostExecutionThread postExecutionThread) {
        mThreadExecutor = threadExecutor;
        mPostExecutionThread = postExecutionThread;
        mDisposables = new CompositeDisposable();
    }

    abstract Observable<T> buildUseCaseObservable(Param1 param1, Param2 param2, Param3 param3);

    public void execute(DisposableObserver<T> observer, Param1 param1, Param2 param2, Param3 param3){
        final Observable<T> observable = buildUseCaseObservable(param1, param2, param3)
                .subscribeOn(Schedulers.from(mThreadExecutor))
                .observeOn(mPostExecutionThread.getScheduler());
        addDisposable(observable.subscribeWith(observer));
    }

    private void addDisposable(Disposable disposable){
        mDisposables.add(disposable);
    }
}
