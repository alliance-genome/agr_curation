export class BaseAuthService {

  apiAuthHeader;

  constructor(authState) {
    if (authState && authState.accessToken) {
      this.apiAuthHeader = {
        headers: {
          Authorization: authState.accessToken.tokenType + " " + authState.accessToken.accessToken
        }
      }
    }
  }

}
