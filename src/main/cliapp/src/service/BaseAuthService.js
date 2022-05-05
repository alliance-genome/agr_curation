import axios from 'axios';

export class BaseAuthService {

  api;

  constructor() {
    let oktaTokenStorage = localStorage.getItem('okta-token-storage');
    const { accessToken } = JSON.parse(oktaTokenStorage);
    if (accessToken) {
      this.api = axios.create({
        baseURL: "/api",
        headers: {
          Authorization: "Bearer " + accessToken.accessToken
        }
      });
    } else {
      console.log('No accessToken');
    }
  }

}
