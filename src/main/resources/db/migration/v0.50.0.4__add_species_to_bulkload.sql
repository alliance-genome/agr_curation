ALTER TABLE organization ADD COLUMN hasinferredgenephenotypeannotations boolean NOT NULL DEFAULT false;
ALTER TABLE organization ADD COLUMN hasassertedgenephenotypeannotations boolean NOT NULL DEFAULT false;
ALTER TABLE organization ADD COLUMN hasinferredallelephenotypeannotations boolean NOT NULL DEFAULT false;
ALTER TABLE organization ADD COLUMN hasassertedallelephenotypeannotations boolean NOT NULL DEFAULT false;

UPDATE organization SET hasinferredgenephenotypeannotations = true WHERE abbreviation = 'MGI';
UPDATE organization SET hasinferredgenephenotypeannotations = true, hasinferredallelephenotypeannotations = true WHERE abbreviation = 'ZFIN';
UPDATE organization SET hasinferredgenephenotypeannotations = true WHERE abbreviation = 'FB';
UPDATE organization SET hasinferredgenephenotypeannotations = true WHERE abbreviation = 'SGD';
UPDATE organization SET hasinferredgenephenotypeannotations = true, hasassertedallelephenotypeannotations = true WHERE abbreviation = 'WB';
UPDATE organization SET hasinferredgenephenotypeannotations = true WHERE abbreviation = 'XB';

ALTER TABLE bulkload ADD COLUMN species_id bigint;

UPDATE bulkload SET species_id = s.id
FROM bulkmanualload bml
JOIN species s ON s.displayname = bml.dataprovider
WHERE bulkload.id = bml.id;

UPDATE bulkload SET species_id = s.id
FROM bulkfmsload f
JOIN species s ON s.displayname = f.fmsdatasubtype
WHERE bulkload.id = f.id AND bulkload.species_id IS NULL;

UPDATE bulkload SET species_id = s.id
FROM bulkurlload u, species s
WHERE bulkload.id = u.id AND bulkload.species_id IS NULL
AND bulkload.backendbulkloadtype = 'EXPRESSION_ATLAS'
AND bulkload.name LIKE s.displayname || ' %';

ALTER TABLE bulkload ADD CONSTRAINT bulkload_species_id_fk FOREIGN KEY (species_id) REFERENCES species (id);
CREATE INDEX bulkload_species_index ON bulkload USING btree(species_id);
