import { BaseAuthService } from './BaseAuthService';

export class DiseaseAnnotationService extends BaseAuthService {
	saveDiseaseAnnotation(updatedAnnotation) {
		const { type } = updatedAnnotation;
		let endpoint;
		if (type in typeEndpoints) {
			endpoint = typeEndpoints[type];
		}
		return this.api.put(`/${endpoint}-disease-annotation`, updatedAnnotation);
	}
}

const typeEndpoints = {
	"AGMDiseaseAnnotation": "agm",
	"AlleleDiseaseAnnotation": "allele",
	"GeneDiseaseAnnotation": "gene"
}


