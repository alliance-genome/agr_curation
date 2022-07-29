import { BaseAuthService } from "./BaseAuthService";

export class DeletionService extends BaseAuthService {

	async delete(endpoint, id) {
		try {
			const response = await this.api.delete(`/${endpoint}/${id}`);
			console.log(response);
			return {
				isSuccess: true,
				isError: false,
			}
		} catch (error) {
			console.log(error);
			return {
				isSuccess: false,
				isError: true,
				message: error.response.data.errorMessage
			};
		};
	}
}

