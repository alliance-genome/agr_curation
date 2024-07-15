package org.alliancegenome.curation_api.services;

import java.time.OffsetDateTime;
import java.util.List;

import org.alliancegenome.curation_api.dao.GeneInteractionDAO;
import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.GeneInteraction;
import org.alliancegenome.curation_api.model.entities.GeneMolecularInteraction;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;

@RequestScoped
public class GeneInteractionService extends BaseEntityCrudService<GeneInteraction, GeneInteractionDAO> {

	@Inject GeneInteractionDAO geneInteractionDAO;
	@Inject PersonService personService;
	@Inject PersonDAO personDAO;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(geneInteractionDAO);
	}

	public ObjectResponse<GeneInteraction> getByIdentifer(String identifier) {
		GeneInteraction interaction = findByAlternativeFields(List.of("interactionId", "uniqueId"), identifier);
		return new ObjectResponse<GeneInteraction>(interaction);
	}

	public GeneInteraction deprecateOrDeleteInteraction(Long id, Boolean throwApiError, String loadDescription, Boolean deprecateInteraction) {
		GeneInteraction interaction = geneInteractionDAO.find(id);

		if (interaction == null) {
			String errorMessage = "Could not find Gene Interaction with id: " + id;
			if (throwApiError) {
				ObjectResponse<GeneMolecularInteraction> response = new ObjectResponse<>();
				response.addErrorMessage("id", errorMessage);
				throw new ApiErrorException(response);
			}
			Log.error(errorMessage);
			return null;
		}

		if (deprecateInteraction) {
			if (!interaction.getObsolete()) {
				interaction.setObsolete(true);
				if (authenticatedPerson.getId() != null) {
					interaction.setUpdatedBy(personDAO.find(authenticatedPerson.getId()));
				} else {
					interaction.setUpdatedBy(personService.fetchByUniqueIdOrCreate(loadDescription));
				}
				interaction.setDateUpdated(OffsetDateTime.now());
				return geneInteractionDAO.persist(interaction);
			} else {
				return interaction;
			}
		} else {
			geneInteractionDAO.remove(id);
		}

		return null;

	}
}
