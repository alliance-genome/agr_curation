package org.alliancegenome.curation_api.model.entities;

import java.util.Map;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;
import org.alliancegenome.curation_api.model.entities.base.GeneratedAuditedObject;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import io.quarkiverse.hibernate.types.json.JsonTypes;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Audited
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true, exclude = "person")
@JsonIgnoreProperties(ignoreUnknown = true)
@AGRCurationSchemaVersion(min = "1.3.2", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObject.class })
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
@Table(indexes = {
	@Index(name = "personsetting_createdby_index", columnList = "createdBy_id"),
	@Index(name = "personsetting_updatedby_index", columnList = "updatedBy_id"),
	@Index(name = "personsetting_person_index", columnList = "person_id"),
	@Index(name = "personsetting_settingskey_index", columnList = "settingskey")
})
public class PersonSetting extends GeneratedAuditedObject {

	@ManyToOne
	private Person person;

	@JsonView(View.PersonSettingView.class)
	private String settingsKey;

	@Column(columnDefinition = JsonTypes.JSON)
	@JsonView(View.PersonSettingView.class)
	private Map<String, Object> settingsMap;

}