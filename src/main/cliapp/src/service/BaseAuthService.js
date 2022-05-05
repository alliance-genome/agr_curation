import axios from 'axios';

export class BaseAuthService {

  apiAuthHeader;
  api;

  constructor(authState) {
    if (authState && authState.accessToken) {
      this.api = axios.create({
        baseURL: "/api",
        headers: {
          Authorization: authState.accessToken.tokenType + " " + authState.accessToken.accessToken
        }
      });
    }
  }

}
