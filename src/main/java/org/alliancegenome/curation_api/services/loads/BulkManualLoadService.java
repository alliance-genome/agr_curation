package org.alliancegenome.curation_api.services.loads;

import org.alliancegenome.curation_api.dao.loads.BulkManualLoadDAO;
import org.alliancegenome.curation_api.jobs.events.PendingBulkLoadJobEvent;
import org.alliancegenome.curation_api.model.entities.Species;
import org.alliancegenome.curation_api.model.entities.bulkloads.BulkManualLoad;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.SpeciesService;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.event.Event;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class BulkManualLoadService extends BaseEntityCrudService<BulkManualLoad, BulkManualLoadDAO> {

	@Inject BulkManualLoadDAO bulkManualLoadDAO;
	@Inject SpeciesService speciesService;

	@Inject Event<PendingBulkLoadJobEvent> pendingJobEvents;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(bulkManualLoadDAO);
	}

	@Override
	@Transactional
	public ObjectResponse<BulkManualLoad> create(BulkManualLoad entity) {
		resolveSpecies(entity);
		return super.create(entity);
	}

	@Override
	@Transactional
	public ObjectResponse<BulkManualLoad> update(BulkManualLoad entity) {
		resolveSpecies(entity);
		return super.update(entity);
	}

	private void resolveSpecies(BulkManualLoad load) {
		Species species = load.getSpecies();
		if (species == null || species.getId() != null) {
			return;
		}
		load.setSpecies(speciesService.getByDisplayName(species.getDisplayName()));
	}

}
