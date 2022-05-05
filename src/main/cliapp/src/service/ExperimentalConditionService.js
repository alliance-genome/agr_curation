import axios from 'axios';
import { BaseAuthService } from './BaseAuthService';

export class ExperimentalConditionService extends BaseAuthService{
  saveExperimentalCondition(updatedCondition){
    return this.api.put(`api/experimental-condition`, updatedCondition);
  }
}

