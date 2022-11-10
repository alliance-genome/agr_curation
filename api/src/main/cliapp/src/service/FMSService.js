import axios from 'axios';

export class FMSService {

		getReleases() {
			return axios.get('https://fms.alliancegenome.org/api/releaseversion/all').then(res => res.data);
		}

		getSnapshot(release) {
			return axios.get(`https://fms.alliancegenome.org/api/snapshot/release/${release}`).then(res => res.data.snapShot);
		}

}