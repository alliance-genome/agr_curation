//This is in place so that using the service methods don't trigger an error due to the lack of an accessToken
import { BaseAuthService } from "./BaseAuthService";

export class PersonSettingsService extends BaseAuthService {
	getUserSettings(){
		console.log("in mock person settings service")
	}
}
