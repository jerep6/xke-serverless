package com.serverless.gateway;

import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class ServerlessOffline extends BaseTest {

    @Test
    public void runLocal() throws InterruptedException {
        while (true) {
            TimeUnit.MINUTES.sleep(1);
        }
    }
}
