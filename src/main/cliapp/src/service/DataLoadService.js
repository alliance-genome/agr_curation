import { BaseAuthService } from './BaseAuthService';

export class DataLoadService extends BaseAuthService {
		createGroup(newGroup) {
				return this.api.post(`/bulkloadgroup`, newGroup);
		}

		deleteGroup(id) {
				return this.api.delete(`/bulkloadgroup/${id}`);
		}

		createLoad(newLoad) {
				let endpoint = newLoad.type.toLowerCase();

				newLoad.group = { id: newLoad.group };
				for (const objectKey in newLoad) {
						if (!newLoad[objectKey]) {
								delete newLoad[objectKey];
						}
				}
				console.log("Creating: ");
				console.log(newLoad);
				return this.api.post(`/${endpoint}`, newLoad);
		}

		updateLoad(newLoad) {

				let endpoint = newLoad.type.toLowerCase();

				newLoad.group = { id: newLoad.group };
				for (const objectKey in newLoad) {
						if (!newLoad[objectKey]) {
								delete newLoad[objectKey];
						}
				}
				delete newLoad["loadFiles"];
				console.log("Saving: ");
				console.log(newLoad);
				return this.api.put(`/${endpoint}`, newLoad);
		}

		deleteLoad(loadType, id) {
				let endpoint = loadType.toLowerCase();
				return this.api.delete(`/${endpoint}/${id}`);
		}

		restartLoad(loadType, id) {
				let endpoint = loadType.toLowerCase();
				return this.api.get(`/${endpoint}/restart/${id}`);
		}

		restartLoadFile(id) {
			return this.api.get(`/bulkloadfile/restart/${id}`);
		}

		getFileHistoryFile(id) {
			return this.api.get(`/bulkloadfilehistory/${id}`);
		}

		getHistoryExceptions(historyId, rows, page) {
			var searchObject ={
				"bulkLoadFileHistory.id" : historyId
			}
			return this.api.post(`/bulkloadfileexception/find?limit=${rows}&page=${page}`, searchObject);
		}

		deleteLoadFile(id) {
			return this.api.delete(`/bulkloadfile/${id}`);
		}

		getBackendBulkLoadTypes(loadType) {
				const bulkLoadTypes = {
						BulkFMSLoad: ["MOLECULE", "ORTHOLOGY"],
						BulkURLLoad: ["ONTOLOGY", "GENE", "ALLELE", "AGM", "DISEASE_ANNOTATION", "RESOURCE_DESCRIPTOR"],
						BulkManualLoad: ["FULL_INGEST", "DISEASE_ANNOTATION", "GENE_DISEASE_ANNOTATION", "ALLELE_DISEASE_ANNOTATION", "AGM_DISEASE_ANNOTATION", "GENE", "ALLELE", "AGM" ]
				};
				return bulkLoadTypes[loadType];
		}

		getLoadTypes() {
				return [
						"BulkFMSLoad", "BulkURLLoad", "BulkManualLoad"
				];
		}

		getOntologyTypes() {
				return [
					"APO", "ATP", "CHEBI", "DAO", "DO", "DPO", "ECO", "EMAPA", "FBDV", "GO", "HP", "MA", "MI", "MMO", "MPATH", "MOD", "MMUSDV", "MP", "OBI", "PATO", "RO", "RS", "SO", "UBERON", "VT", "WBBT", "WBLS", "WBPheno", "XBA_XBS", "XBED", "XCO", "XPO", "XSMO", "ZECO", "ZFA", "ZFS"
				];
		}

		getDataProviders() {
				return [
					"FB", "MGI", "OMIM", "RGD", "SGD", "WB", "XB", "ZFIN"
				];
		}


}
