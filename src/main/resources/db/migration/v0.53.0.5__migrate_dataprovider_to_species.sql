UPDATE bulkload SET species_id = s.id
FROM bulkmanualload bml
JOIN species s ON s.displayname = bml.dataprovider
WHERE bulkload.id = bml.id AND bulkload.species_id IS NULL;

UPDATE bulkload SET species_id = s.id
FROM species s
WHERE bulkload.species_id IS NULL
AND bulkload.name = s.displayname || ' GAF Load';

UPDATE organization SET hasinferredallelephenotypeannotations = true WHERE abbreviation = 'MGI';

ALTER TABLE bulkmanualload DROP COLUMN dataprovider;
