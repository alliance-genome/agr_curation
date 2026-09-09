-- SCRUM-6463 — somewhere to store an AGRKB curie for the interaction and HTP expression dataset
-- classes, which inherit one from neither CurieObject nor Annotation.
--
-- GeneGeneAssociation is a @MappedSuperclass over a flattened hierarchy, so its new curie field
-- lands as a column on each concrete interaction table rather than on a shared parent table.
--
-- Nullable, indexed, no unique constraint — the same treatment the other minted curie columns get
-- (diseaseannotation, phenotypeannotation, geneexpressionannotation). Nullable is required: all
-- existing rows predate minting and stay NULL until the backfill reaches them, and both the
-- mint-on-upsert and the backfill paths target NULL-curie rows.

ALTER TABLE genemolecularinteraction ADD COLUMN curie varchar(255);
ALTER TABLE genegeneticinteraction ADD COLUMN curie varchar(255);
ALTER TABLE htpexpressiondatasetannotation ADD COLUMN curie varchar(255);

CREATE INDEX genemolecularinteraction_curie_index ON genemolecularinteraction USING btree (curie);
CREATE INDEX genegeneticinteraction_curie_index ON genegeneticinteraction USING btree (curie);
CREATE INDEX htpdatasetannotation_curie_index ON htpexpressiondatasetannotation USING btree (curie);
