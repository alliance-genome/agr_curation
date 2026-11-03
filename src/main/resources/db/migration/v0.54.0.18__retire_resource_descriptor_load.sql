-- SCRUM-6558: Retire the resource descriptor load.
--
-- Resource descriptors are no longer loaded from the agr_schemas resourceDescriptors.yaml; they are
-- curated in the UI. v0.51.2.2 turned the "AGR Resource Descriptors Load" schedule off, but the load
-- could still be run by hand, and a run would replace every descriptor's pages with the file's,
-- re-double the backslashes in idPattern and delete every descriptor missing from the file. The
-- loader code is removed along with the RESOURCE_DESCRIPTOR load type, so the load itself goes too:
-- its histories, their exceptions and files, and the load's own rows. The "Resource Descriptor Load"
-- group stays, as it also holds the GA Load.

CREATE TEMPORARY TABLE rd_loads AS
SELECT id FROM bulkload WHERE backendbulkloadtype = 'RESOURCE_DESCRIPTOR';

-- The load's files are its own: no other load's history references them.
CREATE TEMPORARY TABLE rd_files AS
SELECT DISTINCT bulkloadfile_id AS id FROM bulkloadfilehistory
WHERE bulkload_id IN (SELECT id FROM rd_loads) AND bulkloadfile_id IS NOT NULL;

DELETE FROM bulkloadfileexception
WHERE bulkloadfilehistory_id IN (SELECT id FROM bulkloadfilehistory WHERE bulkload_id IN (SELECT id FROM rd_loads));

DELETE FROM bulkloadfilehistory
WHERE bulkload_id IN (SELECT id FROM rd_loads);

DELETE FROM bulkloadfile
WHERE id IN (SELECT id FROM rd_files)
	AND id NOT IN (SELECT bulkloadfile_id FROM bulkloadfilehistory WHERE bulkloadfile_id IS NOT NULL);

DELETE FROM bulkload_dependencies
WHERE dependencies_id IN (SELECT id FROM rd_loads) OR depends_id IN (SELECT id FROM rd_loads);

DELETE FROM bulkurlload WHERE id IN (SELECT id FROM rd_loads);
DELETE FROM bulkscheduledload WHERE id IN (SELECT id FROM rd_loads);
DELETE FROM bulkload WHERE id IN (SELECT id FROM rd_loads);

DROP TABLE rd_files;
DROP TABLE rd_loads;
