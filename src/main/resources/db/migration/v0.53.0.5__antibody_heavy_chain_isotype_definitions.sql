-- SCRUM-6235: `name` is the antibody heavy chain isotype vocabulary's internal/search
-- key (indexed lowercase via sortNormalizer for case-insensitive search, same as every
-- other VocabularyTerm), not what should be displayed to curators. Populate `definition`
-- with the case-correct display text so the UI can show that instead, matching the
-- existing name (machine key) vs. definition (display text) convention used elsewhere
-- (e.g. KO_consortium_allele -> "Knockout consortium allele").
UPDATE vocabularyterm SET definition = 'Immunoglobulin A' WHERE name = 'IgA' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype');
UPDATE vocabularyterm SET definition = 'Immunoglobulin A1' WHERE name = 'IgA1' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype');
UPDATE vocabularyterm SET definition = 'Immunoglobulin A2' WHERE name = 'IgA2' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype');
UPDATE vocabularyterm SET definition = 'Immunoglobulin D' WHERE name = 'IgD' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype');
UPDATE vocabularyterm SET definition = 'Immunoglobulin E' WHERE name = 'IgE' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype');
UPDATE vocabularyterm SET definition = 'Immunoglobulin G' WHERE name = 'IgG' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype');
UPDATE vocabularyterm SET definition = 'Immunoglobulin G1' WHERE name = 'IgG1' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype');
UPDATE vocabularyterm SET definition = 'Immunoglobulin G2' WHERE name = 'IgG2' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype');
UPDATE vocabularyterm SET definition = 'Immunoglobulin G2a' WHERE name = 'IgG2a' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype');
UPDATE vocabularyterm SET definition = 'Immunoglobulin G2b' WHERE name = 'IgG2b' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype');
UPDATE vocabularyterm SET definition = 'Immunoglobulin G2c' WHERE name = 'IgG2c' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype');
UPDATE vocabularyterm SET definition = 'Immunoglobulin G3' WHERE name = 'IgG3' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype');
UPDATE vocabularyterm SET definition = 'Immunoglobulin G4' WHERE name = 'IgG4' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype');
UPDATE vocabularyterm SET definition = 'Immunoglobulin M' WHERE name = 'IgM' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype');
UPDATE vocabularyterm SET definition = 'Immunoglobulin N' WHERE name = 'IgN' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype');
UPDATE vocabularyterm SET definition = 'Immunoglobulin R' WHERE name = 'IgR' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype');
UPDATE vocabularyterm SET definition = 'Immunoglobulin W' WHERE name = 'IgW' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype');
UPDATE vocabularyterm SET definition = 'Immunoglobulin X' WHERE name = 'IgX' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype');
UPDATE vocabularyterm SET definition = 'Immunoglobulin Y' WHERE name = 'IgY' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype');
UPDATE vocabularyterm SET definition = 'Fragment antigen-binding' WHERE name = 'Fab' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype');
UPDATE vocabularyterm SET definition = 'Variable domain of the heavy chain of a heavy-chain antibody' WHERE name = 'VHH' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype');
UPDATE vocabularyterm SET definition = 'Not specified' WHERE name = 'not_specified' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'antibody_heavy_chain_isotype');
