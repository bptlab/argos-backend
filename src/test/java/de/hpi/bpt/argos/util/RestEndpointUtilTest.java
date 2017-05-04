package de.hpi.bpt.argos.util;

import org.junit.Test;
import spark.HaltException;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

public class RestEndpointUtilTest {

	@Test
	public void testValidateInteger() {
		assertEquals(1, RestEndpointUtilImpl.getInstance().validateInteger("1", (Integer input) -> input >= 0));
	}

	@Test(expected = HaltException.class)
	public void testValidateInteger_InvalidInput_HaltException() {
		RestEndpointUtilImpl.getInstance().validateInteger("not an integer", (Integer input) -> input >= 0);
	}

	@Test(expected = HaltException.class)
	public void testValidateInteger_ValidationError_HaltException() {
		RestEndpointUtilImpl.getInstance().validateInteger("-1", (Integer input) -> input >= 0);
	}

	@Test(expected = HaltException.class)
	public void testValidateInteger_OutOfRange_HaltException() {
		RestEndpointUtilImpl.getInstance().validateInteger(Integer.toString(Integer.MAX_VALUE) + "0", (Integer input) -> input >= 0);
	}

	@Test
	public void testValidateLong() {
		assertEquals(1, RestEndpointUtilImpl.getInstance().validateLong("1", (Long input) -> input >= 0));
	}

	@Test(expected = HaltException.class)
	public void testValidateLong_InvalidInput_HaltException() {
		RestEndpointUtilImpl.getInstance().validateLong("not a long", (Long input) -> input >= 0);
	}

	@Test(expected = HaltException.class)
	public void testValidateLong_ValidationError_HaltException() {
		RestEndpointUtilImpl.getInstance().validateLong("-1", (Long input) -> input >= 0);
	}

	@Test(expected = HaltException.class)
	public void testValidateLong_OutOfRange_HaltException() {
		RestEndpointUtilImpl.getInstance().validateLong(Long.toString(Long.MAX_VALUE) + "0", (Long input) -> input >= 0);
	}

	@Test
	public void testValidateListOfString() {
		List<String> strings = new ArrayList<>();

		strings.add("string_A-1");
		strings.add("string_A-2");

		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < strings.size(); i++) {
			if (i > 0) {
				stringBuilder.append(" ");
			}

			stringBuilder.append(strings.get(i));
		}

		List<String> validatedStrings = RestEndpointUtilImpl.getInstance()
				.validateListOfString(stringBuilder.toString(), (String input) -> input.matches("(\\w|-)+(\\s(\\w|-)+)*"));

		assertEquals(true, strings.containsAll(validatedStrings));
		assertEquals(true, validatedStrings.containsAll(strings));
	}

	@Test(expected = HaltException.class)
	public void testValidateListOfString_ValidationError_HaltException() {
		List<String> strings = new ArrayList<>();

		strings.add("string_A-1--Ö");
		strings.add("string_A-2--/");

		StringBuilder stringBuilder = new StringBuilder();

		for (int i = 0; i < strings.size(); i++) {
			if (i > 0) {
				stringBuilder.append(" ");
			}

			stringBuilder.append(strings.get(i));
		}

		RestEndpointUtilImpl.getInstance().validateListOfString(stringBuilder.toString(), (String input) -> input.matches("(\\w|-)+(\\s(\\w|-)+)*"));
	}
}
