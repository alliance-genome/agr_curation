import React from 'react';
import { Tooltip } from 'primereact/tooltip';

export const StringTemplate = ({ string }) => {
	const targetClass = `a${global.crypto.randomUUID()}`;
	if (!string) return null;
	return (
		<>
			<div
				className={`overflow-hidden text-overflow-ellipsis ${targetClass}`}
				dangerouslySetInnerHTML={{ __html: string }}
			/>
			<Tooltip target={`.${targetClass}`} style={{ maxWidth: '450px' }} mouseTrack position="bottom">
				<div dangerouslySetInnerHTML={{ __html: string }} />
			</Tooltip>
		</>
	);
};
