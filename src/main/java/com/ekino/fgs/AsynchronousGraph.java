/*
 * AsynchronousGraph.java
 *
 * Copyright (c) 2012 Ekino
 *
 * $Id$
 */
package com.ekino.fgs;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

/**
 * Graph (oriented and acyclic) of asynchronous calls to be executed.
 *
 * @author Frank Pavageau
 * @version $Id$
 */
public abstract class AsynchronousGraph {
    protected static final ListeningExecutorService DEFAULT_EXECUTOR =
            MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(
                    Math.max(Runtime.getRuntime().availableProcessors() - 1, 1),
                    new NamedThreadFactory("default-pool-")));
    protected static final ListeningExecutorService DEDICATED_EXECUTOR = MoreExecutors.listeningDecorator(
            Executors.newSingleThreadExecutor(new NamedThreadFactory("dedicated-pool-")));

    protected final Service service1;
    protected final Service service2;
    protected final Service service3;
    protected final Service service4;


    public AsynchronousGraph(Service service1, Service service2, Service service3, Service service4) {
        this.service1 = Preconditions.checkNotNull(service1);
        this.service2 = Preconditions.checkNotNull(service2);
        this.service3 = Preconditions.checkNotNull(service3);
        this.service4 = Preconditions.checkNotNull(service4);
    }


    /**
     * Creates and executes a graph of chained asynchronous calls.
     * <pre>
     *        start
     *     _____|_____
     *     |         |
     * service1      |
     *     |      service3
     * service2      |
     *     |         |
     *     -----|-----
     *       service4
     *          |
     *         end
     * </pre>
     * @return The status
     */
    public abstract Status call();


    public static enum Status {
        OK, INTERRUPTED, ABORTED
    }


    private static class NamedThreadFactory implements ThreadFactory {
        private final AtomicInteger count = new AtomicInteger();
        private final String prefix;


        private NamedThreadFactory(String prefix) {
            this.prefix = prefix;
        }


        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, prefix + count.getAndIncrement());
        }
    }
}
