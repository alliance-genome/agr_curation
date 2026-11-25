-- SCRUM-6714: now that direct LinkML DQM submission of HTP Expression Dataset Annotations is
-- validated (real WB files loading correctly via the new direct-submission path), turn off the
-- nightly scheduled FMS HTPDATASET load for all six providers (FB, MGI, RGD, SGD, WB, ZFIN) so it
-- can no longer run automatically and overwrite/duplicate records against the direct-submission
-- path. Mirrors v0.51.2.2__fix_rd_idpattern_and_disable_load.sql's approach for the resource
-- descriptor load: only the schedule is turned off here, same as that migration's own precedent -
-- the load type, loader code and rows are left in place for the separate FMS retirement/clean-up
-- ticket to remove.
UPDATE bulkscheduledload
SET scheduleactive = false
WHERE id IN (SELECT id FROM bulkload WHERE backendbulkloadtype = 'HTPDATASET');
