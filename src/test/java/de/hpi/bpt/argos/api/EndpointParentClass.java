package de.hpi.bpt.argos.api;

import de.hpi.bpt.argos.common.RestRequest;
import de.hpi.bpt.argos.common.RestRequestFactory;
import de.hpi.bpt.argos.common.RestRequestFactoryImpl;
import de.hpi.bpt.argos.core.Argos;
import de.hpi.bpt.argos.core.ArgosImpl;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class EndpointParentClass {
    private static final int TEST_PORT = 9001;
    private static final int TEST_NUMBER_OF_THREADS = 8;
    protected static final String TEST_HOST = "http://localhost:" + TEST_PORT;
    protected static final String TEST_REQUEST_METHOD = "GET";
    protected static final String TEST_CONTENT_TYPE = "application/json";
    protected static final String TEST_ACCEPT_TYPE = "text/plain";
    protected static final int HTTP_NOT_FOUND_CODE = 404;
    protected static final int INVALID_REQUEST_RESPONSE_CODE = 500;

    private static Argos argos;
    protected static RestRequestFactory requestFactory;
    protected RestRequest request;

    @BeforeClass
    public static void setUp() {
        argos = new ArgosImpl();
        argos.run(TEST_PORT, TEST_NUMBER_OF_THREADS);
        requestFactory = new RestRequestFactoryImpl();
    }

    @AfterClass
    public static void tearDown() {
        argos.shutdown();
    }
}
