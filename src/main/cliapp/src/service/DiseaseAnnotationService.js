import axios from 'axios';
import { BaseAuthService } from './BaseAuthService';

export class DiseaseAnnotationService extends BaseAuthService {
  //eslint-disable-next-line
  constructor(authState) {
    super(authState);
  }

  saveDiseaseAnnotation(updatedAnnotation) {
    const { type } = updatedAnnotation;
    let endpoint;
    if (type in typeEndpoints) {
      endpoint = typeEndpoints[type];
    }
    return axios.put(`api/${endpoint}-disease-annotation`, updatedAnnotation, this.apiAuthHeader);
  }
}

const typeEndpoints = {
  "AGMDiseaseAnnotation": "agm",
  "AlleleDiseaseAnnotation": "allele",
  "GeneDiseaseAnnotation": "gene"
}


