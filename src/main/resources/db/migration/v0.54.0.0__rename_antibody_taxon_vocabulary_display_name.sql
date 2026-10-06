-- Rename the "antibody_taxon" vocabulary's display name from "Antibody taxon" to
-- "Taxon term" -- it's a generic taxon vocabulary (unified from the old host/antigen
-- taxon vocabularies in v0.53.0.13) that may be reused outside the antibody context in
-- the future. The internal vocabularylabel stays "antibody_taxon" -- it's referenced by
-- VocabularyConstants.java and the antibody_host_taxon/antibody_antigen_taxon
-- VocabularyTermSets, none of which need to change for a display-name-only rename.

UPDATE vocabulary SET name = 'Taxon term' WHERE vocabularylabel = 'antibody_taxon';
