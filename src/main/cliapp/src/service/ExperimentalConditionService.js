import { BaseAuthService } from './BaseAuthService';

export class ExperimentalConditionService extends BaseAuthService {
	saveExperimentalCondition(updatedCondition){
		return this.api.put(`/experimental-condition`, updatedCondition);
	}
	createExperimentalCondition(newExperimentalCondition) { 
		return this.api.post(`/experimental-condition`, newExperimentalCondition);
	}
}

