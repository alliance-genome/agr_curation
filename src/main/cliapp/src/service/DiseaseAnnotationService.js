import axios from 'axios';

export class DiseaseAnnotationService {

    saveDiseaseAnnotation(updatedAnnotation){
        return axios.put(`api/disease-annotation`, updatedAnnotation)
    }
}


