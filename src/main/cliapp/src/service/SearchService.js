import axios from 'axios';


export class SearchService {

    search(endpoint, rows, page, sorts, filters, sortMapping) {
        const searchOptions = {};
        if (!sorts) {
            sorts = [];
        }

        searchOptions["searchFilters"] = filters;
        searchOptions["sortOrders"] = includeSecondarySorts(sorts, sortMapping);
        // console.log(searchOptions);
        return axios.post(`/api/${endpoint}/search?limit=${rows}&page=${page}`, searchOptions).then(res => res.data);
    }

    find(endpoint, rows, page, findOptions) {
        //console.log(findOptions);
        return axios.post(`/api/${endpoint}/find?limit=${rows}&page=${page}`, findOptions).then(res => res.data);
    }


}

function includeSecondarySorts(sorts, sortMapping) {
    const newSorts = [];

    sorts.forEach(sort => {
        newSorts.push(sort);
        if(sortMapping && sort.field in sortMapping){
            sortMapping[sort.field].forEach(field => {
                let newSort = {}
                newSort["field"] = field;
                newSort["order"] = sort.order;
                newSorts.push(newSort);
            })
        }
    })

    return newSorts;
}
