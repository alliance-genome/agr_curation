/**
 * Job status helpers for anything driven by the API's JobStatus enum
 * (org.alliancegenome.curation_api.enums.JobStatus) — bulk loads and curation reports both use it.
 *
 * TERMINAL_JOB_STATUSES mirrors JobStatus.isNotRunning(). FORCED_STOPPED is the one that is easy to
 * miss: the API treats it as not running, so a job stopped from the UI is restartable, but a check
 * that enumerates only FINISHED/FAILED/STOPPED hides that job's run button and leaves no way to
 * start it again.
 *
 * A missing status means the job has never run, which is not running either — so isJobRunning
 * requires a status to be present before it reports true.
 */
export const TERMINAL_JOB_STATUSES = Object.freeze(['FINISHED', 'FAILED', 'STOPPED', 'FORCED_STOPPED']);

export const isJobRunning = (status) => !!status && !TERMINAL_JOB_STATUSES.includes(status);
