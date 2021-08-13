import axios from 'axios';

export class GeneService {
	getGenes(limit, page, sorts, filters) {
		var sortOptions = {};

		var sortArray = {};
		sorts.forEach((o) => {
			sortArray[o.field] = o.order;
		});

		var filterArray = {};

		Object.keys(filters).forEach((key) => {
			filterArray[key] = filters[key]["value"];
		});

		if(Object.keys(filterArray).length > 0) {
			sortOptions["searchFilters"] = filterArray;
		}
		if(Object.keys(sortArray).length > 0) {
			sortOptions["sortOrders"] = sortArray;
		}

		return axios.post('/api/gene/search?limit=' + limit + '&page=' + page, sortOptions).then(res => res.data);
	}
}
