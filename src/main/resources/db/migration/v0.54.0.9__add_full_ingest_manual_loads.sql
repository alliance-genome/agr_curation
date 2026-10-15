-- SCRUM-6535: one manual load per data provider for FULL_INGEST, so a submission is routed by what
-- the file contains rather than by a load type the submitter has to name.
--
-- /api/data/submit does not create a load, it looks one up: the form field name is split into a
-- BackendBulkLoadType and a BackendBulkDataProvider, and BulkLoadManualProcessor requires a
-- matching BulkManualLoad row. Adding the six new load types in this ticket therefore left them
-- unreachable through that route, because no such row exists for any of them.
--
-- Rather than configure six more per-type loads, this configures FULL_INGEST, which BulkLoadJobExecutor
-- already dispatches by content: every executor runs, each reads only its own ingest set, and each
-- returns immediately when that set is absent. A file carrying only cassette_ingest_set therefore
-- runs only the cassette load. That mechanism has always been there; only the configuration to
-- reach it was missing, for every provider.
--
-- FULL_INGEST also fixes the ordering problem a per-type submission has: transgenic tools and
-- cassettes are dispatched before the four association loads, so an association can resolve a
-- subject the same file has just created.
--
-- Providers are the BackendBulkDataProvider values that already have manual loads of some kind.
-- STOPPED matches how every other load is seeded; it does not prevent an API submission, which
-- sets MANUAL_STARTED on the row it finds.
--
-- Guarded on NOT EXISTS so it is a no-op wherever a row already exists.

INSERT INTO bulkloadgroup (id, name)
	SELECT nextval('bulkloadgroup_seq'), 'Direct (LinkML) DQM Full Ingest Loads'
	WHERE NOT EXISTS (SELECT 1 FROM bulkloadgroup WHERE name = 'Direct (LinkML) DQM Full Ingest Loads');

INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'FULL_INGEST', p.provider || ' Full Ingest Load', 'STOPPED', g.id
	FROM (VALUES ('RGD'),('MGI'),('SGD'),('HUMAN'),('ZFIN'),('FB'),('WB'),('XB'),('XBXL'),('XBXT')) AS p(provider)
	CROSS JOIN bulkloadgroup g
	WHERE g.name = 'Direct (LinkML) DQM Full Ingest Loads'
	AND NOT EXISTS (SELECT 1 FROM bulkload existing WHERE existing.name = p.provider || ' Full Ingest Load');

INSERT INTO bulkmanualload (id, dataprovider)
	SELECT bl.id, p.provider
	FROM (VALUES ('RGD'),('MGI'),('SGD'),('HUMAN'),('ZFIN'),('FB'),('WB'),('XB'),('XBXL'),('XBXT')) AS p(provider)
	JOIN bulkload bl ON bl.name = p.provider || ' Full Ingest Load'
	WHERE NOT EXISTS (SELECT 1 FROM bulkmanualload existing WHERE existing.id = bl.id);
