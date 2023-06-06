package org.alliancegenome.curation_api.model.entities;

import java.util.List;
import java.util.Optional;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Transient;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.enums.CrossReferencePrefix;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.envers.Audited;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Audited
@Entity
@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = true)
@ToString(callSuper = true)
@Schema(name = "Reference", description = "POJO that represents the Reference")
@AGRCurationSchemaVersion(min = "1.4.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = {InformationContentEntity.class}, partial = true)
public class Reference extends InformationContentEntity {

	@IndexedEmbedded(includeDepth = 1)
	@IndexingDependency(reindexOnUpdate = ReindexOnUpdate.SHALLOW)
	@ManyToMany(fetch = FetchType.EAGER)
	@JsonView({View.FieldsOnly.class})
	@JoinTable(indexes = {@Index(columnList = "Reference_curie"), @Index(columnList = "crossReferences_id")})
	@EqualsAndHashCode.Include
	@Fetch(FetchMode.SUBSELECT)
	private List<CrossReference> crossReferences;

	/**
	 * Retrieve PMID if available in the crossReference collection otherwise MOD ID
	 */
	@Transient
	@JsonIgnore
	public String getReferenceID() {
		Optional<CrossReference> opt = getCrossReferences().stream().filter(reference -> reference.getReferencedCurie().startsWith("PMID:")).findFirst();
		// if no PUBMED ID try MOD ID
		if (opt.isEmpty()) {
			opt = getCrossReferences().stream().filter(reference -> CrossReferencePrefix.valueOf(reference.getPrefix()) != null).findFirst();
		}
		return opt.map(CrossReference::getReferencedCurie).orElse(null);
	}
}
