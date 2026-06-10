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

ALTER TABLE bulkmanualload ADD COLUMN species_id bigint;

UPDATE bulkmanualload SET species_id = (
    SELECT s.id FROM species s WHERE s.displayname = bulkmanualload.dataprovider
);

DELETE FROM bulkmanualload WHERE species_id IS NULL;

ALTER TABLE bulkmanualload ADD CONSTRAINT bulkmanualload_species_id_fk FOREIGN KEY (species_id) REFERENCES species (id);
CREATE INDEX bulkmanualload_species_index ON bulkmanualload USING btree(species_id);

ALTER TABLE bulkmanualload DROP COLUMN dataprovider;
