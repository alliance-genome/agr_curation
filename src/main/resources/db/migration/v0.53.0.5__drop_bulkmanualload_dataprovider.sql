UPDATE bulkload SET species_id = s.id
FROM bulkmanualload bml
JOIN species s ON s.displayname = bml.dataprovider
WHERE bulkload.id = bml.id AND bulkload.species_id IS NULL;

ALTER TABLE bulkmanualload DROP COLUMN dataprovider;
