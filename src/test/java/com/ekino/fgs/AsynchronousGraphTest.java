/*
 * AsynchronousGraphTest.java
 *
 * Copyright (c) 2012 Ekino
 *
 * $Id$
 */
package com.ekino.fgs;

import java.util.List;

import com.google.common.collect.Lists;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Generic tests for all the {@link AsynchronousGraph} implementations.
 *
 * @author Frank Pavageau
 * @version $Id$
 */
@Test(invocationCount = 10, dataProvider = "config")
public class AsynchronousGraphTest {
    public void ok(boolean sleep, AsynchronousGraphFactory factory)
            throws Exception {
        Mocks mocks = new Mocks(sleep);

        assertEquals(factory.create(mocks).call(), AsynchronousGraph.Status.OK);

        mocks.assertCall(1);
        mocks.assertCall(2);
        mocks.assertCall(3);
        mocks.assertCall(4);
        mocks.assertOrder(1, 2, 4);
        mocks.assertOrder(3, 4);
    }


    public void timeoutOn1(boolean sleep, AsynchronousGraphFactory factory)
            throws Exception {
        Mocks mocks = new Mocks(sleep, 1);

        assertEquals(factory.create(mocks).call(), AsynchronousGraph.Status.ABORTED);

        mocks.assertCall(1);
        mocks.assertNoCall(2);
        mocks.assertMaybeCall(3);
        mocks.assertNoCall(4);
    }


    public void timeoutOn2(boolean sleep, AsynchronousGraphFactory factory)
            throws Exception {
        Mocks mocks = new Mocks(sleep, 2);

        assertEquals(factory.create(mocks).call(), AsynchronousGraph.Status.ABORTED);

        mocks.assertCall(1);
        mocks.assertCall(2);
        mocks.assertMaybeCall(3);
        mocks.assertNoCall(4);
        mocks.assertOrder(1, 2);
    }


    public void timeoutOn3(boolean sleep, AsynchronousGraphFactory factory)
            throws Exception {
        Mocks mocks = new Mocks(sleep, 3);

        assertEquals(factory.create(mocks).call(), AsynchronousGraph.Status.ABORTED);

        mocks.assertMaybeCall(1);
        mocks.assertMaybeCall(2);
        mocks.assertCall(3);
        mocks.assertNoCall(4);
    }


    public void timeoutOn4(boolean sleep, AsynchronousGraphFactory factory)
            throws Exception {
        Mocks mocks = new Mocks(sleep, 4);

        assertEquals(factory.create(mocks).call(), AsynchronousGraph.Status.ABORTED);

        mocks.assertCall(1);
        mocks.assertCall(2);
        mocks.assertCall(3);
        mocks.assertCall(4);
        mocks.assertOrder(1, 2, 4);
        mocks.assertOrder(3, 4);
    }


    @DataProvider
    public Object[][] config() {
        List<Object[]> parameters = Lists.newArrayList();
        boolean sleeps[] = new boolean[] { true, false };
        // Cartesian product: blocking and non-blocking calls, all the AsynchronousGraph implementations.
        for (boolean sleep : sleeps) {
            for (AsynchronousGraphFactory factory : AsynchronousGraphFactory.values()) {
                parameters.add(new Object[] { sleep, factory });
            }
        }
        return parameters.toArray(new Object[parameters.size()][]);
    }
}
