import { SubResourceService } from './SubResourceService';

/**
 * The lists a gene holds through sub-resources of its own, such as `/gene/{id}/crossreferences`. A cross reference
 * matching the gene's GCRP cross reference is rejected.
 */
export class GeneSubResourceService extends SubResourceService {
	constructor() {
		super('gene');
	}
}
