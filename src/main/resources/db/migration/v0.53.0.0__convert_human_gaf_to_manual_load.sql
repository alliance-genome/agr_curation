-- SCRUM-6264: The HUMAN GAF file is produced by RGD and submitted to the Alliance FMS,
-- where the curation API picks it up as an FMS load. Move it to a direct DQM submission:
-- RGD POSTs the file to /api/data/submit with the form key "GAF_HUMAN" and it loads
-- straight into the persistent store, bypassing the FMS. This is the follow-up to
-- v0.50.0.6, which converted the other MOD GAF loads to URL loads and deliberately left
-- HUMAN on FMS because there is no MOD-sourced human GAF at the GO consortium.
--
-- BulkLoad uses JOINED inheritance with no discriminator column, so a load's type is
-- determined by which subclass table holds the row with that id. Converting an FMS load
-- to a manual load = add a bulkmanualload row (same id) and remove the bulkfmsload row.
-- BulkManualLoad extends BulkLoad directly rather than BulkScheduledLoad, so the
-- bulkscheduledload row is removed too: a manual load never fires on a cron, it runs only
-- when a DQM pushes a file. No schedule is lost in practice -- the GAF loads all have
-- scheduleactive = false.
--
-- Left unchanged on purpose:
--   name = 'HUMAN GAF Load'      GeneOntologyAnnotationExecutor derives the data provider
--                                from the first word of the load name, so renaming the
--                                load would break it.
--   backendbulkloadtype = 'GAF'  BulkLoadJobExecutor routes on this alone.
--   group_id, species_id         Still valid. The id is unchanged, so the
--                                bulkloadfilehistory and bulkload_dependencies rows that
--                                reference this load are preserved.
--
-- BulkLoadManualProcessor.processBulkManualLoadFromDQM looks the load up by
-- (backendBulkLoadType, dataProvider) and requires exactly one match. No other manual load
-- uses backendbulkloadtype = 'GAF', so ('GAF', 'HUMAN') is unique.

INSERT INTO bulkmanualload (id, dataprovider)
SELECT id, 'HUMAN' FROM bulkload WHERE name = 'HUMAN GAF Load';

DELETE FROM bulkfmsload
WHERE id IN (SELECT id FROM bulkload WHERE name = 'HUMAN GAF Load');

DELETE FROM bulkscheduledload
WHERE id IN (SELECT id FROM bulkload WHERE name = 'HUMAN GAF Load');
