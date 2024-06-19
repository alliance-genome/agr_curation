import React, { useRef, useState, useContext } from 'react';
import { Link } from 'react-router-dom';
import { Menu } from 'primereact/menu';
import classNames from 'classnames';
import { SiteContext } from './containers/layout/SiteContext';
import useWebSocket from 'react-use-websocket';
import { Message } from 'primereact/message';
import { ProgressBar } from 'primereact/progressbar';

export const AppTopbar = (props) => {
	const menu = useRef(null);
	const [processingEvent, setProcessingEvent] = useState(null);

	const { apiVersion } = useContext(SiteContext);

	var loc = window.location,
		new_uri;
	if (loc.protocol === 'https:') {
		new_uri = 'wss:';
	} else {
		new_uri = 'ws:';
	}
	if (process.env.NODE_ENV === 'production') {
		new_uri += '//' + loc.host;
	} else {
		new_uri += '//localhost:8080';
	}

	new_uri += loc.pathname + 'index_processing_events';
	useWebSocket(new_uri, {
		onOpen: () => {
			//console.log("WS Connection Open");
		},
		onMessage: (event) => {
			let localEvent = JSON.parse(event.data);
			setProcessingEvent(localEvent);
		},
	});

	const processingMessageTemplate = () => {
		if (processingEvent) {
			return (
				<div style={{ width: '20vw' }}>
					{processingEvent.message}
					<br />
					Started: {new Date(processingEvent.startTime).toLocaleString()}
					<br />
					Update: {new Date(processingEvent.lastTime).toLocaleString()}
					<br />
					{ProgressIndicator(processingEvent)}
				</div>
			);
		}
	};
	const processingMessageStyle = () => {
		if (!processingEvent || 'duration' in processingEvent) {
			return { display: 'none' };
		} else {
			//return( { display: "block" } );
		}
	};

	const ProgressIndicator = (pe) => {
		if (pe.currentCount && pe.totalSize) {
			return <ProgressBar value={parseInt((pe.currentCount / pe.totalSize) * 10000) / 100} />;
		} else if (pe.currentCount && pe.lastCount && pe.lastTime && pe.nowTime) {
			let rate = Math.ceil(((pe.currentCount - pe.lastCount) / (pe.nowTime - pe.lastTime)) * 1000);
			return (
				<ProgressBar
					value={100}
					displayValueTemplate={(value) => {
						return (
							<>
								{rate}r/s -- {pe.currentCount}
							</>
						);
					}}
				/>
			);
		} else {
			return <ProgressBar value={0} />;
		}
	};

	const menuItems = [
		{
			items: [
				{
					label: 'Profile',
					icon: 'pi pi-profile',
					command: (e) => {
						window.location.hash = '/profile';
					},
				},
				{
					label: 'Logout',
					icon: 'pi pi-sign-out',
					command: props.logout,
				},
			],
		},
	];

	return (
		<div className="layout-topbar">
			{props.authState?.isAuthenticated && (
				<>
					<Link to="/" className="layout-topbar-logo">
						AGR Curation: {apiVersion?.version}
					</Link>
					<button
						type="button"
						className="p-link layout-menu-button layout-topbar-button"
						onClick={props.onToggleMenuClick}
					>
						<i className="pi pi-bars" />
					</button>
				</>
			)}
			<button
				type="button"
				className="p-link layout-topbar-menu-button layout-topbar-button"
				onClick={props.onMobileTopbarMenuClick}
			>
				<i className="pi pi-ellipsis-v" />
			</button>
			<Message severity="info" style={processingMessageStyle()} content={processingMessageTemplate} />
			{props.authState?.isAuthenticated && (
				<ul
					className={classNames('layout-topbar-menu lg:flex origin-top', {
						'layout-topbar-menu-mobile-active': props.mobileTopbarMenuActive,
					})}
				>
					<li>
						<Menu model={menuItems} popup ref={menu} id="popup_menu" />
						<i
							className="pi pi-user"
							style={{ fontSize: '1.5em' }}
							onClick={(event) => menu.current.toggle(event)}
							aria-controls="popup_menu"
							aria-haspopup
						/>
					</li>
				</ul>
			)}
		</div>
	);
};
