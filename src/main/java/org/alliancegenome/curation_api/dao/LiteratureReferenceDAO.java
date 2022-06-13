package org.alliancegenome.curation_api.dao;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.dao.BaseESDAO;
import org.alliancegenome.curation_api.model.document.LiteratureReference;

@ApplicationScoped
public class LiteratureReferenceDAO extends BaseESDAO<LiteratureReference> {

	protected LiteratureReferenceDAO() {
		super(LiteratureReference.class, "references_index");
	}

}
