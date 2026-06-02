-- SCRUM-6053: drain orphaned crossreference rows + remove duplicate stub
-- References that block the entity-mapping switch to @OneToMany +
-- orphanRemoval=true.
--
-- Two leak mechanisms produced ~2.4M orphans by 2026-05:
--   1. Per-entity DTO ingest with @ManyToMany mappings lacking orphanRemoval
--      (Reference / OntologyTerm / ExternalDataBaseEntity).
--   2. BaseSQLDAO.removeByIds using JPQL bulk DELETE, which bypasses JPA
--      cascade and orphanRemoval (JPA spec section 4.10).
-- Both are fixed in this branch's Java changes; this migration cleans the
-- accumulated debris and prepares the data for the new mappings.
--
-- An orphan is a crossreference row not referenced by any of the 23 FK
-- columns that point at crossreference.id. We additionally require
-- datecreated IS NULL as a belt-and-suspenders guard: in practice every
-- curator-authored xref has datecreated populated, while the bulk-load
-- ingest path that produced the orphans does not set it.
CREATE TABLE IF NOT EXISTS crossreference_ids_to_delete_scrum6053 (id bigint PRIMARY KEY);
TRUNCATE crossreference_ids_to_delete_scrum6053;

INSERT INTO crossreference_ids_to_delete_scrum6053 (id)
SELECT x.id
FROM crossreference x
WHERE x.datecreated IS NULL
  AND NOT EXISTS (SELECT 1 FROM biologicalentity                        t WHERE t.dataprovidercrossreference_id           = x.id)
  AND NOT EXISTS (SELECT 1 FROM chromosome                              t WHERE t.dataprovidercrossreference_id           = x.id)
  AND NOT EXISTS (SELECT 1 FROM diseaseannotation                       t WHERE t.dataprovidercrossreference_id           = x.id)
  AND NOT EXISTS (SELECT 1 FROM diseaseannotation                       t WHERE t.secondarydataprovidercrossreference_id  = x.id)
  AND NOT EXISTS (SELECT 1 FROM externaldatabaseentity                  t WHERE t.preferredcrossreference_id              = x.id)
  AND NOT EXISTS (SELECT 1 FROM externaldatabaseentity_crossreference   t WHERE t.crossreferences_id                      = x.id)
  AND NOT EXISTS (SELECT 1 FROM gene                                    t WHERE t.gcrpcrossreference_id                   = x.id)
  AND NOT EXISTS (SELECT 1 FROM geneexpressionannotation                t WHERE t.dataprovidercrossreference_id           = x.id)
  AND NOT EXISTS (SELECT 1 FROM geneexpressionannotation_crossreference t WHERE t.crossreferences_id                      = x.id)
  AND NOT EXISTS (SELECT 1 FROM geneexpressionexperiment                t WHERE t.dataprovidercrossreference_id           = x.id)
  AND NOT EXISTS (SELECT 1 FROM geneexpressionexperiment_crossreference t WHERE t.crossreferences_id                      = x.id)
  AND NOT EXISTS (SELECT 1 FROM genegeneticinteraction_crossreference   t WHERE t.crossreferences_id                      = x.id)
  AND NOT EXISTS (SELECT 1 FROM genemolecularinteraction_crossreference t WHERE t.crossreferences_id                      = x.id)
  AND NOT EXISTS (SELECT 1 FROM genomeassembly_crossreference           t WHERE t.crossreferences_id                      = x.id)
  AND NOT EXISTS (SELECT 1 FROM genomicentity_crossreference            t WHERE t.crossreferences_id                      = x.id)
  AND NOT EXISTS (SELECT 1 FROM htpexpressiondatasetannotation          t WHERE t.dataprovidercrossreference_id           = x.id)
  AND NOT EXISTS (SELECT 1 FROM htpexpressiondatasetsampleannotation    t WHERE t.dataprovidercrossreference_id           = x.id)
  AND NOT EXISTS (SELECT 1 FROM ontologyterm_crossreference             t WHERE t.crossreferences_id                      = x.id)
  AND NOT EXISTS (SELECT 1 FROM phenotypeannotation                     t WHERE t.crossreference_id                       = x.id)
  AND NOT EXISTS (SELECT 1 FROM phenotypeannotation                     t WHERE t.dataprovidercrossreference_id           = x.id)
  AND NOT EXISTS (SELECT 1 FROM reagent                                 t WHERE t.dataprovidercrossreference_id           = x.id)
  AND NOT EXISTS (SELECT 1 FROM reference_crossreference                t WHERE t.crossreferences_id                      = x.id)
  AND NOT EXISTS (SELECT 1 FROM species                                 t WHERE t.dataprovidercrossreference_id           = x.id)
ON CONFLICT (id) DO NOTHING;

DO $$
DECLARE
    rows_deleted int;
    total_deleted bigint := 0;
BEGIN
    LOOP
        WITH batch AS (
            DELETE FROM crossreference_ids_to_delete_scrum6053
            WHERE id IN (SELECT id FROM crossreference_ids_to_delete_scrum6053 LIMIT 250000)
            RETURNING id
        )
        DELETE FROM crossreference c USING batch WHERE c.id = batch.id;
        GET DIAGNOSTICS rows_deleted = ROW_COUNT;
        EXIT WHEN rows_deleted = 0;
        total_deleted := total_deleted + rows_deleted;
    END LOOP;
    RAISE NOTICE 'SCRUM-6053: deleted % orphaned crossreference rows', total_deleted;
END $$;

DROP TABLE crossreference_ids_to_delete_scrum6053;

-- Duplicate stub References that share a crossreference row with their
-- fully-populated counterparts. Pinned by id so this is a one-shot fix, but each
-- DELETE is also gated by the structural condition that motivates it. On a
-- future restored DB where these ids refer to unrelated entities, the
-- structural guard makes this section a safe no-op.

DELETE FROM reference_crossreference
WHERE reference_id IN (150917, 255346)
  AND crossreferences_id IN (
    SELECT crossreferences_id FROM reference_crossreference
    GROUP BY crossreferences_id HAVING count(*) > 1
  );

DELETE FROM reference
WHERE id IN (150917, 255346)
  AND id NOT IN (SELECT reference_id FROM reference_crossreference);

DELETE FROM informationcontententity
WHERE id IN (150917, 255346)
  AND id NOT IN (SELECT id FROM reference);
