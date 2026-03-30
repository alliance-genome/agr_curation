import React, { useState, useEffect } from 'react';
import dayjs from 'dayjs';

// Format a millisecond duration as HH:mm:ss.
const formatDuration = (ms) => {
	const totalSeconds = Math.floor(Math.abs(ms) / 1000);
	const hours = String(Math.floor(totalSeconds / 3600)).padStart(2, '0');
	const minutes = String(Math.floor((totalSeconds % 3600) / 60)).padStart(2, '0');
	const seconds = String(totalSeconds % 60).padStart(2, '0');
	return `${hours}:${minutes}:${seconds}`;
};

// Separate component so it can use hooks for the live-updating timer.
// Replaces the react-moment <Moment interval={1000}> that auto-updated every second.
export const DurationCell = ({ rowData }) => {
	const [now, setNow] = useState(() => dayjs());
	const isRunning = !rowData.loadFinished && rowData.bulkloadStatus !== 'FAILED' && rowData.bulkloadStatus !== 'STOPPED';

	useEffect(() => {
		if (!isRunning) return;
		const interval = setInterval(() => setNow(dayjs()), 1000);
		return () => clearInterval(interval);
	}, [isRunning]);

	// The DB stores timestamps as "timestamp without time zone" — they arrive
	// without a Z suffix (e.g. "2026-03-27T13:48:23.608487"). These represent
	// local time, so we parse with dayjs() (local mode) rather than dayjs.utc().
	// Using UTC would add a timezone offset to the duration calculation.
	const started = dayjs(rowData.loadStarted);
	const finished = rowData.loadFinished ? dayjs(rowData.loadFinished) : now;

	return (
		<>
			Start: {started.format('YYYY-MM-DD HH:mm:ss')}
			<br />
			{rowData.loadFinished && (
				<>
					End: {finished.format('YYYY-MM-DD HH:mm:ss')}
					<br />
					Duration: {formatDuration(finished.diff(started))}
				</>
			)}
			{isRunning && <>Running Time: {formatDuration(finished.diff(started))}</>}
		</>
	);
};
