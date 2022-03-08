import axios from 'axios';
import { BaseAuthService } from './BaseAuthService';

export class ApiVersionService extends BaseAuthService {
  
  //eslint-disable-next-line
  constructor(authState) {
    super(authState);
  }

  getApiVersion() {
    return axios.get('/api/version').then(res => res.data);
  }

  getUserInfo() {
    return axios.get('/api/version/user', this.apiAuthHeader).then(res => res.data);
  }
}