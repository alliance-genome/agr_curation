DELETE FROM vocabularyterm WHERE name = 'post embryonic' AND id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'stage_uberon_slim_terms');
DELETE FROM vocabularyterm WHERE name = 'pre-adult' AND id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'stage_uberon_slim_terms');
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'post embryonic, pre-adult', id FROM vocabulary WHERE vocabularylabel = 'stage_uberon_slim_terms';
