import { BaseAuthService } from './BaseAuthService';

export class HTPExpressionDatasetSampleAnnotationService extends BaseAuthService {
	getHTPExpressionDatasetSampleAnnotation(identifier) {
		return this.api.get(`/htpexpressiondatasetsampleannotation/findBy/${identifier}`);
	}
}
