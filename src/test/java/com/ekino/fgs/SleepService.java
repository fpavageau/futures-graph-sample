/*
 * SleepService.java
 *
 * Copyright (c) 2012 Ekino
 *
 * $Id$
 */
package com.ekino.fgs;

import java.util.Random;

import com.google.common.base.Preconditions;

/**
 * @author Frank Pavageau
 * @version $Id$
 */
public class SleepService implements Service {
    private static final ThreadLocal<Random> RANDOM = new ThreadLocal<Random>() {
        @Override
        protected Random initialValue() {
            return new Random();
        }
    };

    private final int index;
    private final boolean sleep;
    private final Mocks mocks;


    public SleepService(int index, boolean sleep, Mocks mocks) {
        this.index = index;
        this.sleep = sleep;
        this.mocks = Preconditions.checkNotNull(mocks);
    }


    @Override
    public void call() {
        mocks.addCall(this);
        if (sleep) {
            try {
                Thread.sleep(95 + RANDOM.get().nextInt(10));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            }
        }
    }


    @Override
    public String toString() {
        return "Service " + index;
    }
}
