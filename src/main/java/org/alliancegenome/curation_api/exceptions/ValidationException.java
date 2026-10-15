package org.alliancegenome.curation_api.exceptions;

import io.quarkus.transaction.annotations.Rollback;
import lombok.NoArgsConstructor;

// @Rollback is @Inherited, so every subclass (ObjectUpdateException, ObjectValidationException,
// KnownIssueValidationException, ...) rolls back any @Transactional method it passes through --
// this is a checked exception, so without this a bare @Transactional commits anyway. See
// TransactionalInterceptorBase.handleExceptionNoThrow: it checks this annotation directly on the
// thrown exception's class, independent of the @Transactional method's own rollbackOn.
@Rollback
@NoArgsConstructor
public class ValidationException extends Exception {
	public ValidationException(String message) {
		super(message);
	}
}