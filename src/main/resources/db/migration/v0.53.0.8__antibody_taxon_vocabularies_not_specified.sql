-- SCRUM-6496: Replace the 'other' term with 'not_specified' in the antibody host and
-- antigen taxon vocabularies (v0.53.0.7), matching the not_specified convention already
-- used by antibody_clonality and antibody_heavy_chain_isotype. Renaming in place (rather
-- than delete+insert) preserves the term's id in case any antibody row already references it.
UPDATE vocabularyterm
SET name = 'not_specified', definition = 'not_specified', obsolete = false
WHERE name = 'other' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'antibody_host_taxon');

UPDATE vocabularyterm
SET name = 'not_specified', definition = 'not_specified', obsolete = false
WHERE name = 'other' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'antibody_antigen_taxon');
