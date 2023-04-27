package org.alliancegenome.curation_api.dao;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Molecule;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MoleculeDAO extends BaseSQLDAO<Molecule> {

	protected MoleculeDAO() {
		super(Molecule.class);
	}

	public Molecule getByIdOrCurie(String id) {
		return find(id);
	}

}
