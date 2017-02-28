package de.hpi.bpt.argos.common.validation;

import de.hpi.bpt.argos.persistence.model.product.ProductState;
import org.junit.Test;
import spark.HaltException;

import static org.junit.Assert.assertEquals;

public class RestInputValidationServiceTest {

	protected final RestInputValidationService inputValidationService = new RestInputValidationServiceImpl();

	@Test
	public void testValidateInteger() {
		assertEquals(1, inputValidationService.validateInteger("1", (Integer input) -> input >= 0));
	}

	@Test(expected = HaltException.class)
	public void testValidateInteger_ValidationError_HaltException() {
		inputValidationService.validateInteger("-1", (Integer input) -> input >= 0);
	}

	@Test(expected = HaltException.class)
	public void testValidateInteger_InvalidInput_HaltException() {
		inputValidationService.validateInteger("not a number", (Integer input) -> input >= 0);
	}

	@Test
	public void testValidateLong() {
		assertEquals(1L, inputValidationService.validateLong("1", (Long input) -> input >= 0));
	}

	@Test(expected = HaltException.class)
	public void testValidateLong_ValidationError_HaltException() {
		inputValidationService.validateLong("-1", (Long input) -> input >= 0);
	}

	@Test(expected = HaltException.class)
	public void testValidateLong_InvalidInput_HaltException() {
		inputValidationService.validateLong("not a number", (Long input) -> input >= 0);
	}

	@Test
	public void testValidateEnum() {
		assertEquals(ProductState.RUNNING, inputValidationService.validateEnum(ProductState.class, "RUNNING"));
	}

	@Test(expected = HaltException.class)
	public void testValidateEnum_InvalidInput_HaltException() {
		inputValidationService.validateEnum(ProductState.class, "NOT_A_MEMBER");
	}
}
