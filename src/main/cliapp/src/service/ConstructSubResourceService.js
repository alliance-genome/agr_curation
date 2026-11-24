import { SubResourceService } from './SubResourceService';

/**
 * The lists a construct holds through sub-resources of its own, such as `/construct/{id}/synonyms`.
 */
export class ConstructSubResourceService extends SubResourceService {
	constructor() {
		super('construct');
	}
}
