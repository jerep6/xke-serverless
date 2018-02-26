package com.serverless.gateway;

import io.restassured.RestAssured;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import static org.apache.commons.lang3.StringUtils.defaultIfBlank;

/**
 * Contains common code to be used by all IT tests
 */
public class BaseTest {

    private static ServerlessApiGatewayEmulator apiGateway = null;

    public BaseTest() {
    }

    @BeforeClass
    public static void setUpClass() {

        String serverApiPort = defaultIfBlank(System.getProperty("port"), "3000");

        RestAssured.baseURI = "http://localhost:" + serverApiPort;

        apiGateway = ServerlessApiGatewayEmulator.instance(Integer.parseInt(serverApiPort));
    }

    @AfterClass
    public static void tearDownClass() {
        // left empty intentionally
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

}
