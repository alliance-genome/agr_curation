import axios from 'axios';
import { BaseAuthService } from './BaseAuthService';


export class ValidationService extends BaseAuthService {
  //eslint-disable-next-line
  constructor(authState) {
    super(authState);
  }

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


