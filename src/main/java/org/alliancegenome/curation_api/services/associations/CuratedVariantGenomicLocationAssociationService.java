package org.alliancegenome.curation_api.services.associations;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.PersonDAO;
import org.alliancegenome.curation_api.dao.associations.CuratedVariantGenomicLocationAssociationDAO;
import org.alliancegenome.curation_api.enums.BackendBulkDataProvider;
import org.alliancegenome.curation_api.exceptions.ApiErrorException;
import org.alliancegenome.curation_api.model.entities.Variant;
import org.alliancegenome.curation_api.model.entities.associations.CuratedVariantGenomicLocationAssociation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.services.PersonService;
import org.alliancegenome.curation_api.services.base.BaseEntityCrudService;
import org.alliancegenome.curation_api.services.validation.dto.fms.VariantFmsDTOValidator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import io.quarkus.logging.Log;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@RequestScoped
public class CuratedVariantGenomicLocationAssociationService extends BaseEntityCrudService<CuratedVariantGenomicLocationAssociation, CuratedVariantGenomicLocationAssociationDAO> {

	@Inject CuratedVariantGenomicLocationAssociationDAO curatedVariantGenomicLocationAssociationDAO;
	@Inject VariantFmsDTOValidator variantFmsDtoValidator;
	@Inject PersonDAO personDAO;
	@Inject PersonService personService;

	@Override
	@PostConstruct
	protected void init() {
		setSQLDao(curatedVariantGenomicLocationAssociationDAO);
	}


	public List<Long> getIdsByDataProvider(BackendBulkDataProvider dataProvider) {
		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.VARIANT_ASSOCIATION_SUBJECT_DATA_PROVIDER, dataProvider.sourceOrganization);
		if (StringUtils.equals(dataProvider.sourceOrganization, "RGD")) {
			params.put(EntityFieldConstants.VARIANT_ASSOCIATION_SUBJECT_TAXON, dataProvider.canonicalTaxonCurie);
		}
		List<Long> associationIds = curatedVariantGenomicLocationAssociationDAO.findIdsByParams(params);
		associationIds.removeIf(Objects::isNull);

		return associationIds;
	}

	public ObjectResponse<CuratedVariantGenomicLocationAssociation> getLocationAssociation(Long exonId, Long assemblyComponentId) {
		CuratedVariantGenomicLocationAssociation association = null;

		Map<String, Object> params = new HashMap<>();
		params.put(EntityFieldConstants.VARIANT_ASSOCIATION_SUBJECT + ".id", exonId);
		params.put(EntityFieldConstants.VARIANT_GENOMIC_LOCATION_ASSOCIATION_OBJECT + ".id", assemblyComponentId);

		SearchResponse<CuratedVariantGenomicLocationAssociation> resp = curatedVariantGenomicLocationAssociationDAO.findByParams(params);
		if (resp != null && resp.getSingleResult() != null) {
			association = resp.getSingleResult();
		}

		ObjectResponse<CuratedVariantGenomicLocationAssociation> response = new ObjectResponse<>();
		response.setEntity(association);

		return response;
	}

	public void addAssociationToSubject(CuratedVariantGenomicLocationAssociation association) {
		Variant variant = association.getVariantAssociationSubject();

		List<CuratedVariantGenomicLocationAssociation> currentSubjectAssociations = variant.getCuratedVariantGenomicLocations();
		if (currentSubjectAssociations == null) {
			currentSubjectAssociations = new ArrayList<>();
		}

		List<Long> currentSubjectAssociationIds = currentSubjectAssociations.stream()
				.map(CuratedVariantGenomicLocationAssociation::getId).collect(Collectors.toList());

		if (!currentSubjectAssociationIds.contains(association.getId())) {
			currentSubjectAssociations.add(association);
		}
	}

	@Override
	@Transactional
	public CuratedVariantGenomicLocationAssociation deprecateOrDelete(Long id, Boolean throwApiError, String requestSource, Boolean forceDeprecate) {
		CuratedVariantGenomicLocationAssociation cvgla = curatedVariantGenomicLocationAssociationDAO.find(id);
		if (cvgla != null) {
			if (forceDeprecate || CollectionUtils.isNotEmpty(cvgla.getPredictedVariantConsequences())) {
				if (!cvgla.getObsolete()) {
					cvgla.setUpdatedBy(personService.fetchByUniqueIdOrCreate(requestSource));
					cvgla.setDateUpdated(OffsetDateTime.now());
					cvgla.setObsolete(true);
					return curatedVariantGenomicLocationAssociationDAO.persist(cvgla);
				} else {
					return cvgla;
				}
			} else {
				curatedVariantGenomicLocationAssociationDAO.remove(id);
			}
		} else {
			String errorMessage = "Could not find CuratedVariantGenomicLocationAssociation with id: " + id;
			if (throwApiError) {
				ObjectResponse<CuratedVariantGenomicLocationAssociation> response = new ObjectResponse<>();
				response.addErrorMessage("id", errorMessage);
				throw new ApiErrorException(response);
			}
			Log.error(errorMessage);
		}
		return null;
	}

	public static final Map<String, Integer> SORTED_VARIANT_CONSEQUENCE_MAP = Map.ofEntries(
		Map.entry("transcript_ablation", 1),
		Map.entry("splice_acceptor_variant", 2),
		Map.entry("splice_donor_variant", 3),
		Map.entry("stop_gained", 4),
		Map.entry("frameshift_variant", 5),
		Map.entry("stop_lost", 6),
		Map.entry("start_lost", 7),
		Map.entry("transcript_amplification", 8),
		Map.entry("feature_elongation", 9),
		Map.entry("feature_truncation", 10),
		Map.entry("inframe_insertion", 11),
		Map.entry("inframe_deletion", 12),
		Map.entry("missense_variant", 13),
		Map.entry("protein_altering_variant", 14),
		Map.entry("splice_donor_5th_base_variant", 15),
		Map.entry("splice_region_variant", 16),
		Map.entry("splice_donor_region_variant", 17),
		Map.entry("splice_polypyrimidine_tract_variant", 18),
		Map.entry("incomplete_terminal_codon_variant", 19),
		Map.entry("start_retained_variant", 20),
		Map.entry("stop_retained_variant", 21),
		Map.entry("synonymous_variant", 22),
		Map.entry("coding_sequence_variant", 23),
		Map.entry("mature_miRNA_variant", 24),
		Map.entry("5_prime_UTR_variant", 25),
		Map.entry("3_prime_UTR_variant", 26),
		Map.entry("non_coding_transcript_exon_variant", 27),
		Map.entry("intron_variant", 28),
		Map.entry("NMD_transcript_variant", 29),
		Map.entry("non_coding_transcript_variant", 30),
		Map.entry("coding_transcript_variant", 31),
		Map.entry("upstream_gene_variant", 32),
		Map.entry("downstream_gene_variant", 33),
		Map.entry("TFBS_ablation", 34),
		Map.entry("TFBS_amplification", 35),
		Map.entry("TF_binding_site_variant", 36),
		Map.entry("regulatory_region_ablation", 37),
		Map.entry("regulatory_region_amplification", 38),
		Map.entry("regulatory_region_variant", 39),
		Map.entry("intergenic_variant", 40),
		Map.entry("sequence_variant", 41)
	);


}
