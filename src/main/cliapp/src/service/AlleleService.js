import axios from 'axios';

export class AlleleService {
    getAlleles(limit, page) {
        return axios.get('/api/allele/all?limit=' + limit + '&page=' + page).then(res => res.data);
    }
}
