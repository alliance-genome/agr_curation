import React from 'react';
import dayjs from 'dayjs';
import { DialogCard } from './DialogCard';
import { DialogCardRow } from './DialogCardRow';
import { StatusTemplate } from '../StatusTemplate';

export const DialogTop = ({ report }) => {
	return (
		<DialogCardRow>
			<DialogCard topText="Name">{report.name}</DialogCard>
			<DialogCard topText="Status">
				<StatusTemplate rowData={report} />
			</DialogCard>
			<DialogCard topText="Date Created">{dayjs(report.dateCreated).format('MMM D, YYYY')}</DialogCard>
			<DialogCard topText="Date Updated">{dayjs(report.dateUpdated).format('MMM D, YYYY')}</DialogCard>
		</DialogCardRow>
	);
};
