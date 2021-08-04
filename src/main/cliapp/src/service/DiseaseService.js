import axios from 'axios';

export class DiseaseService {

    getDiseases(limit, page) {
        return axios.get('http://localhost:8080/api/doterm/all?limit=' + limit + '&page=' + page).then(res => res.data);
    }

}
