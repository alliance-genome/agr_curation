import { SubResourceService } from './SubResourceService';

/**
 * The lists an allele holds through sub-resources of its own, such as `/allele/{id}/crossreferences`. Cross
 * references and gene associations are created, updated or deleted to match a replaced list. References are existing
 * ones the allele is linked to, and any the list omits are detached.
 */
export class AlleleSubResourceService extends SubResourceService {
	constructor() {
		super('allele');
	}
}
