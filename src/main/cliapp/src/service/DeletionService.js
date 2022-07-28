import { BaseAuthService } from "./BaseAuthService";

export class DeletionService extends BaseAuthService {

	async delete(endpoint, id) {
		try {
			const response = await this.api.delete(`/${endpoint}/${id}`);
			return {
				isSuccess: true,
				isError: false,
			}
		} catch (error) {
			return {
				isSuccess: false,
				isError: true,
				message: error.response.dataerrorMessage
			};
		};
	}
}

