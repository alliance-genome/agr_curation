package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseESDAO;
import org.alliancegenome.curation_api.model.document.LiteratureReference;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LiteratureReferenceDAO extends BaseESDAO<LiteratureReference> {

	protected LiteratureReferenceDAO() {
		super(LiteratureReference.class, "references_index");
	}

}
