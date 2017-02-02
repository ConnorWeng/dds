package com.icbc.dds.rpc.factory;

import com.icbc.dds.rpc.support.ExampleRestSupport;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by kfzx-wengxj on 02/02/2017.
 */
public class SupportFactoryTest {
    @Before
    public void clearCache() {
        SupportFactory.reset();
    }

    @Test
    public void getOneRestSupportTwiceShouldReturnTheSameInstance() {
        ExampleRestSupport restSupport1 = SupportFactory.getRestSupport(ExampleRestSupport.class);
        ExampleRestSupport restSupport2 = SupportFactory.getRestSupport(ExampleRestSupport.class);
        assertSame(restSupport1, restSupport2);
    }

    @Test
    public void getOneRestSupportTwiceConcurrentlyShouldReturnTheSameInstance() throws InterruptedException {
        final ExampleRestSupport[] restSupports = new ExampleRestSupport[2];
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                restSupports[0] = SupportFactory.getRestSupport(ExampleRestSupport.class);
            }
        });
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                restSupports[1] = SupportFactory.getRestSupport(ExampleRestSupport.class);
            }
        });
        thread1.run();
        thread2.run();

        thread1.join();
        thread2.join();

        assertNotNull(restSupports[0]);
        assertSame(restSupports[0], restSupports[1]);
    }
}