-- SCRUM-6496: Replace the 'other' term with 'not_specified' in the antibody host and
-- antigen taxon vocabularies (v0.53.0.7), matching the not_specified convention already
-- used by antibody_clonality and antibody_heavy_chain_isotype. Renaming in place (rather
-- than delete+insert) preserves the term's id in case any antibody row already references it.
--
-- v0.53.0.7 also separately introduced a 'not specified' (space) term in both vocabularies,
-- which duplicates the intent of 'not_specified' -- only one placeholder for "species not
-- specified" is wanted per vocabulary. Any antibody already pointing at the space variant is
-- repointed to 'not_specified' before the now-redundant term is deleted.
UPDATE vocabularyterm
SET name = 'not_specified', definition = 'not_specified', obsolete = false
WHERE name = 'other' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'antibody_host_taxon');

UPDATE vocabularyterm
SET name = 'not_specified', definition = 'not_specified', obsolete = false
WHERE name = 'other' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon');

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
