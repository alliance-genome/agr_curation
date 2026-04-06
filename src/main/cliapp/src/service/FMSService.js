import { createApiClient } from './ApiClient';

const fmsApi = createApiClient({ baseURL: 'https://fms.alliancegenome.org' });

export class FMSService {
	getReleases() {
		return fmsApi.get('/api/releaseversion/all').then((res) => res.data);
	}

	getSnapshot(release) {
		return fmsApi
			.get(`/api/snapshot/release/${release}`)
			.then((res) => res.data.snapShot)
			.catch((res) => console.log(res));
	}

	getNextRelease() {
		return fmsApi.get('/api/releaseversion/next').then((res) => res.data);
	}

	getDataTypes() {
		return fmsApi.get('/api/datatype/all').then((res) => res.data);
	}

	getDataFiles(dataType, latest = true) {
		return fmsApi.get('/api/datafile/by/' + dataType + '?latest=' + latest).then((res) => res.data);
	}
}
