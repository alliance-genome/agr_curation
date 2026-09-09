-- SCRUM-6496: Accommodate new antibody/antigen taxon vocabularies.
-- Replaces Antibody.taxon/antigenTaxon (NCBITaxonTerm) with taxonTerm/antigenTaxonTerm
-- (VocabularyTerm) so free-text/not-specified values can be recorded alongside real
-- NCBITaxon IDs. Existing data is backfilled by matching the old NCBITaxon curie to
-- the new vocab term whose name equals that curie.

INSERT INTO vocabulary (id, name, vocabularylabel) VALUES (nextval('vocabulary_seq'), 'Antibody/host taxon', 'antibody_taxon');
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:10090', 'mouse (Mus musculus)', id FROM vocabulary WHERE vocabularylabel = 'antibody_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:10116', 'rat (Rattus norvegicus)', id FROM vocabulary WHERE vocabularylabel = 'antibody_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:10141', 'guinea pig (Cavia porcellus)', id FROM vocabulary WHERE vocabularylabel = 'antibody_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:3122392', 'hamster, Armenian', id FROM vocabulary WHERE vocabularylabel = 'antibody_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:7924', 'bowfin (Amia calva)', id FROM vocabulary WHERE vocabularylabel = 'antibody_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:7955', 'zebrafish (Danio rerio)', id FROM vocabulary WHERE vocabularylabel = 'antibody_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:9031', 'chicken (Gallus gallus)', id FROM vocabulary WHERE vocabularylabel = 'antibody_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:9135', 'common canary (Serinus canaria)', id FROM vocabulary WHERE vocabularylabel = 'antibody_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:9606', 'human (Homo sapiens)', id FROM vocabulary WHERE vocabularylabel = 'antibody_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:9796', 'horse (Equus caballus)', id FROM vocabulary WHERE vocabularylabel = 'antibody_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:9823', 'pig (Sus scrofa)', id FROM vocabulary WHERE vocabularylabel = 'antibody_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:9843', 'vicugna (Vicugna vicugna)', id FROM vocabulary WHERE vocabularylabel = 'antibody_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:9844', 'llama (Lama glama)', id FROM vocabulary WHERE vocabularylabel = 'antibody_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:9913', 'cow (Bos taurus)', id FROM vocabulary WHERE vocabularylabel = 'antibody_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:9925', 'goat (Capra hircus)', id FROM vocabulary WHERE vocabularylabel = 'antibody_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:9940', 'sheep (Ovis aries)', id FROM vocabulary WHERE vocabularylabel = 'antibody_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:9986', 'rabbit, European (Oryctolagus cuniculus)', id FROM vocabulary WHERE vocabularylabel = 'antibody_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'camel', 'camel (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'chicken', 'chicken (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'donkey', 'donkey (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'goat', 'goat (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'guinea pig', 'guinea pig (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'hamster', 'hamster (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'horse', 'horse (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'monkey', 'monkey (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'mouse', 'mouse (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'not specified', 'not specified', id FROM vocabulary WHERE vocabularylabel = 'antibody_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'other', 'other', id FROM vocabulary WHERE vocabularylabel = 'antibody_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'rabbit', 'rabbit (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'rat', 'rat (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'sheep', 'sheep (taxon not specified)', id FROM vocabulary WHERE vocabularylabel = 'antibody_taxon';

UPDATE vocabularyterm SET obsolete = true
WHERE name = 'other' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'antibody_taxon');

INSERT INTO vocabulary (id, name, vocabularylabel) VALUES (nextval('vocabulary_seq'), 'Antibody antigen taxon', 'antibody_antigen_taxon');
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:10029', 'hamster, Chinese (Cricetulus griseus)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:10036', 'hamster, golden (Mesocricetus auratus)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:10090', 'mouse (Mus musculus)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:10116', 'rat (Rattus norvegicus)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:10141', 'guinea pig (Cavia porcellus)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:105401', 'Zoanthus (Zoanthus)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:1870830', 'electric ray, ocellated (Diplobatis ommata)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:3055', 'green algae (Chlamydomonas reinhardtii)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:31033', 'fugu (Takifugu rubripes)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:34765', 'larvacean (Oikopleura dioica)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:36177', 'Atlantic sturgeon (Acipenser oxyrinchus)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:4564', 'wheat (Triticum)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:4932', 'yeast (Saccharomyces cerevisiae)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:6100', 'jellyfish (Aequorea victoria)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:6239', 'nematode (Caenorhabditis elegans)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:69293', 'three-spined stickleback (Gasterosteus aculeatus)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:7227', 'fruit fly (Drosophila melanogaster)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:7668', 'purple sea urchin (Strongylocentrotus purpuratus)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:7719', 'sea squirt (Ciona intestinalis)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:7740', 'amphioxus (Branchiostoma lanceolatum)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:7757', 'sea lamprey (Petromyzon marinus)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:7769', 'Atlantic hagfish (Myxine glutinosa)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:7787', 'Pacific electric ray (Torpedo californica)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:7797', 'spiny dogfish (Squalus acanthias)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:7924', 'bowfin (Amia calva)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:7955', 'zebrafish (Danio rerio)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:7957', 'goldfish (Carassius auratus)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:7962', 'carp (Cyprinus carpio)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:8022', 'rainbow trout (Oncorhynchus mykiss)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:8030', 'salmon (Salmo salar)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:8078', 'mummichog killifish (Fundulus heteroclitus)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:8090', 'medaka (Oryzias latipes)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:8353', 'Xenopus (Xenopus)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:8355', 'African clawed frog (Xenopus laevis)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:8364', 'Western clawed frog (Xenopus tropicalis)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:8400', 'bullfrog (Rana catesbeiana)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:86599', 'coral anemone (Discosoma)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:9031', 'chicken (Gallus gallus)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:9091', 'common quail (Coturnix coturnix)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:9135', 'common canary (Serinus canaria)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:9606', 'human (Homo sapiens)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:9615', 'dog, domestic (Canis lupus familiaris)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:9685', 'cat, domestic (Felis catus)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:9796', 'horse (Equus caballus)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:9823', 'pig (Sus scrofa)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:9825', 'pig, domestic (Sus scrofa domestica L.)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:9838', 'Arabian camel (Camelus dromedarius)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:9913', 'cow (Bos taurus)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:9940', 'sheep (Ovis aries)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:9986', 'rabbit, European (Oryctolagus cuniculus)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
INSERT INTO vocabularyterm (id, name, definition, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'NCBITaxon:99883', 'spotted green pufferfish (Tetraodon nigroviridis)', id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon';
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

ALTER TABLE antibody ADD COLUMN taxonterm_id bigint;
ALTER TABLE antibody ADD COLUMN antigentaxonterm_id bigint;

ALTER TABLE antibody ADD CONSTRAINT antibody_taxonterm_id_fk FOREIGN KEY (taxonterm_id) REFERENCES vocabularyterm (id);
ALTER TABLE antibody ADD CONSTRAINT antibody_antigentaxonterm_id_fk FOREIGN KEY (antigentaxonterm_id) REFERENCES vocabularyterm (id);

CREATE INDEX antibody_taxonterm_index ON antibody USING btree (taxonterm_id);
CREATE INDEX antibody_antigentaxonterm_index ON antibody USING btree (antigentaxonterm_id);

-- Backfill from the old NCBITaxonTerm-based columns by matching curie -> new vocab term name.
-- Three curies in use (NCBITaxon:10114 Rattus, NCBITaxon:10140 Cavia, NCBITaxon:9923 Capra
-- aegagrus) have no exact match in the new non-redundant list; map them to the nearest
-- covered term (rat, guinea pig, goat respectively) per curator decision.
UPDATE antibody a
SET taxonterm_id = vt.id
FROM ontologyterm o
JOIN vocabularyterm vt ON vt.name = CASE o.curie
		WHEN 'NCBITaxon:10114' THEN 'NCBITaxon:10116'
		WHEN 'NCBITaxon:10140' THEN 'NCBITaxon:10141'
		WHEN 'NCBITaxon:9923' THEN 'NCBITaxon:9925'
		ELSE o.curie
	END
JOIN vocabulary v ON v.id = vt.vocabulary_id AND v.vocabularylabel = 'antibody_taxon'
WHERE a.taxon_id = o.id;

UPDATE antibody a
SET antigentaxonterm_id = vt.id
FROM ontologyterm o
JOIN vocabularyterm vt ON vt.name = o.curie
JOIN vocabulary v ON v.id = vt.vocabulary_id AND v.vocabularylabel = 'antibody_antigen_taxon'
WHERE a.antigentaxon_id = o.id;

-- FK constraint names vary by environment (explicit "antibody_taxon_id_fk"-style names where
-- v0.52.0.1 ran as a Flyway migration; Hibernate auto-DDL-generated hash names on environments
-- where the antibody table was instead created by dev-mode auto-DDL) -- look them up dynamically.
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

	SELECT tc.constraint_name INTO con_name
	FROM information_schema.table_constraints tc
	JOIN information_schema.key_column_usage kcu
		ON tc.constraint_name = kcu.constraint_name AND tc.table_schema = kcu.table_schema
	WHERE tc.table_name = 'antibody' AND tc.constraint_type = 'FOREIGN KEY' AND kcu.column_name = 'antigentaxon_id';
	IF con_name IS NOT NULL THEN
		EXECUTE 'ALTER TABLE antibody DROP CONSTRAINT ' || quote_ident(con_name);
	END IF;
END $$;

DROP INDEX antibody_taxon_index;
DROP INDEX antibody_antigentaxon_index;
ALTER TABLE antibody DROP COLUMN taxon_id;
ALTER TABLE antibody DROP COLUMN antigentaxon_id;
