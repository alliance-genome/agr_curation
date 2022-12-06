package org.alliancegenome.curation_api.model.entities.bulkloads;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Transient;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Audited
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "1.2.4", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { BulkLoad.class })
public abstract class BulkScheduledLoad extends BulkLoad {

	@JsonView({ View.FieldsOnly.class })
	private Boolean scheduleActive;
	@JsonView({ View.FieldsOnly.class })
	private String cronSchedule;

	@JsonView({ View.FieldsOnly.class })
	@Column(columnDefinition = "TEXT")
	private String schedulingErrorMessage;

	@Transient
	@JsonView({ View.FieldsOnly.class })
	public String getNextRun() {
		CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);
		CronParser parser = new CronParser(cronDefinition);
		try {
			Cron unixCron = parser.parse(cronSchedule);
			unixCron.validate();
			ExecutionTime executionTime = ExecutionTime.forCron(unixCron);
			ZonedDateTime nextExecution = executionTime.nextExecution(ZonedDateTime.now()).get();

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss Z");
			return nextExecution.format(formatter);

		} catch (Exception e) {
			return "";
		}
	}

	public void setNextRun(String nextRun) {
	}

}
