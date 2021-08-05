import axios from 'axios';

export class GeneService {

    getGenes(limit, page) {
        return axios.get('http://localhost:8080/api/gene/all?limit=' + limit + '&page=' + page).then(res => res.data);
    }

}
