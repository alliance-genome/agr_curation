import axios from 'axios';

export class DiseaseAnnotationService {

    saveDiseaseAnnotation(updatedAnnotation){
        const { type } = updatedAnnotation;
        let endpoint;
        if(type in typeEndpoints){
            endpoint = typeEndpoints[type];
        }
        return axios.put(`api/${endpoint}-disease-annotation`, updatedAnnotation)
    }
}

const typeEndpoints = {
    "AGMDiseaseAnnotation": "agm",
    "AlleleDiseaseAnnotation": "allele",
    "GeneDiseaseAnnotation": "gene"
}


