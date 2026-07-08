-- SCRUM-6075: The GO consortium moved the MOD GAF files to direct download URLs under
-- https://current.geneontology.org/annotations/gaf/ (MOD-centric "-mod" files, live
-- 2026-06-26; old URLs retire after Sept 2026). Convert the MOD GAF loads from FMS
-- loads to URL loads.
--
-- Left as FMS loads (intentionally not converted here):
--   HUMAN - still sourced from RGD via DQM upload to the FMS.
--   XB    - Xenbase's bundled file splits into XENLA-mod + XENTR-mod; handled separately.
--
-- BulkLoad uses JOINED inheritance with no discriminator column, so the load type is
-- determined by which subclass table holds the row. Converting an FMS load to a URL
-- load = add a bulkurlload row (same id) and remove the bulkfmsload row. The shared
-- bulkscheduledload row and backendbulkloadtype='GAF' are unchanged, so the loads keep
-- their schedule and continue to route to GeneOntologyAnnotationExecutor.

INSERT INTO bulkurlload (id, bulkloadurl)
SELECT id, 'https://current.geneontology.org/annotations/gaf/DANRE-mod.gaf.gz' FROM bulkload WHERE name = 'ZFIN GAF Load';
INSERT INTO bulkurlload (id, bulkloadurl)
SELECT id, 'https://current.geneontology.org/annotations/gaf/YEAST-mod.gaf.gz' FROM bulkload WHERE name = 'SGD GAF Load';
INSERT INTO bulkurlload (id, bulkloadurl)
SELECT id, 'https://current.geneontology.org/annotations/gaf/CAEEL-mod.gaf.gz' FROM bulkload WHERE name = 'WB GAF Load';
INSERT INTO bulkurlload (id, bulkloadurl)
SELECT id, 'https://current.geneontology.org/annotations/gaf/MOUSE-mod.gaf.gz' FROM bulkload WHERE name = 'MGI GAF Load';
INSERT INTO bulkurlload (id, bulkloadurl)
SELECT id, 'https://current.geneontology.org/annotations/gaf/DROME-mod.gaf.gz' FROM bulkload WHERE name = 'FB GAF Load';
INSERT INTO bulkurlload (id, bulkloadurl)
SELECT id, 'https://current.geneontology.org/annotations/gaf/RAT-mod.gaf.gz' FROM bulkload WHERE name = 'RGD GAF Load';

DELETE FROM bulkfmsload
WHERE id IN (
	SELECT id FROM bulkload
	WHERE name IN ('ZFIN GAF Load', 'SGD GAF Load', 'WB GAF Load', 'MGI GAF Load', 'FB GAF Load', 'RGD GAF Load')
);

-- The group now holds a mix of URL loads (the 6 MOD loads) and FMS loads (HUMAN, XB),
-- so drop the "File Management System (FMS)" label.
UPDATE bulkloadgroup SET name = 'GAF Loads'
WHERE name = 'File Management System (FMS) GAF Loads';
