import { BaseAuthService } from './BaseAuthService';
import { DeletionService } from './DeletionService';
import { Endpoints } from '../constants/Endpoints';

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

	createDiseaseAnnotation(annotation) {
		let newAnnotation = { ...annotation };

		const { type } = annotation.diseaseAnnotationSubject ? newAnnotation.diseaseAnnotationSubject : '';

		newAnnotation['type'] = subectAnnotationLookup[type];
		let endpoint;
		if (type in subjectTypeEndpoints) {
			endpoint = subjectTypeEndpoints[type];
		}
		return this.api.post(`/${endpoint}-disease-annotation`, newAnnotation);
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
	AGMDiseaseAnnotation: Endpoints.Entity.AGM,
	AlleleDiseaseAnnotation: Endpoints.Entity.ALLELE,
	GeneDiseaseAnnotation: Endpoints.Entity.GENE,
};

const subectAnnotationLookup = {
	AffectedGenomicModel: 'AGMDiseaseAnnotation',
	Allele: 'AlleleDiseaseAnnotation',
	Gene: 'GeneDiseaseAnnotation',
};

const subjectTypeEndpoints = {
	AffectedGenomicModel: Endpoints.Entity.AGM,
	Allele: Endpoints.Entity.ALLELE,
	Gene: Endpoints.Entity.GENE,
};
