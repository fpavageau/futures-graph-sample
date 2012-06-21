/*
 * GuavaAsynchronousGraph.java
 *
 * Copyright (c) 2012 Ekino
 *
 * $Id$
 */
package com.ekino.fgs;

import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation based on Guava's concurrent classes.
 *
 * @author Frank Pavageau
 * @version $Id$
 */
public class GuavaAsynchronousGraph extends AsynchronousGraph {
    private static final Logger LOGGER = LoggerFactory.getLogger(GuavaAsynchronousGraph.class);


    public GuavaAsynchronousGraph(Service service1, Service service2, Service service3,
                                  Service service4) {
        super(service1, service2, service3, service4);
    }


    @Override
    public Status call() {
        ListenableFuture<Void> future1 = DEFAULT_EXECUTOR.submit(new ServiceCallable(service1));
        ListenableFuture<Void> future3 = DEFAULT_EXECUTOR.submit(new ServiceCallable(service3));
        ListenableFuture<Void> future2 = Futures.transform(future1,
                new ChainingAsyncFunction<Void>(service2, DEDICATED_EXECUTOR));
        @SuppressWarnings("unchecked") ListenableFuture<List<Void>> barrier = Futures.allAsList(future2, future3);
        ListenableFuture<Void> future4 = Futures.transform(barrier, new ChainingAsyncFunction<List<Void>>(service4));

        try {
            future4.get();
            return Status.OK;
        } catch (InterruptedException e) {
            LOGGER.debug("Execution interrupted", e);
            Thread.currentThread().interrupt();
            return Status.INTERRUPTED;
        } catch (ExecutionException e) {
            LOGGER.debug("Execution aborted", e);
            return Status.ABORTED;
        }
    }


    /**
     * Chains a {@link Service} after another.
     */
    private static class ChainingAsyncFunction<I> implements AsyncFunction<I, Void> {
        private final Service service;
        private final ListeningExecutorService executor;


        public ChainingAsyncFunction(Service service) {
            this(service, DEFAULT_EXECUTOR);
        }


        public ChainingAsyncFunction(Service service, ListeningExecutorService executor) {
            this.service = service;
            this.executor = executor;
        }


        @Override
        public ListenableFuture<Void> apply(I input)
                throws Exception {
            return executor.submit(new ServiceCallable(service));
        }
    }
}
