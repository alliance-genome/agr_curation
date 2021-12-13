import axios from 'axios';
import { getSecondarySorts } from '../utils/utils';

export class SearchService {

    search(endpoint, rows, page, sorts, filters) {
        const searchOptions = {};
        if (!sorts) {
            sorts = [];
        }
        sorts = getSecondarySorts(sorts);
        // console.log(sorts);
        searchOptions["searchFilters"] = filters;
        searchOptions["sortOrders"] = sorts;
        // console.log(searchOptions);
        return axios.post(`/api/${endpoint}/search?limit=${rows}&page=${page}`, searchOptions).then(res => res.data);
    }
}
