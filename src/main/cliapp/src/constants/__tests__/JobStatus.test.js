import { isBulkloadNotRunning, TERMINAL_BULKLOAD_STATUSES } from '../JobStatus';

// The API's JobStatus.isNotRunning() counts FORCED_STOPPED as terminal, so a load stopped from the
// UI is restartable. The data loads page used to enumerate only FINISHED/FAILED/STOPPED, which hid
// the run button for a FORCED_STOPPED load and left no way to start it again.
const RUNNING_STATUSES = [
	'SCHEDULED_PENDING',
	'SCHEDULED_STARTED',
	'SCHEDULED_RUNNING',
	'FORCED_PENDING',
	'FORCED_STARTED',
	'FORCED_RUNNING',
	'MANUAL_PENDING',
	'MANUAL_STARTED',
	'MANUAL_RUNNING',
];

describe('isBulkloadNotRunning', () => {
	it.each(['FINISHED', 'FAILED', 'STOPPED', 'FORCED_STOPPED'])('treats %s as not running', (status) => {
		expect(isBulkloadNotRunning(status)).toBe(true);
	});

	it.each(RUNNING_STATUSES)('treats %s as running', (status) => {
		expect(isBulkloadNotRunning(status)).toBe(false);
	});

	it('does not claim a missing status is terminal, so callers keep their own falsy check', () => {
		expect(isBulkloadNotRunning(undefined)).toBe(false);
		expect(isBulkloadNotRunning(null)).toBe(false);
	});

	it('covers every terminal status the API reports', () => {
		expect([...TERMINAL_BULKLOAD_STATUSES].sort()).toEqual(['FAILED', 'FINISHED', 'FORCED_STOPPED', 'STOPPED']);
	});
});
