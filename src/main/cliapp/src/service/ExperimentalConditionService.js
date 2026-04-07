import { BaseAuthService } from './BaseAuthService';
import { DeletionService } from './DeletionService';
import { Endpoints } from '../constants/Endpoints';

export class ExperimentalConditionService extends BaseAuthService {
	saveExperimentalCondition(updatedCondition) {
		return this.api.put(`/experimental-condition`, updatedCondition);
	}
	createExperimentalCondition(newExperimentalCondition) {
		return this.api.post(`/experimental-condition`, newExperimentalCondition);
	}

	async deleteExperimentalCondition(experimentalCondition) {
		const deletionService = new DeletionService();
		return await deletionService.delete(Endpoints.Annotation.EXPERIMENTAL_CONDITION, experimentalCondition.id);
	}
}
