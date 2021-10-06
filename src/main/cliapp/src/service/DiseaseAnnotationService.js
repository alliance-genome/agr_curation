import axios from 'axios';

export class DiseaseAnnotationService {

    saveDiseaseAnnotation(newAnnotation){
        return axios.put(`api/disease-annotation`, newAnnotation)
    }
    getDiseaseAnnotations(limit, page, sorts, filters) {
        var sortOptions = {};

        var sortArray = {};
        if(sorts) {
          sorts.forEach((o) => {
            sortArray[o.field] = o.order;
          });
        }

        var filterArray = {};
        if(filters) {
          Object.keys(filters).forEach((key) => {
            filterArray[key] = filters[key]["value"];
          });
        }

        if(Object.keys(filterArray).length > 0) {
          sortOptions["searchFilters"] = filterArray;
        }
        if(Object.keys(sortArray).length > 0) {
          sortOptions["sortOrders"] = sortArray;
        }

        return axios.post(`/api/disease-annotation/search?limit=${limit}&page=${page}`, sortOptions).then(res => res.data);
    }
}


