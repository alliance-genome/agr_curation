import axios from 'axios';

export class FMSService {
    
    getRelease() {
        return axios.get('https://fms.alliancegenome.org/api/snapshot/release/4.1.0').then(res => res.data);
    }

}
