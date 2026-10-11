-- SCRUM-6535: an FMS load for FB's sequence targeting reagents (FMS data type SQTR, subtype FB).
--
-- v0.34.0.4 meant to create an STR load per provider, but it looked its group up as
-- ' Sequence Targeting Reagent Bulk Loads' (leading space) and registered the FMS type as
-- SEQUENCE_TARGETING_REAGENT, so it inserted nothing. ZFIN's load was later added by hand on alpha;
-- this mirrors it for FB: same group, FMS SQTR/FB, depending on FB Gene Load.
--
-- FB submitted its first SQTR file in 2026_03 (133,566 FBsf reagents). They are what FB's
-- cassette_str_association_ingest_set refers to, so cassette STR associations can only load once
-- this load has run. The schedule is the ZFIN one but inactive until the first load has been checked.
--
-- Guarded on NOT EXISTS throughout, so it is a no-op where the group, load or rows already exist,
-- e.g. if the load was created by hand on the data loads page first.

INSERT INTO bulkloadgroup (id, name)
	SELECT nextval('bulkloadgroup_seq'), 'File Management System (FMS) Sequence Targeting Reagent Loads'
	WHERE NOT EXISTS (SELECT 1 FROM bulkloadgroup WHERE name = 'File Management System (FMS) Sequence Targeting Reagent Loads');

INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'SEQUENCE_TARGETING_REAGENT', 'FB Sequence Targeting Reagent Load', 'STOPPED', g.id
	FROM bulkloadgroup g
	WHERE g.name = 'File Management System (FMS) Sequence Targeting Reagent Loads'
	AND NOT EXISTS (SELECT 1 FROM bulkload WHERE name = 'FB Sequence Targeting Reagent Load');

-- BulkFMSLoad extends BulkScheduledLoad extends BulkLoad (joined tables), so this order.
INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
	SELECT bl.id, '0 0 22 ? * SUN-THU', false
	FROM bulkload bl
	WHERE bl.name = 'FB Sequence Targeting Reagent Load'
	AND NOT EXISTS (SELECT 1 FROM bulkscheduledload existing WHERE existing.id = bl.id);

INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT bl.id, 'SQTR', 'FB'
	FROM bulkload bl
	WHERE bl.name = 'FB Sequence Targeting Reagent Load'
	AND NOT EXISTS (SELECT 1 FROM bulkfmsload existing WHERE existing.id = bl.id);

-- STRs reference their target genes, so the load runs after FB's gene load.
INSERT INTO bulkload_dependencies (dependencies_id, depends_id)
	SELECT c.id, p.id
	FROM bulkload c, bulkload p
	WHERE c.name = 'FB Sequence Targeting Reagent Load' AND p.name = 'FB Gene Load'
	AND NOT EXISTS (SELECT 1 FROM bulkload_dependencies d WHERE d.dependencies_id = c.id AND d.depends_id = p.id);
