-- SCRUM-6714: one direct-submission manual load per DQM for HTP_EXPRESSION_DATASET_ANNOTATION, so
-- DQMs can submit HTP Expression Dataset Annotations as LinkML instead of only through the FMS
-- HTPDATASET load. Mirrors v0.54.0.9__add_full_ingest_manual_loads.sql's pattern, scoped to just
-- the six providers named in the ticket rather than every provider with a manual load.
--
-- Guarded on NOT EXISTS so it is a no-op wherever a row already exists.

INSERT INTO bulkloadgroup (id, name)
	SELECT nextval('bulkloadgroup_seq'), 'Direct (LinkML) DQM HTP Expression Dataset Annotation Loads'
	WHERE NOT EXISTS (SELECT 1 FROM bulkloadgroup WHERE name = 'Direct (LinkML) DQM HTP Expression Dataset Annotation Loads');

-- Named "... Direct Load" rather than "... Load" - the existing FMS HTPDATASET rows (see
-- v0.37.0.59__htpdatasample_tables.sql) already use the plain "<Provider> HTP Expression Dataset
-- Annotation Load" name, and this migration's NOT EXISTS guards match by name.
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'HTP_EXPRESSION_DATASET_ANNOTATION', p.provider || ' HTP Expression Dataset Annotation Direct Load', 'STOPPED', g.id
	FROM (VALUES ('FB'),('MGI'),('RGD'),('SGD'),('WB'),('ZFIN')) AS p(provider)
	CROSS JOIN bulkloadgroup g
	WHERE g.name = 'Direct (LinkML) DQM HTP Expression Dataset Annotation Loads'
	AND NOT EXISTS (SELECT 1 FROM bulkload existing WHERE existing.name = p.provider || ' HTP Expression Dataset Annotation Direct Load');

INSERT INTO bulkmanualload (id, dataprovider)
	SELECT bl.id, p.provider
	FROM (VALUES ('FB'),('MGI'),('RGD'),('SGD'),('WB'),('ZFIN')) AS p(provider)
	JOIN bulkload bl ON bl.name = p.provider || ' HTP Expression Dataset Annotation Direct Load'
	WHERE NOT EXISTS (SELECT 1 FROM bulkmanualload existing WHERE existing.id = bl.id);
