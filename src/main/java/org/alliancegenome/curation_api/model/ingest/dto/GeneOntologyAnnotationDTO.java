package org.alliancegenome.curation_api.model.ingest.dto;

import org.alliancegenome.curation_api.model.ingest.dto.base.BaseDTO;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GeneOntologyAnnotationDTO extends BaseDTO {
	private String db;
	private String dbObjectId;
	private String dbObjectSymbol;
	private String qualifier;
	private String goId;
	private String dbReference;
	private String evidenceCode;
	private String with;
	private String aspect;
	private String dbObjectName;
	private String dbObjectSynonym;
	private String dbObjectType;
	private String taxon;
	private String date;
	private String assignedBy;
	private String annoationExtension;
	private String geneProductFormId;
}
