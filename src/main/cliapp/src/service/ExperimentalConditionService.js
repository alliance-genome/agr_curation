import { BaseAuthService } from './BaseAuthService';

export class ExperimentalConditionService extends BaseAuthService {
	saveExperimentalCondition(updatedCondition){
		return this.api.put(`/experimental-condition`, updatedCondition);
	}
}

