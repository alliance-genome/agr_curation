DELETE FROM resourcedescriptorpage WHERE resourcedescriptor_id = (SELECT id FROM resourcedescriptor WHERE prefix ='MIM');
DELETE FROM resourcedescriptor_synonyms WHERE resourcedescriptor_id = (SELECT id FROM resourcedescriptor WHERE prefix ='MIM');
DELETE FROM resourcedescriptor WHERE prefix = 'MIM';
UPDATE resourcedescriptor SET prefix = 'MIM', name = 'MIM', idpattern = '^(MIM|mim):[PS]*\\d+$', idexample = 'MIM:600483, MIM:PS100200' WHERE prefix = 'OMIM';
INSERT INTO resourcedescriptor_synonyms (resourcedescriptor_id, synonyms) SELECT id, 'mim' FROM resourcedescriptor WHERE prefix = 'MIM';