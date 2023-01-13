import { SearchService } from './SearchService';

export class ExperimentsSearchService extends SearchService {
	findExperiments(endpoint, rows, page, findOptions) {
		//console.log(findOptions);
		return this.api.post(`/${endpoint}/find-experiments?limit=${rows}&page=${page}`, findOptions).then(res => res.data);
	}

}
