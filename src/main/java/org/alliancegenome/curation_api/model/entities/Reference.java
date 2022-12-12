package org.alliancegenome.curation_api.model.entities;

import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.view.View;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.hibernate.envers.Audited;
import org.hibernate.search.mapper.pojo.automaticindexing.ReindexOnUpdate;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexedEmbedded;
import org.hibernate.search.mapper.pojo.mapping.definition.annotation.IndexingDependency;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
	@ManyToMany
	@JsonView({View.FieldsOnly.class})
	@JoinTable(indexes = {@Index(columnList = "Reference_curie"), @Index(columnList = "crossReferences_curie")})
	@EqualsAndHashCode.Include
	private List<CrossReference> crossReferences;


	public static List<String> speciesPrefix;

	static {
		speciesPrefix.add("MGI");
		speciesPrefix.add("ZFIN");
		speciesPrefix.add("SGD");
		speciesPrefix.add("FB");
		speciesPrefix.add("WB");
		speciesPrefix.add("XB");
		speciesPrefix.add("OMIM");
		speciesPrefix.add("GO");
	}

	/**
	 * Retrieve PMID if available in the crossReference collection otherwise MOD ID
	 */
	public String getReferenceID() {
		List<String> referencePrefixEnum = getCrossReferences().stream().map(CrossReference::getPrefix).collect(Collectors.toList());

		Optional<CrossReference> opt = getCrossReferences().stream().filter(reference -> reference.getCurie().contains("PMID:")).findFirst();
		// if no PUBMED ID try MOD ID
		if (opt.isEmpty()) {
			opt = getCrossReferences().stream().filter(reference -> speciesPrefix.contains(reference.getPrefix())).findFirst();
		}
		return opt.map(CrossReference::getPrefix).orElse(null);
	}


}
