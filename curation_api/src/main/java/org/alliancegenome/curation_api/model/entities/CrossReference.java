package org.alliancegenome.curation_api.model.entities;

import java.util.List;

import javax.persistence.*;

import org.hibernate.envers.Audited;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.*;

@Audited
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@ToString(exclude = {"genomicEntity"})
public class CrossReference extends InformationContentEntity {

	@ElementCollection
	private List<String> pageAreas; 
	private String displayName;
	private String prefix;
	
	@ManyToOne
	@JsonIgnore
	private GenomicEntity genomicEntity;
}
