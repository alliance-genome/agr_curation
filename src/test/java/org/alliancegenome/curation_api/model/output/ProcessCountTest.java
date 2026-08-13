package org.alliancegenome.curation_api.model.output;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * SCRUM-6258 - {@link ProcessCount#getErrorRate()} used to read {@code return error / 1000;},
 * integer division on a Long. It returned 0 for any error count below 1000 and then jumped to
 * whole numbers above it, so the "failure rate > 0.25" cutoff in LoadFileExecutor was really
 * "abort once 1000 net errors accumulate" - silent below that, and far over the cutoff above it.
 * A ZFIN GFF load on beta recorded 131,099 failed associations as an error rate of 131.0.
 */
@DisplayName("ProcessCount error rate")
class ProcessCountTest {

	private static final double DELTA = 0.0001;

	private static ProcessCount counts(long total, int completed, int failed, int skipped) {
		ProcessCount count = new ProcessCount(total);
		for (int i = 0; i < completed; i++) {
			count.incrementCompleted();
		}
		for (int i = 0; i < failed; i++) {
			count.incrementFailed();
		}
		for (int i = 0; i < skipped; i++) {
			count.incrementSkipped();
		}
		return count;
	}

	@Test
	public void nothingProcessedIsNotAnError() {
		assertEquals(0.0, counts(100, 0, 0, 0).getErrorRate(), DELTA,
			"a load that has not processed anything yet must not report a failure rate");
	}

	@Test
	public void allCompletedIsZero() {
		assertEquals(0.0, counts(10, 10, 0, 0).getErrorRate(), DELTA);
	}

	@Test
	public void allFailedIsOne() {
		assertEquals(1.0, counts(10, 0, 10, 0).getErrorRate(), DELTA);
	}

	@Test
	public void rateIsFailedOverProcessed() {
		// 1 of 4 processed failed
		assertEquals(0.25, counts(4, 3, 1, 0).getErrorRate(), DELTA);
		// 1 of 3 processed failed - above the 0.25 cutoff
		assertEquals(1.0 / 3.0, counts(3, 2, 1, 0).getErrorRate(), DELTA);
	}

	@Test
	public void skippedRecordsCountAsProcessed() {
		// 1 failed, 1 completed, 2 skipped -> 1 of 4
		assertEquals(0.25, counts(4, 1, 1, 2).getErrorRate(), DELTA);
	}

	// The old implementation returned 0 for anything under 1000 errors, so a load could fail
	// every single record and still sit under the cutoff.
	@Test
	public void smallTotallyFailedLoadIsNotSilent() {
		assertEquals(1.0, counts(5, 0, 5, 0).getErrorRate(), DELTA,
			"5 failures out of 5 is a 100% failure rate, not 0");
		assertEquals(1.0, counts(999, 0, 999, 0).getErrorRate(), DELTA,
			"999 failures used to fall below the hardcoded /1000 divisor and report 0");
	}

	// The observed beta case: the whole Associations phase failed and was reported as 131.0.
	@Test
	public void theZfinRegressionCaseIsARateNotAMultiple() {
		ProcessCount count = new ProcessCount(131099L);
		count.setFailed(131099L);
		assertEquals(1.0, count.getErrorRate(), DELTA,
			"a fully failed load is a rate of 1.0, never 131.0");
	}

	// incrementCompleted decrements the running `error` counter, which the old rate keyed on.
	// The rate must key on `failed`, which is monotonic, so recovery cannot mask past failures.
	@Test
	public void laterSuccessesDoNotEraseTheFailureRate() {
		ProcessCount count = counts(10, 0, 5, 0);
		assertEquals(1.0, count.getErrorRate(), DELTA);

		for (int i = 0; i < 5; i++) {
			count.incrementCompleted();
		}
		assertEquals(0.5, count.getErrorRate(), DELTA,
			"5 failures and 5 successes is 50%, regardless of the running error counter");
	}
}
