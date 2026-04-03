import React, { useState, useEffect } from 'react';
import dayjs from 'dayjs';
import duration from 'dayjs/plugin/duration';

dayjs.extend(duration);

export const DurationCell = ({ rowData }) => {
	const [now, setNow] = useState(() => dayjs());
	const isRunning =
		!rowData.loadFinished && rowData.bulkloadStatus !== 'FAILED' && rowData.bulkloadStatus !== 'STOPPED';

	useEffect(() => {
		if (!isRunning) return;
		const interval = setInterval(() => setNow(dayjs()), 1000);
		return () => clearInterval(interval);
	}, [isRunning]);

	const started = dayjs(rowData.loadStarted);
	const finished = rowData.loadFinished ? dayjs(rowData.loadFinished) : now;
	const elapsed = dayjs.duration(finished.diff(started)).format('HH:mm:ss');

	return (
		<>
			Start: {started.format('YYYY-MM-DD HH:mm:ss')}
			<br />
			{rowData.loadFinished && (
				<>
					End: {finished.format('YYYY-MM-DD HH:mm:ss')}
					<br />
					Duration: {elapsed}
				</>
			)}
			{isRunning && <>Running Time: {elapsed}</>}
		</>
	);
};
