-- SCRUM-6205: change the default (official) genome assembly for ZFIN (Danio
-- rerio, NCBITaxon:7955) from GRCz11 to GRCz12tu.
--
-- The SCRUM-6080 GFF-load check compares the header '#!assembly <id>' against
-- the official assembly designated for the species in the Species table
-- (Species.genomeAssembly.primaryExternalId). To load the new GRCz12tu GFF the
-- official assembly must therefore already be GRCz12tu — but the assembly row
-- itself does not yet exist (it was previously auto-created during a load,
-- which the new check now blocks). So this migration both CREATES the GRCz12tu
-- GenomeAssembly and REPOINTS the ZFIN species at it.
--
-- GenomeAssembly is a direct subclass of BiologicalEntity (genomeassembly.id ->
-- biologicalentity.id), so a GenomeAssembly is one biologicalentity row + one
-- genomeassembly row. IDs come from biologicalentity_seq (pooled optimizer,
-- increment 50): nextval returns a fresh, un-allocated block start, so it will
-- not collide with Hibernate-assigned ids.
--
-- The assembly's primaryexternalid is set to 'GRCz12tu' so it matches the GFF
-- header '#!assembly GRCz12tu' exactly. Idempotent and env-agnostic (taxon id
-- is resolved by curie, not hard-coded).

-- 1. Create the GRCz12tu BiologicalEntity row if it doesn't already exist.
INSERT INTO biologicalentity
    (id, primaryexternalid, taxon_id, internal, obsolete,
     datecreated, dateupdated, dbdatecreated, dbdateupdated)
SELECT nextval('biologicalentity_seq'),
       'GRCz12tu',
       (SELECT id FROM ontologyterm WHERE curie = 'NCBITaxon:7955'),
       false, false,
       now(), now(), now(), now()
WHERE NOT EXISTS (
    SELECT 1 FROM biologicalentity WHERE primaryexternalid = 'GRCz12tu'
);

-- 2. Promote that BiologicalEntity to a GenomeAssembly if not already one.
INSERT INTO genomeassembly (id)
SELECT be.id
FROM biologicalentity be
WHERE be.primaryexternalid = 'GRCz12tu'
  AND NOT EXISTS (SELECT 1 FROM genomeassembly ga WHERE ga.id = be.id);

-- 3. Point the ZFIN species at GRCz12tu as its official assembly.
UPDATE species
SET genomeassembly_id = (
        SELECT ga.id
        FROM genomeassembly ga
        JOIN biologicalentity be ON be.id = ga.id
        WHERE be.primaryexternalid = 'GRCz12tu'
    )
WHERE taxon_id = (SELECT id FROM ontologyterm WHERE curie = 'NCBITaxon:7955');
