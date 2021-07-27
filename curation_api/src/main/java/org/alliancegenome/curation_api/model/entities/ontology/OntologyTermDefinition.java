package org.alliancegenome.curation_api.model.entities.ontology;

import java.util.List;

import javax.persistence.*;

import org.alliancegenome.curation_api.model.entities.BaseEntity;
import org.hibernate.envers.Audited;
import org.hibernate.search.annotations.Field;

import lombok.*;

@Audited
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = {"termDefinitionUrls"})
public class OntologyTermDefinition extends BaseEntity {

	
	@Field
	@Column(columnDefinition="TEXT")
	private String termDefinition;
	
	@ElementCollection
	private List<String> termDefinitionUrls;
	
}
