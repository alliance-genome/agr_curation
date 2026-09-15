UPDATE bulkload SET species_id = s.id
FROM bulkmanualload bml
JOIN species s ON s.displayname = bml.dataprovider
WHERE bulkload.id = bml.id AND bulkload.species_id IS NULL;

UPDATE bulkload SET species_id = s.id
FROM species s
WHERE bulkload.species_id IS NULL
AND bulkload.name = s.displayname || ' GAF Load';

ALTER TABLE bulkmanualload DROP COLUMN dataprovider;
