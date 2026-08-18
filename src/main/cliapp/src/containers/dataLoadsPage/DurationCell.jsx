import { useState, useEffect } from 'react';
import dayjs from 'dayjs';
import utc from 'dayjs/plugin/utc';
import duration from 'dayjs/plugin/duration';
import { isBulkloadNotRunning } from '../../constants/JobStatus';

dayjs.extend(utc);
dayjs.extend(duration);

export const DurationCell = ({ rowData }) => {
	const [now, setNow] = useState(() => dayjs.utc());
	const isRunning = !rowData.loadFinished && !isBulkloadNotRunning(rowData.bulkloadStatus);

	useEffect(() => {
		if (!isRunning) return;
		const interval = setInterval(() => setNow(dayjs.utc()), 1000);
		return () => clearInterval(interval);
	}, [isRunning]);

	// Parse as UTC (server stores UTC without Z suffix), then .local() for display
	const startedUtc = dayjs.utc(rowData.loadStarted);
	const finishedUtc = rowData.loadFinished ? dayjs.utc(rowData.loadFinished) : now;
	const elapsed = dayjs.duration(finishedUtc.diff(startedUtc)).format('HH:mm:ss');

	return (
		<>
			Start: {startedUtc.local().format('YYYY-MM-DD HH:mm:ss')}
			<br />
			{rowData.loadFinished && (
				<>
					End: {finishedUtc.local().format('YYYY-MM-DD HH:mm:ss')}
					<br />
					Duration: {elapsed}
				</>
			)}
			{isRunning && <>Running Time: {elapsed}</>}
		</>
	);
};
