import React, { useState, useEffect } from 'react';
import { Card } from 'primereact/card';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { LoggedInPersonService } from '../../service/LoggedInPersonService';
import { useOktaAuth } from '@okta/okta-react';
import { resetThemes } from '../../service/useSessionStorage';

import { ConfirmButton } from '../../components/ConfirmButton';

const initialThemeState = {
	layoutMode: "static",
	layoutColorMode: "dark",
	inputStyle: "outlined",
	ripple: true,
	scale: 14,
	theme: "vela-blue",
};

export const ProfileComponent = () => {

	const [localUserInfo, setLocalUserInfo] = useState({});
	const [oktaToken] = useState(JSON.parse(localStorage.getItem('okta-token-storage')));

	const { authState, oktaAuth } = useOktaAuth();

	const loggedInPersonService = new LoggedInPersonService();

	const globalResetHandler = () =>{
		window.sessionStorage.setItem('globalStateObject', JSON.stringify({}));
		window.location.reload();
	};

	const themeResetHandler = () =>{
		resetThemes(initialThemeState);
		window.location.reload();
	};

	const regenApiTokenr = () => {
		console.log("RegenToken");
		loggedInPersonService.regenApiToken().then((data) => {
			setLocalUserInfo(data);
		}).catch((err) => {
			console.log(err);
		});
	}

	useEffect(() => {
			if (!authState || !authState.isAuthenticated) {
					setLocalUserInfo(null);
			} else {
					loggedInPersonService.getUserInfo().then((data) => {
						setLocalUserInfo(data);
					}).catch((err) => {
						console.log(err);
					});
			}
	}, [authState, oktaAuth, setLocalUserInfo]); // eslint-disable-line react-hooks/exhaustive-deps

	const userInfos = [
		{ name: "Name", value: localUserInfo.firstName + " " + localUserInfo.lastName },
		{ name: "Okta Email", value: localUserInfo.oktaEmail },
		{ name: "Okta Token", value: oktaToken.accessToken.accessToken },
		{ name: "Curation API Token", value: localUserInfo.apiToken },
	];

	const valueTemplate = (props) => {
		console.log(props);
		return (
			<p style={{ wordBreak: "break-all" }}>{ props.value }</p>
		);
	}

	return (
		<div className="grid">
			<div className="col-12">
				<Card title="User Profile" subTitle="">
					<DataTable value={userInfos} style={{ width: "100%"}}>
						<Column field="name" header="Name" />
						<Column field="value" header="Value" body={valueTemplate} />
					</DataTable>
				</Card>
			</div>
			<div className="col-6">
				<div className="grid">
					<div className="col-5">
						<ConfirmButton
							buttonText="Regenerate Curation API Token"
							headerText="Regenerate Curation API Token"
							messageText="Are you sure you want to regenerate the API token? This will immediately invalidate the prior token and it can't be used again."
							acceptHandler={regenApiTokenr}
						/>
					</div>
					<div className="col-4">
						<ConfirmButton
							buttonText="Global State Reset"
							headerText="Global State Reset"
							buttonClassName="p-button-danger mr-3"
							messageText="Are you sure? This will reset the local state of all the data tables and your theme settings."
							acceptHandler={globalResetHandler}
						/>
					</div>
					<div className="col-3">
						<ConfirmButton
							buttonText="Reset Themes"
							headerText="Reset Themes"
							messageText="Are you sure? This will reset your theme settings."
							acceptHandler={themeResetHandler}
						/>
					</div>
				</div>
			</div>
		</div>
	);
};
