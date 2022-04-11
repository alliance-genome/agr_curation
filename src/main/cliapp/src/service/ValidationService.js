import axios from 'axios';
import { BaseAuthService } from './BaseAuthService';


export class ValidationService extends BaseAuthService {
  constructor(authState) {
    super(authState);
  }

  async validate(endpoint, objectToValidate) {
    try {
      const response = await axios.post(`/api/${endpoint}/validate`, objectToValidate, this.apiAuthHeader);
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


