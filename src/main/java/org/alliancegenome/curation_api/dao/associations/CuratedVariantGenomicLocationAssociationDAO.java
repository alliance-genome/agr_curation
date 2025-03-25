package org.alliancegenome.curation_api.dao.associations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.CuratedVariantGenomicLocationAssociation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CuratedVariantGenomicLocationAssociationDAO extends BaseSQLDAO<CuratedVariantGenomicLocationAssociation> {

	protected CuratedVariantGenomicLocationAssociationDAO() {
		super(CuratedVariantGenomicLocationAssociation.class);
	}

}
