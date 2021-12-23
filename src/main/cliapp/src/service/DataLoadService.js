import axios from 'axios';

export class DataLoadService {

    createGroup(newGroupName){
        const newGroup = {
            name: newGroupName
        }
        return axios.post(`api/bulkloadgroup`, newGroup)
    }
}
