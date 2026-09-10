-- SCRUM-6496: Accommodate a new host taxon vocabulary for Antibody, and a small,
-- disjoint-from-NCBITaxon vocabulary for antigen taxon "reason absent" values.
--
-- Antibody.taxon (NCBITaxonTerm) is replaced with hostTaxonTerm (VocabularyTerm) so
-- free-text/not-specified host values can be recorded alongside real NCBITaxon IDs.
-- Existing data is backfilled by matching the old NCBITaxon curie to the new vocab
-- term whose name equals that curie.
--
-- Antibody.antigenTaxon is NOT replaced: antigen source species is an open set
-- (potentially any organism used to raise the antigen), unlike the small closed set
-- of common antibody hosts, so collapsing it into a single closed CV would either
-- require perpetual CV additions on every submission with a new organism or cause
-- recurring load failures. antigenTaxon stays NCBITaxonTerm-backed as before; a new,
-- separate antigenTaxonTerm (VocabularyTerm) is added alongside it purely to record
-- *why* a species is absent (not specified, etc.) -- this CV must never contain an
-- NCBITaxon curie, or a taxonomy-aware query against antigenTaxon alone (e.g. "any
-- antibody against a Drosophila antigen") could silently miss records recorded via
-- the CV instead. No backfill is needed for it: the old antigentaxon_id column is an
-- ontologyterm foreign key, so it could never have held a non-taxonomic value.

INSERT INTO vocabulary (id, name, vocabularylabel) VALUES (nextval('vocabulary_seq'), 'Antibody host taxon', 'antibody_host_taxon');
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:10090', 'mouse (Mus musculus)', id FROM vocabulary WHERE vocabularylabel = 'antibody_host_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:10116', 'rat (Rattus norvegicus)', id FROM vocabulary WHERE vocabularylabel = 'antibody_host_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:10141', 'guinea pig (Cavia porcellus)', id FROM vocabulary WHERE vocabularylabel = 'antibody_host_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:3122392', 'hamster, Armenian', id FROM vocabulary WHERE vocabularylabel = 'antibody_host_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:7924', 'bowfin (Amia calva)', id FROM vocabulary WHERE vocabularylabel = 'antibody_host_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:7955', 'zebrafish (Danio rerio)', id FROM vocabulary WHERE vocabularylabel = 'antibody_host_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:9031', 'chicken (Gallus gallus)', id FROM vocabulary WHERE vocabularylabel = 'antibody_host_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:9135', 'common canary (Serinus canaria)', id FROM vocabulary WHERE vocabularylabel = 'antibody_host_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:9606', 'human (Homo sapiens)', id FROM vocabulary WHERE vocabularylabel = 'antibody_host_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:9796', 'horse (Equus caballus)', id FROM vocabulary WHERE vocabularylabel = 'antibody_host_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:9823', 'pig (Sus scrofa)', id FROM vocabulary WHERE vocabularylabel = 'antibody_host_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:9843', 'vicugna (Vicugna vicugna)', id FROM vocabulary WHERE vocabularylabel = 'antibody_host_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:9844', 'llama (Lama glama)', id FROM vocabulary WHERE vocabularylabel = 'antibody_host_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:9913', 'cow (Bos taurus)', id FROM vocabulary WHERE vocabularylabel = 'antibody_host_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:9925', 'goat (Capra hircus)', id FROM vocabulary WHERE vocabularylabel = 'antibody_host_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:9940', 'sheep (Ovis aries)', id FROM vocabulary WHERE vocabularylabel = 'antibody_host_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:9986', 'rabbit, European (Oryctolagus cuniculus)', id FROM vocabulary WHERE vocabularylabel = 'antibody_host_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'camel', 'camel (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_host_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'chicken', 'chicken (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_host_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'donkey', 'donkey (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_host_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'goat', 'goat (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_host_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'guinea pig', 'guinea pig (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_host_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'hamster', 'hamster (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_host_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'horse', 'horse (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_host_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'monkey', 'monkey (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_host_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'mouse', 'mouse (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_host_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'not specified', 'not specified', id FROM vocabulary WHERE vocabularylabel = 'antibody_host_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'other', 'other', id FROM vocabulary WHERE vocabularylabel = 'antibody_host_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'rabbit', 'rabbit (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_host_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'rat', 'rat (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_host_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'sheep', 'sheep (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_host_taxon';

UPDATE vocabularyterm SET obsolete = true
WHERE name = 'other' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'antibody_host_taxon');

-- Non-taxonomic only -- see note at top of file. No NCBITaxon:* entries here; real
-- antigen species go through Antibody.antigenTaxon (NCBITaxonTerm) instead.
INSERT INTO vocabulary (id, name, vocabularylabel) VALUES (nextval('vocabulary_seq'), 'Antibody antigen taxon (non-taxonomic)', 'antibody_antigen_taxon');
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'bacteria', 'bacteria (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'bacteriophage', 'bacteriophage (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'butterfly', 'butterfly (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'carp', 'carp (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'chicken', 'chicken (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'frog', 'frog (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'hamster', 'hamster (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'mouse', 'mouse (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'newt', 'newt (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'not specified', 'not specified', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'other', 'other', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'pigeon', 'pigeon (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'quail', 'quail (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'rabbit', 'rabbit (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'rat', 'rat (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'salamander', 'salamander (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'virus', 'virus (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';

UPDATE vocabularyterm SET obsolete = true
WHERE name = 'other' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon');

ALTER TABLE antibody ADD COLUMN hosttaxonterm_id bigint;
ALTER TABLE antibody ADD COLUMN antigentaxonterm_id bigint;

ALTER TABLE antibody ADD CONSTRAINT antibody_hosttaxonterm_id_fk FOREIGN KEY (hosttaxonterm_id) REFERENCES vocabularyterm (id);
ALTER TABLE antibody ADD CONSTRAINT antibody_antigentaxonterm_id_fk FOREIGN KEY (antigentaxonterm_id) REFERENCES vocabularyterm (id);

CREATE INDEX antibody_hosttaxonterm_index ON antibody USING btree (hosttaxonterm_id);
CREATE INDEX antibody_antigentaxonterm_index ON antibody USING btree (antigentaxonterm_id);

-- Backfill from the old NCBITaxonTerm-based host taxon column by matching curie -> new
-- vocab term name. Three curies in use (NCBITaxon:10114 Rattus, NCBITaxon:10140 Cavia,
-- NCBITaxon:9923 Capra aegagrus) have no exact match in the new non-redundant list; map
-- them to the nearest covered term (rat, guinea pig, goat respectively) per curator
-- decision. antigenTaxon is not touched -- see note at top of file.
UPDATE antibody a
SET hosttaxonterm_id = vt.id
FROM ontologyterm o
JOIN vocabularyterm vt ON vt.name = CASE o.curie
		WHEN 'NCBITaxon:10114' THEN 'NCBITaxon:10116'
		WHEN 'NCBITaxon:10140' THEN 'NCBITaxon:10141'
		WHEN 'NCBITaxon:9923' THEN 'NCBITaxon:9925'
		ELSE o.curie
	END
JOIN vocabulary v ON v.id = vt.vocabulary_id AND v.vocabularylabel = 'antibody_host_taxon'
WHERE a.taxon_id = o.id;

-- Fail loudly rather than silently losing data: a host taxon curie that didn't match a
-- vocabulary term above leaves hosttaxonterm_id NULL, and the DROP COLUMN below is
-- irreversible once run. Roll back the whole migration if that happened instead of
-- quietly dropping the original value.
DO $$
DECLARE unmapped bigint;
BEGIN
	SELECT count(*) INTO unmapped FROM antibody WHERE taxon_id IS NOT NULL AND hosttaxonterm_id IS NULL;
	IF unmapped > 0 THEN
		RAISE EXCEPTION 'v0.53.0.5: % antibody rows have a host taxon curie with no matching vocabulary term', unmapped;
	END IF;
END $$;

-- FK constraint name varies by environment (explicit "antibody_taxon_id_fk"-style name where
-- v0.52.0.1 ran as a Flyway migration; Hibernate auto-DDL-generated hash name on environments
-- where the antibody table was instead created by dev-mode auto-DDL) -- look it up dynamically.
DO $$
DECLARE
	con_name text;
BEGIN
	SELECT tc.constraint_name INTO con_name
	FROM information_schema.table_constraints tc
	JOIN information_schema.key_column_usage kcu
		ON tc.constraint_name = kcu.constraint_name AND tc.table_schema = kcu.table_schema
	WHERE tc.table_name = 'antibody' AND tc.constraint_type = 'FOREIGN KEY' AND kcu.column_name = 'taxon_id';
	IF con_name IS NOT NULL THEN
		EXECUTE 'ALTER TABLE antibody DROP CONSTRAINT ' || quote_ident(con_name);
	END IF;
END $$;

-- DROP COLUMN below also drops the dependent index; no need for an explicit DROP INDEX first.
ALTER TABLE antibody DROP COLUMN taxon_id;
