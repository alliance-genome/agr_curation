
INSERT INTO vocabulary (id, name, vocabularylabel) VALUES (nextval('vocabulary_seq'), 'Paralogy confidence','paralogy_confidence');
INSERT INTO vocabulary (id, name, vocabularylabel) VALUES (nextval('vocabulary_seq'), 'Paralogy prediction methods','paralogy_prediction_method');

INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'moderate', id FROM vocabulary WHERE vocabularylabel = 'paralogy_confidence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'high', id FROM vocabulary WHERE vocabularylabel = 'paralogy_confidence';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'low', id FROM vocabulary WHERE vocabularylabel = 'paralogy_confidence';

INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'Ensembl Compara', id FROM vocabulary WHERE vocabularylabel = 'paralogy_prediction_method';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'HGNC', id FROM vocabulary WHERE vocabularylabel = 'paralogy_prediction_method';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'InParanoid', id FROM vocabulary WHERE vocabularylabel = 'paralogy_prediction_method';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'OMA', id FROM vocabulary WHERE vocabularylabel = 'paralogy_prediction_method';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'OrthoFinder', id FROM vocabulary WHERE vocabularylabel = 'paralogy_prediction_method';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'OrthoInspector', id FROM vocabulary WHERE vocabularylabel = 'paralogy_prediction_method';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'PANTHER', id FROM vocabulary WHERE vocabularylabel = 'paralogy_prediction_method';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'PhylomeDB', id FROM vocabulary WHERE vocabularylabel = 'paralogy_prediction_method';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'SonicParanoid', id FROM vocabulary WHERE vocabularylabel = 'paralogy_prediction_method';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'SGD', id FROM vocabulary WHERE vocabularylabel = 'paralogy_prediction_method';
