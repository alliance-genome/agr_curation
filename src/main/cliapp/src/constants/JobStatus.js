/**
 * Terminal bulk load statuses, mirroring JobStatus.isNotRunning() on the API side
 * (org.alliancegenome.curation_api.enums.JobStatus).
 *
 * FORCED_STOPPED is the one that is easy to miss: the API treats it as not running, so a load
 * stopped from the UI is restartable, but a check that enumerates only FINISHED/FAILED/STOPPED
 * hides that load's run button and leaves no way to start it again.
 */
export const TERMINAL_BULKLOAD_STATUSES = Object.freeze(['FINISHED', 'FAILED', 'STOPPED', 'FORCED_STOPPED']);

export const isBulkloadNotRunning = (bulkloadStatus) => TERMINAL_BULKLOAD_STATUSES.includes(bulkloadStatus);
