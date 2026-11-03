-- SCRUM-6685: remove duplicate sequence targeting reagent gene associations.
--
-- The STR load created a new association for every target gene on every run instead of reusing the
-- one it had made before, and its cleanup removed nothing (it was given the STR service, which looked
-- the association ids up as STRs). ZFIN's STRs had accumulated one copy per load run: on production
-- 309,918 associations, of which 32,077 distinct. Both are fixed in the load; this removes the copies.
--
-- Per STR, gene and relation the oldest association (lowest id) is kept. Evidence linked to a copy is
-- moved to the association that stays before the copy is deleted; nothing else references the table.
-- STRs need a reindex afterwards (/api/sqtr/reindex), their documents embed the associations.

CREATE TEMP TABLE sqtr_gene_association_copy ON COMMIT DROP AS
SELECT id, keep_id
FROM (
	SELECT id,
	       min(id) OVER (PARTITION BY sequencetargetingreagentassociationsubject_id,
	                                  sequencetargetingreagentgeneassociationobject_id,
	                                  relation_id) AS keep_id
	FROM sqtrgeneassociation
) a
WHERE id <> keep_id;

CREATE INDEX ON sqtr_gene_association_copy (id);

INSERT INTO sqtrgeneassociation_informationcontententity (association_id, evidence_id)
SELECT DISTINCT c.keep_id, e.evidence_id
FROM sqtr_gene_association_copy c
JOIN sqtrgeneassociation_informationcontententity e ON e.association_id = c.id
WHERE NOT EXISTS (
	SELECT 1 FROM sqtrgeneassociation_informationcontententity k
	WHERE k.association_id = c.keep_id AND k.evidence_id = e.evidence_id
);

DELETE FROM sqtrgeneassociation_informationcontententity e
USING sqtr_gene_association_copy c
WHERE e.association_id = c.id;

DELETE FROM sqtrgeneassociation a
USING sqtr_gene_association_copy c
WHERE a.id = c.id;

ANALYZE sqtrgeneassociation;
ANALYZE sqtrgeneassociation_informationcontententity;
