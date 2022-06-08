package org.alliancegenome.curation_api.dao;

import javax.enterprise.context.ApplicationScoped;

import org.alliancegenome.curation_api.base.dao.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Molecule;

@ApplicationScoped
public class MoleculeDAO extends BaseSQLDAO<Molecule> {

	protected MoleculeDAO() {
		super(Molecule.class);
	}
	
	public Molecule getByIdOrCurie(String id) {
		return find(id);
	}

}
