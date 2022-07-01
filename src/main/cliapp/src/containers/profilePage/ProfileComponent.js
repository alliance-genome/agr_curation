import React, { useState, useEffect } from 'react';
import { Card } from 'primereact/card';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { LoggedInPersonService } from '../../service/LoggedInPersonService';
import { useOktaAuth } from '@okta/okta-react';
import { Button } from 'primereact/button';
import { resetThemes } from '../../service/useSessionStorage';
import { genericConfirmDialog } from '../../utils/utils';

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

	const globalConfirm = () => {
		genericConfirmDialog({
			header: "Global State Reset",
			message: "Are you sure? This will reset the local state of all the data tables and your theme settings.",
			accept: globalResetHandler
		});
	}

	const themeConfirm = () => {
		genericConfirmDialog({
			header: "Theme State Reset",
			message: "Are you sure? This will reset your theme settings.",
			accept: themeResetHandler
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
		{ name: "Email", value: localUserInfo.email },
		{ name: "Okta Token", value: oktaToken.accessToken.accessToken },
		{ name: "Curation API Token", value: localUserInfo.apiToken },
	];

	const header = () => {
		return (
			<div className='card' style={{textAlign: "right"}}>
				<Button onClick={globalConfirm} className="p-button-danger mr-3">Global State Reset</Button>
				<Button onClick={themeConfirm}>Reset Themes</Button>
			</div>
		)
	};

	return (
		<Card title="User Profile" subTitle="">
			<DataTable value={userInfos} header={header}>
				 <Column field="name" header="Name" />
				 <Column field="value" header="Value" />
			</DataTable>
		</Card>
	);
};
