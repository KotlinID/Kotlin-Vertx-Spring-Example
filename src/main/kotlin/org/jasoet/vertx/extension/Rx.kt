package org.jasoet.vertx.extension

import io.vertx.core.AsyncResult
import io.vertx.core.Handler
import io.vertx.rx.java.ObservableFuture
import io.vertx.rx.java.SingleOnSubscribeAdapter
import rx.Observable
import rx.Single
import rx.singles.BlockingSingle

/**
 * [Documentation Here]
 *
 * @author Deny Prasetyo.
 */

inline fun <T : Any> blockingSingle(crossinline operation: (Handler<AsyncResult<T>>) -> Unit): BlockingSingle<T> {
    return single(operation).toBlocking()
}

inline fun <T : Any> observable(operation: (Handler<AsyncResult<T>>) -> Unit): Observable<T> {
    val future = ObservableFuture<T>()
    operation(future.toHandler())
    return future
}

inline fun <T : Any> single(crossinline operation: (Handler<AsyncResult<T>>) -> Unit): Single<T> {
    return Single.create(SingleOnSubscribeAdapter<T> { fut -> operation(fut) })
}

inline fun <T : Any> observableCall(crossinline operation: () -> T): Observable<T> {
    return Observable.fromCallable {
        operation()
    }
}