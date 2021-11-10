import axios from 'axios';

export class DiseaseAnnotationService {

    saveDiseaseAnnotation(updatedAnnotation){
        return axios.put(`api/disease-annotation`, updatedAnnotation)
    }
    getDiseaseAnnotations(limit, page, sorts, filters) {
        console.log("Filters " + JSON.stringify(filters));
        const sortAndFilterOptions = {};

        var sortArray = {};
        if(sorts) {
          sorts.forEach((o) => {
            sortArray[o.field] = o.order;
          });
        }

        // var filterArray = {};
        // if(filters) {
        //   Object.keys(filters).forEach((key) => {
        //     filterArray[key] = filters[key]["value"];
        //   });
        // }

        sortAndFilterOptions["searchFilters"] = filters;
        if(Object.keys(sortArray).length > 0) {
          sortAndFilterOptions["sortOrders"] = sortArray;
        }

        return axios.post(`/api/disease-annotation/search?limit=${limit}&page=${page}`, sortAndFilterOptions).then(res => res.data);
    }
}


