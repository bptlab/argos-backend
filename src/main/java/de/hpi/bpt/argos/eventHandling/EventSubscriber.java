package de.hpi.bpt.argos.eventHandling;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class EventSubscriber implements IEventSubscriber {

	@Override
	public boolean subscribeToEventPlatform(String host, String uri, String eventQuery) {
		try {
			URL eventPlatformURL = new URL(host + uri);

			HttpURLConnection subscriptionRequest = (HttpURLConnection)eventPlatformURL.openConnection();

			subscriptionRequest.setRequestMethod("POST");
			subscriptionRequest.setRequestProperty("Content-Type", "application/json");
			subscriptionRequest.setRequestProperty("Accept", "text/plain");

			subscriptionRequest.setDoOutput(true);
			DataOutputStream output = new DataOutputStream(subscriptionRequest.getOutputStream());

			output.writeBytes(eventQuery);
			output.flush();
			output.close();

			int responseCode = subscriptionRequest.getResponseCode();

			BufferedReader reader = new BufferedReader(new InputStreamReader(subscriptionRequest.getInputStream()));

			String responseString;
			StringBuffer response = new StringBuffer();

			while ((responseString = reader.readLine()) != null) {
				response.append(responseString);
			}
			reader.close();

			System.out.println("response code: " + responseCode);
			System.out.println("response: " + response.toString());
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}
}
