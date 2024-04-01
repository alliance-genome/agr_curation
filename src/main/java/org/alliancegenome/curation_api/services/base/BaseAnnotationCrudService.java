package org.alliancegenome.curation_api.services.base;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.alliancegenome.curation_api.dao.base.BaseSQLDAO;
import org.alliancegenome.curation_api.model.entities.Annotation;
import org.alliancegenome.curation_api.model.entities.ConditionRelation;
import org.alliancegenome.curation_api.response.ObjectResponse;
import org.alliancegenome.curation_api.response.SearchResponse;
import org.alliancegenome.curation_api.util.ProcessDisplayHelper;
import org.apache.commons.collections.CollectionUtils;

public abstract class BaseAnnotationCrudService<E extends Annotation, D extends BaseSQLDAO<E>> extends BaseEntityCrudService<E, D> {

	protected abstract void init();

	public abstract E deprecateOrDeleteAnnotationAndNotes(Long id, Boolean throwApiError, String loadDescription, Boolean deprecate);

	public ObjectResponse<E> get(String identifier) {
		SearchResponse<E> ret = findByField("curie", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<E>(ret.getResults().get(0));
		
		ret = findByField("modEntityId", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<E>(ret.getResults().get(0));
		
		ret = findByField("modInternalId", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<E>(ret.getResults().get(0));
		
		ret = findByField("uniqueId", identifier);
		if (ret != null && ret.getTotalResults() == 1)
			return new ObjectResponse<E>(ret.getResults().get(0));
				
		return new ObjectResponse<E>();
	}
	
	protected List<Long> getAllReferencedConditionRelationIds(D dao) {
		ProcessDisplayHelper pdh = new ProcessDisplayHelper();
		
		List<Long> daIds = dao.findAllIds().getResults();
		pdh.startProcess("Checking DAs for referenced Conditions ", daIds.size());
		
		List<Long> conditionRelationIds = new ArrayList<>();
		daIds.forEach(daId -> {
			E annotation = dao.find(daId);
			if (CollectionUtils.isNotEmpty(annotation.getConditionRelations())) {
				List<Long> crIds = annotation.getConditionRelations().stream().map(ConditionRelation::getId).collect(Collectors.toList());
				conditionRelationIds.addAll(crIds);
			}
			pdh.progressProcess();
		});
		pdh.finishProcess();
		
		return conditionRelationIds;
	}
}
