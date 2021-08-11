import axios from 'axios';

export class DiseaseAnnotationService {
    getDiseaseAnnotations(limit, page) {
        return axios.get('assets/demo/data/diseaseAnnotations.json')
            .then(res => res.data.data);
    }
}