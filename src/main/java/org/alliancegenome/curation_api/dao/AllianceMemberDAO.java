package org.alliancegenome.curation_api.dao;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.AllianceMember;

@ApplicationScoped
public class AllianceMemberDAO extends BaseSQLDAO<AllianceMember> {

	protected AllianceMemberDAO() {
		super(AllianceMember.class);
	}

}
