package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import javax.persistence.*;

import org.alliancegenome.curation_api.base.BaseCurieEntity;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

@Audited
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@ToString
@Schema(name="Cross Reference", description="POJO that represents the Cross Reference")
public class CrossReference extends BaseCurieEntity {

	@ElementCollection
	private List<String> pageAreas; 
	
	private String displayName;
	private String prefix;
	
}
