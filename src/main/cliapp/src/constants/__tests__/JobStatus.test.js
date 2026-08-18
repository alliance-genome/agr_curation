import { isJobRunning, TERMINAL_JOB_STATUSES } from '../JobStatus';

// The API's JobStatus.isNotRunning() counts FORCED_STOPPED as terminal, so a job stopped from the UI
// is restartable. The data loads and reports pages used to enumerate only FINISHED/FAILED/STOPPED,
// which hid the run button for a FORCED_STOPPED job and left no way to start it again.
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

describe('isJobRunning', () => {
	it.each(RUNNING_STATUSES)('reports %s as running', (status) => {
		expect(isJobRunning(status)).toBe(true);
	});

	it.each([...TERMINAL_JOB_STATUSES])('reports %s as not running', (status) => {
		expect(isJobRunning(status)).toBe(false);
	});

	it('reports a job that has never run as not running', () => {
		expect(isJobRunning(undefined)).toBe(false);
		expect(isJobRunning(null)).toBe(false);
		expect(isJobRunning('')).toBe(false);
	});

	it('covers every terminal status the API reports', () => {
		expect([...TERMINAL_JOB_STATUSES].sort()).toEqual(['FAILED', 'FINISHED', 'FORCED_STOPPED', 'STOPPED']);
	});
});
