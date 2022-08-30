import { BaseAuthService } from './BaseAuthService';
import { DeletionService } from './DeletionService';

export class DiseaseAnnotationService extends BaseAuthService {
	saveDiseaseAnnotation(updatedAnnotation) {
		const endpoint = findEndpointType(updatedAnnotation);
		return this.api.put(`/${endpoint}`, updatedAnnotation);
	}

	async deleteDiseaseAnnotation(updatedAnnotation) {
		const endpoint = findEndpointType(updatedAnnotation);
		const deletionService = new DeletionService();
		return await deletionService.delete(endpoint, updatedAnnotation.id);
	}
}

const findEndpointType = (updatedAnnotation) => {
	const { type } = updatedAnnotation;
	let endpointType;
	if (type in typeEndpoints) {
		endpointType = typeEndpoints[type];
	}
	return `${endpointType}-disease-annotation`;
};

const typeEndpoints = {
	"AGMDiseaseAnnotation": "agm",
	"AlleleDiseaseAnnotation": "allele",
	"GeneDiseaseAnnotation": "gene"
}


