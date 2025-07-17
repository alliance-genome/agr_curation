package org.alliancegenome.curation_api.dao.associations;

import static org.alliancegenome.curation_api.constants.EntityFieldConstants.ALLELE_ASSOCIATION_SUBJECT;
import static org.alliancegenome.curation_api.constants.EntityFieldConstants.ALLELE_CONSTRUCT_ASSOCIATION_OBJECT;
import static org.alliancegenome.curation_api.constants.EntityFieldConstants.CONSTRUCT_ASSOCIATION_SUBJECT;
import java.util.HashMap;
import java.util.List;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.associations.AlleleConstructAssociation;
import org.alliancegenome.curation_api.model.input.Pagination;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.hibernate.query.sqm.internal.QuerySqmImpl;
import org.hibernate.query.sqm.tree.select.SqmSelectStatement;
import org.jboss.logging.Logger;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;

@ApplicationScoped
public class AlleleConstructAssociationDAO extends BaseSQLDAO<AlleleConstructAssociation> {

	protected AlleleConstructAssociationDAO() {
		super(AlleleConstructAssociation.class);
	}

}
