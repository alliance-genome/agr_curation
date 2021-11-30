import axios from 'axios';

export class SearchService {

    search(endpoint, rows, page, sorts, filters) {
        const searchOptions = {};
        if(!sorts){
            sorts = [];
        }
        searchOptions["searchFilters"] = filters;
        searchOptions["sortOrders"] = sorts;
        //console.log(searchOptions);
        return axios.post(`/api/${endpoint}/search?limit=${rows}&page=${page}`, searchOptions).then(res => res.data);
    }
}
