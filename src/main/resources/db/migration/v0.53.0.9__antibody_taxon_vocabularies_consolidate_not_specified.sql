-- SCRUM-6496: v0.53.0.7 introduced a space-variant 'not specified' term in both the
-- antibody host and antigen taxon vocabularies, in addition to 'other' (renamed to
-- 'not_specified' in v0.53.0.8) -- redundant, since only one placeholder for "species
-- not specified" is wanted per vocabulary. Any antibody already pointing at the space
-- variant is repointed to 'not_specified' before the now-redundant term is deleted.
UPDATE antibody a
SET hosttaxonterm_id = nsu.id
FROM vocabularyterm nss
JOIN vocabulary v ON v.id = nss.vocabulary_id AND v.vocabularylabel = 'antibody_host_taxon'
JOIN vocabularyterm nsu ON nsu.vocabulary_id = nss.vocabulary_id AND nsu.name = 'not_specified'
WHERE a.hosttaxonterm_id = nss.id AND nss.name = 'not specified';

UPDATE antibody a
SET antigentaxonterm_id = nsu.id
FROM vocabularyterm nss
JOIN vocabulary v ON v.id = nss.vocabulary_id AND v.vocabularylabel = 'antibody_antigen_taxon'
JOIN vocabularyterm nsu ON nsu.vocabulary_id = nss.vocabulary_id AND nsu.name = 'not_specified'
WHERE a.antigentaxonterm_id = nss.id AND nss.name = 'not specified';

DELETE FROM vocabularyterm
WHERE name = 'not specified' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'antibody_host_taxon');

DELETE FROM vocabularyterm
WHERE name = 'not specified' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon');
