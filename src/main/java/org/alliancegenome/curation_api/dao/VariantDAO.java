package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Variant;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class VariantDAO extends BaseSQLDAO<Variant> {

	@Inject DiseaseAnnotationDAO diseaseAnnotationDAO;

	protected VariantDAO() {
		super(Variant.class);
	}
}
