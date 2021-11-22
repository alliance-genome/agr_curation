import axios from 'axios';

export class DiseaseAnnotationService {

    saveDiseaseAnnotation(updatedAnnotation){
        return axios.put(`api/disease-annotation`, updatedAnnotation)
    }
    getDiseaseAnnotations(limit, page, sorts, filters) {//fix me
        const sortAndFilterOptions = {};

        sortAndFilterOptions["searchFilters"] = filters;
        sortAndFilterOptions["sortOrders"] = sorts;

        return axios.post(`/api/disease-annotation/search?limit=${limit}&page=${page}`, sortAndFilterOptions).then(res => res.data);
    }
}


