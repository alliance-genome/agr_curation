import {BaseAuthService} from './BaseAuthService';

export class ConditionRelationService extends BaseAuthService {
	saveConditionRelation(updatedConditionRelation) {
		return this.api.put(`/condition-relation`, updatedConditionRelation);
	}

	createConditionRelation(newConditionRelation) { 
		return this.api.post(`/condition-relation`, newConditionRelation);
	}
}



