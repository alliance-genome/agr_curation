DELETE FROM vocabularyterm_synonyms WHERE vocabularyterm_id IN (SELECT id FROM vocabularyterm WHERE name = 'anatomical structure');
DELETE FROM vocabularyterm_synonyms WHERE vocabularyterm_id IN (SELECT id FROM vocabularyterm WHERE name = 'chemical stimulus');
DELETE FROM vocabularyterm_synonyms WHERE vocabularyterm_id IN (SELECT id FROM vocabularyterm WHERE name = 'strain study');

INSERT INTO vocabularyterm_synonyms (vocabularyterm_id, synonyms) SELECT id, 'tissue type study' FROM vocabularyterm WHERE name = 'anatomical structure';
INSERT INTO vocabularyterm_synonyms (vocabularyterm_id, synonyms) SELECT id, 'tissue specific' FROM vocabularyterm WHERE name = 'anatomical structure';

INSERT INTO vocabularyterm_synonyms (vocabularyterm_id, synonyms) SELECT id, 'chemical stimulus study' FROM vocabularyterm WHERE name = 'chemical stimulus';
INSERT INTO vocabularyterm_synonyms (vocabularyterm_id, synonyms) SELECT id, 'response to chemical' FROM vocabularyterm WHERE name = 'chemical stimulus';

INSERT INTO vocabularyterm_synonyms (vocabularyterm_id, synonyms) SELECT id, 'natural variant' FROM vocabularyterm WHERE name = 'strain study';
INSERT INTO vocabularyterm_synonyms (vocabularyterm_id, synonyms) SELECT id, 'mouse strain' FROM vocabularyterm WHERE name = 'strain study';

DELETE FROM vocabularyterm where name = 'functional_genomics_proteomics' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'data_set_category_tags');

INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'functional genomics and proteomics', id FROM vocabulary WHERE vocabularylabel = 'data_set_category_tags';
