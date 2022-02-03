import axios from 'axios';
import { BaseAuthService } from './BaseAuthService';

export class ExperimentalConditionService extends BaseAuthService{
  //eslint-disable-next-line
  constructor(authState) {
    super(authState);
  }

  saveExperimentalCondition(updatedCondition){
    return axios.put(`api/experimental-condition`, updatedCondition, this.apiAuthHeader);
  }
}

