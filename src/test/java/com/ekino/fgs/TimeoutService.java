/*
 * TimeoutService.java
 *
 * Copyright (c) 2012 Ekino
 *
 * $Id$
 */
package com.ekino.fgs;

/**
 * @author Frank Pavageau
 * @version $Id$
 */
public class TimeoutService extends SleepService {
    public TimeoutService(int index, boolean sleep, Mocks mocks) {
        super(index, sleep, mocks);
    }


    @Override
    public void call() {
        super.call();
        throw new TimeoutException(this + " timed out");
    }


    private static class TimeoutException extends RuntimeException {
        public TimeoutException(String message) {
            super(message);
        }
    }
}
