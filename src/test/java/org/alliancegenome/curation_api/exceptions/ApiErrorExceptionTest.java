package org.alliancegenome.curation_api.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.alliancegenome.curation_api.response.ObjectResponse;
import org.junit.jupiter.api.Test;

// SCRUM-6874: a bulk load records e.getMessage() for a failed deletion, so the reason must be readable from the exception
class ApiErrorExceptionTest {

	@Test
	void keepsTheMessageItIsGiven() {
		assertEquals("Thread isInterrupted", new ApiErrorException(ApiErrorException.INTERRUPTED_MESSAGE).getMessage());
	}

	@Test
	void readsTheErrorMessageOfItsResponse() {
		ObjectResponse<Object> response = new ObjectResponse<>();
		response.setErrorMessage("Violates database constraint constructcassetteassoc_object_id_fk");

		assertEquals("Violates database constraint constructcassetteassoc_object_id_fk", new ApiErrorException(response).getMessage());
	}

	@Test
	void readsTheFieldErrorsOfItsResponse() {
		ObjectResponse<Object> response = new ObjectResponse<>();
		response.addErrorMessage("uniqueId", "Non-unique value");
		response.addErrorMessage("dataProvider", "Required field is empty");

		assertEquals("dataProvider - Required field is empty | uniqueId - Non-unique value", new ApiErrorException(response).getMessage());

		response.setErrorMessage("Could not create TransgenicTool");
		assertEquals("Could not create TransgenicTool: dataProvider - Required field is empty | uniqueId - Non-unique value",
			new ApiErrorException(response).getMessage());
	}

	@Test
	void readsAResponseFilledInAfterTheExceptionWasCreated() {
		ObjectResponse<Object> response = new ObjectResponse<>();
		ApiErrorException exception = new ApiErrorException(response);
		assertNull(exception.getMessage());

		response.setErrorMessage("Entity not found");
		assertEquals("Entity not found", exception.getMessage());
	}

	@Test
	void keepsItsCauseAndReadsTheMessageOfItsResponse() {
		ObjectResponse<Object> response = new ObjectResponse<>();
		response.setErrorMessage("Violates database constraint constructcassetteassoc_object_id_fk");
		RuntimeException cause = new RuntimeException("ERROR: update or delete on table \"cassette\" violates foreign key constraint");

		ApiErrorException exception = new ApiErrorException(response, cause);
		assertSame(cause, exception.getCause());
		assertEquals("Violates database constraint constructcassetteassoc_object_id_fk", exception.getMessage());
	}
}
