import axios from 'axios';

export class ExperimentalConditionService {

    saveExperimentalCondition(updatedCondition){
        return axios.put(`api/experimental-condition`, updatedCondition)
    }
}

