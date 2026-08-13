package org.alliancegenome.curation_api.model.output;

import org.alliancegenome.curation_api.view.CurationView;

import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor
@JsonView({ CurationView.FieldsOnly.class })
public class ProcessCount {
	private Long total = 0L;
	private Long failed = 0L;
	private Long skipped = 0L;
	private Long completed = 0L;
	private Long warnings = 0L;
	private Long error = 0L;
	
	public ProcessCount(Long total) {
		this.total = total;
	}
	public ProcessCount(Integer total) {
		this.total = Long.valueOf(total);
	}

	public void incrementCompleted() {
		completed++;
		if (error > 0) {
			error--;
		}
	}
	
	public void incrementSkipped() {
		skipped++;
	}
	
	public void incrementFailed() {
		failed++;
		error++;
	}

	public void incrementWarnings() {
		warnings++;
	}
	/**
	 * SCRUM-6258: fraction of processed records that failed, in [0.0, 1.0].
	 *
	 * This used to read {@code return error / 1000;} - integer division on a Long, so it
	 * returned 0 for any error count below 1000 and then jumped to whole numbers (131099
	 * errors reported as 131.0). It was not a rate at all, which made the
	 * "failure rate > 0.25" cutoff in LoadFileExecutor behave as "abort once 1000 net errors
	 * accumulate", silent below that and wildly over the cutoff above it.
	 */
	public double getErrorRate() {
		long processed = completed + failed + skipped;
		if (processed <= 0) {
			return 0.0;
		}
		return (double) failed / (double) processed;
	}

	public void add(ProcessCount count) {
		total += count.getTotal();
		failed += count.getFailed();
		skipped += count.getSkipped();
		completed += count.getCompleted();
		warnings += count.getWarnings();
		error += count.getError();
	}

}
