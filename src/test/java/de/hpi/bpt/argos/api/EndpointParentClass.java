package de.hpi.bpt.argos.api;

import com.google.gson.Gson;
import com.google.gson.JsonParser;
import de.hpi.bpt.argos.common.RestRequest;
import de.hpi.bpt.argos.common.RestRequestFactory;
import de.hpi.bpt.argos.common.RestRequestFactoryImpl;
import de.hpi.bpt.argos.core.ArgosTestParent;
import org.junit.BeforeClass;

public class EndpointParentClass extends ArgosTestParent {
    protected final String TEST_REQUEST_METHOD = "GET";
    protected final String TEST_CONTENT_TYPE = "application/json";
    protected final String TEST_ACCEPT_TYPE_PLAIN = "text/plain";
    protected final String TEST_ACCEPT_TYPE_JSON = "application/json";

    protected final JsonParser jsonParser = new JsonParser();
	protected final Gson serializer = new Gson();
	protected RestRequest request;

    protected static RestRequestFactory requestFactory;

    @BeforeClass
    public static void beforeTest() {
        requestFactory = new RestRequestFactoryImpl();
    }
}
