package com.rxjavaimpl;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;

public class HelloWorld {

    public static void main(String[] args) {
        Flowable.just("Hello World", "sdds").subscribe(System.out::println);
    }
}
