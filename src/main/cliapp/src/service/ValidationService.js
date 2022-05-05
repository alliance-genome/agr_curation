import { BaseAuthService } from './BaseAuthService';


export class ValidationService extends BaseAuthService {
  async validate(endpoint, objectToValidate) {
    try {
      const response = await this.api.post(`/${endpoint}/validate`, objectToValidate);
      return {
        isSuccess: true,
        isError: false,
        data: response.entity
      }
    } catch (error) {
      return {
        isSuccess: false,
        isError: true,
        data: error.response.data.errorMessages
      };
    };
  }
}


