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
	public double getErrorRate() {
		return error / 1000;
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
