package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.AllianceMember;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AllianceMemberDAO extends BaseSQLDAO<AllianceMember> {

	protected AllianceMemberDAO() {
		super(AllianceMember.class);
	}

}
