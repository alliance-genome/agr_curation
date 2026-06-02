-- SCRUM-5124: backfill dataProviderCrossReference for the 7 SGD strain AGMs.
-- Their original load skipped dataprovidercrossreference_id, so the
-- model_search_result indexer emits docs without modCrossRefCompleteUrl
-- and the UI shows yeast strains without a link out to SGD.
-- Creates one crossreference row per AGM pointing at the SGD "strain"
-- resourcedescriptorpage, then links it back via biologicalentity.
-- Idempotent: only acts on AGMs where dataprovidercrossreference_id IS NULL.

WITH targets AS (
	SELECT be.id AS be_id,
		be.primaryexternalid,
		nextval('crossreference_seq') AS new_xref_id,
		(SELECT rdp.id
			FROM resourcedescriptorpage rdp
			INNER JOIN resourcedescriptor rd ON rd.id = rdp.resourcedescriptor_id
			WHERE rd.prefix = 'SGD' AND rdp.name = 'strain') AS page_id
	FROM affectedgenomicmodel agm
	INNER JOIN biologicalentity be ON be.id = agm.id
	INNER JOIN ontologyterm ot ON ot.id = be.taxon_id
	INNER JOIN species sp ON sp.taxon_id = ot.id
	WHERE sp.fullname = 'Saccharomyces cerevisiae'
		AND be.dataprovidercrossreference_id IS NULL
),
inserted AS (
	INSERT INTO crossreference (
		id, referencedcurie, displayname, resourcedescriptorpage_id,
		internal, obsolete, datecreated, dateupdated, dbdatecreated, dbdateupdated)
	SELECT new_xref_id, primaryexternalid, primaryexternalid, page_id,
		false, false, now(), now(), now(), now()
	FROM targets
	RETURNING id, referencedcurie
)
UPDATE biologicalentity be
SET dataprovidercrossreference_id = inserted.id
FROM inserted
WHERE be.primaryexternalid = inserted.referencedcurie;