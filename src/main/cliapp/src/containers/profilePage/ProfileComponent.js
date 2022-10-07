import React, { useState, useEffect } from 'react';
import { Card } from 'primereact/card';
import { DataTable } from 'primereact/datatable';
import { Column } from 'primereact/column';
import { LoggedInPersonService } from '../../service/LoggedInPersonService';
import { useOktaAuth } from '@okta/okta-react';
import { Panel } from 'primereact/panel';
import { Ripple } from 'primereact/ripple';
import { useSessionStorage } from '../../service/useSessionStorage';
import * as jose from 'jose'

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

	const [themeState, setThemeState] = useSessionStorage( "themeSettings", initialThemeState);

	const [localUserInfo, setLocalUserInfo] = useState({});
	const [oktaToken] = useState(JSON.parse(localStorage.getItem('okta-token-storage')));

	const { authState, oktaAuth } = useOktaAuth();

	const loggedInPersonService = new LoggedInPersonService();

	const globalResetHandler = () =>{
		window.sessionStorage.setItem('globalStateObject', JSON.stringify({}));
		window.location.reload();
	};

	const themeResetHandler = () => {
		console.log(themeState);
		setThemeState(initialThemeState);
		window.location.reload();
	};

	const regenApiToken = () => {
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

	const valueTemplate = (props) => {
		//console.log(props.template());
		return props.template(props);
		//return props.template;
	};

	const textTemplate = (props) => {
		return (
			<p style={{ wordBreak: "break-all" }}>{ props.value }</p>
		);
	};


	const headerTemplate = (options, props) => {
        const toggleIcon = options.collapsed ? 'pi pi-chevron-down' : 'pi pi-chevron-up';
        const className = `${options.className} justify-content-start`;
        const titleClassName = `${options.titleClassName} pl-1`;

        return (
            <div className={className}>
                <button className={options.togglerClassName} onClick={options.onTogglerClick}>
                    <span className={toggleIcon}></span>
                    <Ripple />
                </button>
                <span className={titleClassName}>
                    Expand
                </span>
            </div>
        )
    };

	const jsonTemplate = (props) => {
		return (
			<p style={{ wordBreak: "break-all" }}>
				<Panel headerTemplate={headerTemplate} toggleable collapsed>
					<pre>{ props.value }</pre>
				</Panel>
			</p>
		);
	};

	const userInfos = [
		{ name: "Name", value: localUserInfo.firstName + " " + localUserInfo.lastName, template: textTemplate  },
		{ name: "Okta Email", value: localUserInfo.oktaEmail, template: textTemplate  },
		{ name: "Okta Access Token", value: oktaToken.accessToken.accessToken, template: textTemplate  },
		{ name: "Okta Id Token", value: oktaToken.idToken.idToken, template: textTemplate  },
		{ name: "Curation API Token", value: localUserInfo.apiToken, template: textTemplate },
		{ name: "Okta Access Token Content", value: JSON.stringify(jose.decodeJwt(oktaToken.accessToken.accessToken), null, 2), template: jsonTemplate  },
		{ name: "Okta Id Token Content", value: JSON.stringify(jose.decodeJwt(oktaToken.idToken.idToken), null, 2), template: jsonTemplate  },
		{ name: "User Settings", value: JSON.stringify(JSON.parse(sessionStorage.getItem("userSettings")), null, 2), template: jsonTemplate },
	];

	return (
		<div className="grid">
			<div className="col-12">
				<Card title="User Profile" subTitle="">
					<DataTable value={userInfos} style={{ width: "100%"}}>
						<Column field="name" header="Name" style={{ minWidth: '16rem' }} />
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
							acceptHandler={regenApiToken}
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
