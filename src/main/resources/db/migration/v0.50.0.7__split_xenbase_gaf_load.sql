-- SCRUM-6075: The GO consortium split the bundled Xenbase GAF (xenbase.gaf.gz) into two
-- single-species files: XENLA-mod (X. laevis) and XENTR-mod (X. tropicalis). Replace the
-- single "XB GAF Load" FMS load with two URL loads, one per species.
--
-- Both species carry the same data provider abbreviation "XB" but distinct taxa
-- (NCBITaxon:8355 vs 8364), so the loads use the XBXL / XBXT providers, which
-- GeneOntologyAnnotationService.getAllGafIdsPerProvider scopes by taxon -- keeping each
-- species load's cleanup from deleting the other's annotations.

-- Repurpose the existing XB load as the X. laevis (XBXL) URL load; preserves its history.
UPDATE bulkload SET name = 'XBXL GAF Load' WHERE name = 'XB GAF Load';
INSERT INTO bulkurlload (id, bulkloadurl)
SELECT id, 'https://current.geneontology.org/annotations/gaf/XENLA-mod.gaf.gz' FROM bulkload WHERE name = 'XBXL GAF Load';
DELETE FROM bulkfmsload WHERE id IN (SELECT id FROM bulkload WHERE name = 'XBXL GAF Load');

-- Add the X. tropicalis (XBXT) URL load to the same group, with the standard schedule.
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'GAF', 'XBXT GAF Load', 'STOPPED', id
FROM bulkloadgroup WHERE name = 'GAF Loads';
INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
SELECT id, '0 0 22 ? * SUN-THU', false FROM bulkload WHERE name = 'XBXT GAF Load';
INSERT INTO bulkurlload (id, bulkloadurl)
SELECT id, 'https://current.geneontology.org/annotations/gaf/XENTR-mod.gaf.gz' FROM bulkload WHERE name = 'XBXT GAF Load';
