UPDATE biologicalentity be
SET taxon_id = ot.id
FROM ontologyterm ot
WHERE ot.curie = 'NCBITaxon:559292'
  AND be.primaryexternalid = 'R64-5-1';
