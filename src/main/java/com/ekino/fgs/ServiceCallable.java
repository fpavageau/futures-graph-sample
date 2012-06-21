/*
 * ServiceCallable.java
 *
 * Copyright (c) 2012 Ekino
 *
 * $Id$
 */
package com.ekino.fgs;

import java.util.concurrent.Callable;

/**
 * {@link Service} wrapper.
 *
 * @author Frank Pavageau
 * @version $Id$
 */
public class ServiceCallable implements Callable<Void> {
    private final Service service;


    public ServiceCallable(Service service) {
        this.service = service;
    }


    @Override
    public Void call()
            throws Exception {
        service.call();
        return null;
    }
}
