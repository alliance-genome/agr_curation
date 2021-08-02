import axios from 'axios';

export class GeneService {
    
    getGenes() {
        return axios.get('http://localhost:8080/api/gene/all').then(res => res.data);
    }

}
