ALTER TABLE species ADD COLUMN IF NOT EXISTS genomeassembly_id bigint;

UPDATE species s
SET genomeassembly_id = ga.id
FROM genomeassembly ga
JOIN biologicalentity be ON be.id = ga.id
WHERE be.primaryexternalid = s.assembly_curie
  AND s.genomeassembly_id IS NULL;

ALTER TABLE species
    ADD CONSTRAINT species_genomeassembly_id_fk
    FOREIGN KEY (genomeassembly_id)
    REFERENCES genomeassembly (id);

CREATE INDEX species_genomeassembly_index
    ON species USING btree (genomeassembly_id);

ALTER TABLE species DROP COLUMN IF EXISTS assembly_curie;
