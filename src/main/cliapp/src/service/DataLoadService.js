import axios from 'axios';

export class DataLoadService {

  createGroup(newGroup){
    return axios.post(`api/bulkloadgroup`, newGroup);
  }


  restartLoad(loadType, id) {
    let endpoint = loadType.toLowerCase();
    return axios.get(`api/${endpoint}/restart/${id}`);
  }
}
