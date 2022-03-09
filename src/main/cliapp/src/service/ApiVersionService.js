import axios from 'axios';

export class ApiVersionService {

  getApiVersion() {
    return axios.get('/api/version').then(res => res.data);
  }

  getUserInfo(authState) {
    return axios.get('/api/version/user', { headers: { Authorization: authState.accessToken.tokenType + " " + authState.accessToken.accessToken }}).then(res => res.data);
  }
}