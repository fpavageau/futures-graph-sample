/*
 * AkkaAsynchronousGraph.java
 *
 * Copyright (c) 2012 Ekino
 *
 * $Id$
 */
package com.ekino.fgs;

import java.util.Arrays;

import akka.dispatch.Await;
import akka.dispatch.ExecutionContext;
import akka.dispatch.ExecutionContexts;
import akka.dispatch.Future;
import akka.dispatch.Futures;
import akka.dispatch.Mapper;
import akka.util.Duration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation based on Akka.
 *
 * @author Frank Pavageau
 * @version $Id$
 */
public class AkkaAsynchronousGraph extends AsynchronousGraph {
    private static final Logger LOGGER = LoggerFactory.getLogger(AkkaAsynchronousGraph.class);
    private static final ExecutionContext DEFAULT_CONTEXT =
            ExecutionContexts.fromExecutorService(DEFAULT_EXECUTOR);
    private static final ExecutionContext DEDICATED_CONTEXT =
            ExecutionContexts.fromExecutorService(DEDICATED_EXECUTOR);


    public AkkaAsynchronousGraph(Service service1, Service service2, Service service3, Service service4) {
        super(service1, service2, service3, service4);
    }


    @Override
    public Status call() {
        Future<Void> future1 = Futures.future(new ServiceCallable(service1), DEFAULT_CONTEXT);
        Future<Void> future3 = Futures.future(new ServiceCallable(service3), DEFAULT_CONTEXT);
        Future<Void> future2 = future1.flatMap(new ChainingMapper<Void>(service2, DEDICATED_CONTEXT));
        @SuppressWarnings("unchecked") Future<Iterable<Void>> barrier =
                Futures.sequence(Arrays.asList(future2, future3), DEFAULT_CONTEXT);
        Future<Void> future4 = barrier.flatMap(new ChainingMapper<Iterable<Void>>(service4));

        try {
            Await.result(future4, Duration.Inf());
            return Status.OK;
        } catch (Exception e) {
            LOGGER.debug("Execution failed", e);
            return Status.ABORTED;
        }
    }


    /**
     * Chains a {@link Service} after another.
     */
    private static class ChainingMapper<I> extends Mapper<I, Future<Void>> {
        private final Service service;
        private final ExecutionContext context;


        public ChainingMapper(Service service) {
            this(service, DEFAULT_CONTEXT);
        }


        public ChainingMapper(Service service, ExecutionContext context) {
            this.service = service;
            this.context = context;
        }


        @Override
        public Future<Void> apply(I input) {
            return Futures.future(new ServiceCallable(service), context);
        }
    }
}
