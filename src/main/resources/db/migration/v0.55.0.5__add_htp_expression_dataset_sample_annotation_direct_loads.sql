-- SCRUM-6715: one direct-submission manual load per DQM for HTP_EXPRESSION_DATASET_SAMPLE_ANNOTATION,
-- so DQMs can submit HTP Expression Dataset Sample Annotations as LinkML instead of only through
-- the FMS HTPDATASAMPLE load. Mirrors v0.55.0.3__add_htp_expression_dataset_annotation_direct_loads.sql.
--
-- Guarded on NOT EXISTS so it is a no-op wherever a row already exists.

INSERT INTO bulkloadgroup (id, name)
	SELECT nextval('bulkloadgroup_seq'), 'Direct (LinkML) DQM HTP Expression Dataset Sample Annotation Loads'
	WHERE NOT EXISTS (SELECT 1 FROM bulkloadgroup WHERE name = 'Direct (LinkML) DQM HTP Expression Dataset Sample Annotation Loads');

-- Named "... Direct Load" rather than "... Load" - the existing FMS HTPDATASAMPLE rows (see
-- v0.37.0.59__htpdatasample_tables.sql) already use the plain "<Provider> HTP Expression Dataset
-- Sample Annotation Load" name, and this migration's NOT EXISTS guards match by name.
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'HTP_EXPRESSION_DATASET_SAMPLE_ANNOTATION', p.provider || ' HTP Expression Dataset Sample Annotation Direct Load', 'STOPPED', g.id
	FROM (VALUES ('FB'),('MGI'),('RGD'),('SGD'),('WB'),('ZFIN')) AS p(provider)
	CROSS JOIN bulkloadgroup g
	WHERE g.name = 'Direct (LinkML) DQM HTP Expression Dataset Sample Annotation Loads'
	AND NOT EXISTS (SELECT 1 FROM bulkload existing WHERE existing.name = p.provider || ' HTP Expression Dataset Sample Annotation Direct Load');

INSERT INTO bulkmanualload (id, dataprovider)
	SELECT bl.id, p.provider
	FROM (VALUES ('FB'),('MGI'),('RGD'),('SGD'),('WB'),('ZFIN')) AS p(provider)
	JOIN bulkload bl ON bl.name = p.provider || ' HTP Expression Dataset Sample Annotation Direct Load'
	WHERE NOT EXISTS (SELECT 1 FROM bulkmanualload existing WHERE existing.id = bl.id);
