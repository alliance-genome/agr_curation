import axios from 'axios';

export class DiseaseAnnotationService {

    saveDiseaseAnnotation(updatedAnnotation){
        return axios.put(`api/disease-annotation`, updatedAnnotation)
    }
    getDiseaseAnnotations(limit, page, sorts, filters) {
        console.log("Filters " + JSON.stringify(filters));
        const sortOptions = {};

        const sortArray = {};
        if(sorts) {
          sorts.forEach((o) => {
            sortArray[o.field] = o.order;
          });
        }

        const filterArray = {};
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


