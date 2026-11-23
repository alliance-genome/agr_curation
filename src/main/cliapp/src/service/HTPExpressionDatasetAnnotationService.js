import { BaseAuthService } from './BaseAuthService';

export class HTPExpressionDatasetAnnotationService extends BaseAuthService {
	getHTPExpressionDatasetAnnotation(identifier) {
		return this.api.get(`/htpexpressiondatasetannotation/findBy/${identifier}`);
	}
}
