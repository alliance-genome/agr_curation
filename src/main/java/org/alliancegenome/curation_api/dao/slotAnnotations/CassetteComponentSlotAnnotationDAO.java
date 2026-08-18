package org.alliancegenome.curation_api.dao.slotAnnotations;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.slotAnnotations.CassetteComponentSlotAnnotation;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CassetteComponentSlotAnnotationDAO extends BaseSQLDAO<CassetteComponentSlotAnnotation> {

	protected CassetteComponentSlotAnnotationDAO() {
		super(CassetteComponentSlotAnnotation.class);
	}
}
