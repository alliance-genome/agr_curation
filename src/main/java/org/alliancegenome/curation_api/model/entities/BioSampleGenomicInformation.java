package org.alliancegenome.curation_api.model.entities;

import org.alliancegenome.curation_api.model.entities.base.AuditedObject;

public class BioSampleGenomicInformation extends AuditedObject {
    
    private Allele bioSampleAllele;

    private AffectedGenomicModel bioSampleAgm;

    private VocabularyTerm bioSampleAgmType;

    private String bioSampleText;

}
