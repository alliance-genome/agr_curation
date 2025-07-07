package org.alliancegenome.curation_api.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alliancegenome.curation_api.constants.EntityFieldConstants;
import org.alliancegenome.curation_api.dao.associations.CodingSequenceGenomicLocationAssociationDAO;
import org.alliancegenome.curation_api.dao.associations.ExonGenomicLocationAssociationDAO;
import org.alliancegenome.curation_api.dao.associations.TranscriptGenomicLocationAssociationDAO;
import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.AssemblyComponent;
import org.apache.commons.collections.CollectionUtils;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class AssemblyComponentDAO extends BaseSQLDAO<AssemblyComponent> {

	@Inject CodingSequenceGenomicLocationAssociationDAO cdsGenomicLocationAssociationDAO;
	@Inject ExonGenomicLocationAssociationDAO exonGenomicLocationAssociationDAO;
	@Inject TranscriptGenomicLocationAssociationDAO transcriptGenomicLocationAssociationDAO;

	protected AssemblyComponentDAO() {
		super(AssemblyComponent.class);
	}

	public Boolean hasReferencingGenomicLocationAssociations(Long acId) {
		
		Map<String, Object> transcriptParams = new HashMap<>();
		transcriptParams.put(EntityFieldConstants.TRANSCRIPT_GENOMIC_LOCATION_ASSOCIATION_OBJECT + ".id", acId);
		List<Long> results = transcriptGenomicLocationAssociationDAO.findIdsByParams(transcriptParams);
		if (CollectionUtils.isNotEmpty(results)) {
			return true;
		}

		Map<String, Object> exonParams = new HashMap<>();
		exonParams.put(EntityFieldConstants.EXON_GENOMIC_LOCATION_ASSOCIATION_OBJECT + ".id", acId);
		results = exonGenomicLocationAssociationDAO.findIdsByParams(exonParams);
		if (CollectionUtils.isNotEmpty(results)) {
			return true;
		}

		Map<String, Object> cdsParams = new HashMap<>();
		cdsParams.put(EntityFieldConstants.CODING_SEQUENCE_GENOMIC_LOCATION_ASSOCIATION_OBJECT + ".id", acId);
		results = cdsGenomicLocationAssociationDAO.findIdsByParams(cdsParams);
		
		return CollectionUtils.isNotEmpty(results);
	}

}
