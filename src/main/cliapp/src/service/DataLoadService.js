import axios from 'axios';

export class DataLoadService {

  createGroup(newGroup){
    return axios.post(`api/bulkloadgroup`, newGroup)
  }
}
