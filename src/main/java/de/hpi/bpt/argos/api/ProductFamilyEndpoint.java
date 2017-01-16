package de.hpi.bpt.argos.api;

import de.hpi.bpt.argos.common.RestEndpoint;
import spark.Request;
import spark.Response;

public interface ProductFamilyEndpoint extends RestEndpoint {
	String getProductFamilies(Request request, Response response);

	String getProductFamilyOverview(Request request, Response response);

	String getEventsForProductFamily(Request request, Response response);
}
