import axios from 'axios';

export class GeneService {
    getGenes(limit, page) {
        return axios.get('/api/gene/all?limit=' + limit + '&page=' + page).then(res => res.data);
    }
}
