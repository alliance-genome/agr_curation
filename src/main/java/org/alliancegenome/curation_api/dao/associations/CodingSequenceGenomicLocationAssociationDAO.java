package org.alliancegenome.curation_api.dao.associations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.CodingSequenceGenomicLocationAssociation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CodingSequenceGenomicLocationAssociationDAO extends BaseSQLDAO<CodingSequenceGenomicLocationAssociation> {

	protected CodingSequenceGenomicLocationAssociationDAO() {
		super(CodingSequenceGenomicLocationAssociation.class);
	}

}
