import axios from 'axios';

export class ApiVersionService {
  getApiVersion() {
    return axios.get('/api/version').then(res => res.data);
  }
}