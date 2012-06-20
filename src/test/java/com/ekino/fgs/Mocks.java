/*
 * Mocks.java
 *
 * Copyright (c) 2012 Ekino
 *
 * $Id$
 */
package com.ekino.fgs;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.LinkedHashMultiset;
import com.google.common.collect.Multiset;

import static org.testng.Assert.*;

/**
 * The mocked {@link Service}s container.
 *
* @author Frank Pavageau
* @version $Id$
*/
public class Mocks {
    public final List<Service> services;
    private final Multiset<Service> calls = LinkedHashMultiset.create();


    /**
     * Constructor.
     *
     * @param sleep true if the services are blocking, false if they are not
     * @throws Exception
     */
    public Mocks(boolean sleep)
            throws Exception {
        this(sleep, 0);
    }


    /**
     * Constructor.
     *
     * @param sleep true if the services are blocking, false if they are not
     * @param timeout The number of the service (1-4) which should time out
     * @throws Exception If a mock cannot be created
     */
    public Mocks(boolean sleep, int timeout)
            throws Exception {
        ImmutableList.Builder<Service> services = ImmutableList.builder();
        for (int i = 1; i <= 4; i++) {
            if (timeout == i) {
                services.add(new TimeoutService(i, sleep, this));
            } else {
                services.add(new SleepService(i, sleep, this));
            }
        }

        this.services = services.build();
    }


    /**
     * Logs a call to a service.
     *
     * @param service The service
     */
    public synchronized void addCall(Service service) {
        calls.add(service);
    }


    /**
     * Asserts that a service has been called exactly once.
     *
     * @param i The number of the service (1-4)
     * @throws Exception
     */
    public void assertCall(int i)
            throws Exception {
        Service service = services.get(i - 1);
        assertEquals(calls.count(service), 1, "Single call for " + service);
    }


    /**
     * Asserts that a service has been called at most once.
     *
     * @param i The number of the service (1-4)
     * @throws Exception
     */
    public void assertMaybeCall(int i)
            throws Exception {
        Service service = services.get(i - 1);
        assertTrue(calls.count(service) <= 1, "At most one call for " + service);
    }


    /**
     * Asserts that a service has not been called.
     *
     * @param i The number of the service (1-4)
     * @throws Exception
     */
    public void assertNoCall(int i)
            throws Exception {
        Service service = services.get(i - 1);
        assertEquals(calls.count(service), 0, "No call for " + service);
    }


    /**
     * Asserts the ordering of service calls.
     *
     * @param indices The numbers of the services, in the order they are supposed to be called
     */
    public void assertOrder(int... indices) {
        assertTrue(indices.length > 1, "Not enough indices to assert the order");

        // Compare the order of service calls 2 by 2, using a sliding window, since the ordering is transitive.
        for (int i = 0, max = indices.length - 1; i < max; i++) {
            Service beforeService = services.get(indices[i] - 1), afterService = services.get(indices[i + 1] - 1);
            int beforeIndex = indexOfCall(beforeService), afterIndex = indexOfCall(afterService);
            assertTrue(beforeIndex < afterIndex,
                    beforeService + "(" + beforeIndex + ") is not before " + afterService + " (" + afterIndex + ")");
        }
    }


    /**
     * Returns the index of the first call on a {@link Service}.
     *
     * @param service The {@link Service}
     * @return The index of the first call, or -1 if the service was not called
     */
    private int indexOfCall(Service service) {
        int index = -1;
        for (Service call : calls) {
            index++;
            if (call.equals(service)) {
                return index;
            }
        }
        return -1;
    }
}
