-- SCRUM-6253: (1) store resource descriptor idPattern with a single backslash, and
-- (2) turn off the scheduled RD load so its nightly run can't re-double them.

-- (1) Collapse doubled backslashes to one. The ingest validator Java-escaped gid_pattern
--     (StringEscapeUtils.escapeJava), doubling every backslash: intended ^MGI:\d+$ was stored as
--     ^MGI:\\d+$, which is not a valid regex for the IDs. standard_conforming_strings=on, so in these
--     literals '\\' is two backslash characters and '\' is one; REPLACE is a plain substring op and all
--     stored backslashes are doubled/paired, so this collapse is safe and exact.
UPDATE resourcedescriptor
SET idpattern = replace(idpattern, '\\', '\')
WHERE idpattern LIKE '%\\%';

-- (2) Disable the nightly "AGR Resource Descriptors Load" (a scheduled URL load of the agr_schemas
--     resourceDescriptors.yaml). It upserts through the same escapeJava validator and would otherwise
--     re-double the patterns on its next run. The loader code is intentionally left unchanged.
UPDATE bulkscheduledload
SET scheduleactive = false
WHERE id IN (SELECT id FROM bulkload WHERE backendbulkloadtype = 'RESOURCE_DESCRIPTOR');
