/*
 * AsynchronousGraphFactory.java
 *
 * Copyright (c) 2012 Ekino
 *
 * $Id$
 */
package com.ekino.fgs;

/**
 * Enum representing the factories for all the {@link AsynchronousGraph} implementations.
 *
* @author Frank Pavageau
* @version $Id$
*/
public enum AsynchronousGraphFactory {
    GUAVA {
        @Override
        public AsynchronousGraph create(Mocks mocks) {
            return new GuavaAsynchronousGraph(mocks.services.get(0), mocks.services.get(1), mocks.services.get(2),
                    mocks.services.get(3));
        }
    },
    AKKA {
        @Override
        public AsynchronousGraph create(Mocks mocks) {
            return new AkkaAsynchronousGraph(mocks.services.get(0), mocks.services.get(1), mocks.services.get(2),
                    mocks.services.get(3));
        }
    };


    /**
     * Creates an {@link AsynchronousGraph}.
     *
     * @param mocks The mocked services container
     * @return The {@link AsynchronousGraph}
     */
    public abstract AsynchronousGraph create(Mocks mocks);
}
