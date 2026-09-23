-- The "NCBITaxon:3122392" taxon term's definition lacked the Latin name used by every
-- other taxon term in this vocabulary (e.g. "mouse (Mus musculus)"). Add it.

UPDATE vocabularyterm SET definition = 'hamster, Armenian (Cricetulus migratorius)'
WHERE name = 'NCBITaxon:3122392'
  AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'antibody_taxon');
