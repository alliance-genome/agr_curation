-- SCRUM-6496: Rename the antibody_host_taxon term 'guinea pig' to 'guinea_pig' -- it's
-- the vocabulary's only multi-word non-taxonomic term still using a space; not_specified
-- (v0.53.0.8) already moved to the underscore-separated form. Renaming in place preserves
-- the term's id in case any antibody row already references it.
UPDATE vocabularyterm
SET name = 'guinea_pig'
WHERE name = 'guinea pig' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'antibody_host_taxon');
