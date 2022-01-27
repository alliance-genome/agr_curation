export class BaseAuthService {

  apiAuthHeader;

  constructor(authState) {
    if (authState) {
      this.apiAuthHeader = {
        headers: {
          Authorization: authState.accessToken.tokenType + " " + authState.accessToken.accessToken
        }
      }
    }
  }

}
