
UPDATE vocabulary SET vocabularylabel = 'homology_confidence', name = 'Homology confidence' WHERE vocabularylabel = 'ortho_confidence';

UPDATE vocabulary SET vocabularylabel = 'homology_prediction_method', name = 'Homology prediction methods' WHERE vocabularylabel = 'ortho_prediction_method';

INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('vocabularyterm_seq'), 'SGD', id FROM vocabulary WHERE vocabularylabel = 'homology_prediction_method';

DELETE from vocabularyterm where vocabulary_id = (select id from vocabulary where vocabularylabel = 'paralogy_confidence')

DELETE from vocabularyterm where vocabulary_id = (select id from vocabulary where vocabularylabel = 'paralogy_prediction_method')

DELETE from vocabulary where vocabularylabel = 'paralogy_confidence'

DELETE from vocabulary where vocabularylabel = 'paralogy_prediction_method'