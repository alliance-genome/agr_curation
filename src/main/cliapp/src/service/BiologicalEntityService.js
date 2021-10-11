import axios from 'axios';

export class BiologicalEntityService {
    getBiologicalEntities(limit, page, sorts, filters) {
        console.log("Filters " + JSON.stringify(filters));
        let sortOptions = {};

        let sortArray = {};
        if(sorts) {
            sorts.forEach((o) => {
                sortArray[o.field] = o.order;
            });
        }

        let filterArray = {};
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

        return axios.post(`/api/biologicalentity/search?limit=${limit}&page=${page}`, sortOptions).then(res => res.data);
    }
}


