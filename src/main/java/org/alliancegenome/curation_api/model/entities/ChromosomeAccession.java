package org.alliancegenome.curation_api.model.entities;

import org.alliancegenome.curation_api.constants.LinkMLSchemaConstants;
import org.alliancegenome.curation_api.interfaces.AGRCurationSchemaVersion;
import org.alliancegenome.curation_api.model.entities.base.AuditedObject;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Schema(name = "ChromosomeAccession", description = "ChromosomeAccession: a chromosome name and RefSeq accession for a genome assembly")
@Entity
@Data
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@ToString(callSuper = true)
@AGRCurationSchemaVersion(min = "2.0.0", max = LinkMLSchemaConstants.LATEST_RELEASE, dependencies = { AuditedObject.class })
@Table(
	indexes = {
		@Index(name = "chromosomeaccession_accession_index", columnList = "accession"),
		@Index(name = "chromosomeaccession_name_assembly_index", columnList = "chromosomename, assemblyidentifier")
	}
)
public class ChromosomeAccession extends AuditedObject {

	private String chromosomeName;

	private String accession;

	private String assemblyIdentifier;

	private Integer displayOrder;

}
