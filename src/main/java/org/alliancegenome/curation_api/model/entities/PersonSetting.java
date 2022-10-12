package org.alliancegenome.curation_api.model.entities;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.GeneratedAuditedObject;
import org.alliancegenome.curation_api.view.View;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import io.quarkiverse.hibernate.types.json.JsonBinaryType;
import io.quarkiverse.hibernate.types.json.JsonTypes;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Audited
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true, exclude = "person")
@JsonIgnoreProperties(ignoreUnknown = true)
@AGRCurationSchemaVersion(min="1.3.2", max=LinkMLSchemaConstants.LATEST_RELEASE, dependencies={Agent.class})
@TypeDef(name = JsonTypes.JSON_BIN, typeClass = JsonBinaryType.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class PersonSetting extends GeneratedAuditedObject {

	@ManyToOne
	private Person person;
	
	@JsonView(View.PersonSettingView.class)
	private String settingsKey;

	@Type(type = JsonTypes.JSON_BIN)
	@Column(columnDefinition = JsonTypes.JSON_BIN)
	@JsonView(View.PersonSettingView.class)
	private Map<String, Object> settingsMap;
	
}