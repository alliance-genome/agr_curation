import { BaseAuthService } from './BaseAuthService';
import { DeletionService } from './DeletionService';
import { Endpoints } from '../constants/Endpoints';

export class ConditionRelationService extends BaseAuthService {
	saveConditionRelation(updatedConditionRelation) {
		return this.api.put(`/condition-relation`, updatedConditionRelation);
	}

	createConditionRelation(newConditionRelation) {
		return this.api.post(`/condition-relation`, newConditionRelation);
	}

	async deleteConditionRelation(conditionRelation) {
		const deletionService = new DeletionService();
		return await deletionService.delete(Endpoints.Annotation.CONDITION_RELATION, conditionRelation.id);
	}
}
