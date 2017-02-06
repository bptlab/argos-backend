package de.hpi.bpt.argos.api;

import de.hpi.bpt.argos.common.RestRequestFactory;
import de.hpi.bpt.argos.common.RestRequestFactoryImpl;
import de.hpi.bpt.argos.core.ArgosTestParent;
import org.junit.BeforeClass;

public class EndpointParentClass extends ArgosTestParent {
    protected static final String TEST_REQUEST_METHOD = "GET";
    protected static final String TEST_CONTENT_TYPE = "application/json";
    protected static final String TEST_ACCEPT_TYPE = "text/plain";
    protected static final int HTTP_NOT_FOUND_CODE = 404;
    protected static final int INVALID_REQUEST_RESPONSE_CODE = 500;

    protected static RestRequestFactory requestFactory;

    @BeforeClass
    public static void beforeTest() {
        requestFactory = new RestRequestFactoryImpl();
    }
}
