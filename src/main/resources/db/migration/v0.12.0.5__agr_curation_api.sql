CREATE TABLE vocabularytermset (
	id bigint CONSTRAINT vocabularytermset_pkey PRIMARY KEY,
	datecreated timestamp without time zone,
	dateupdated timestamp without time zone,
	dbdatecreated timestamp without time zone,
	dbdateupdated timestamp without time zone,
	internal boolean DEFAULT false,
	obsolete boolean DEFAULT false,
	setdescription varchar(255),
	name varchar(255) NOT NULL,
	createdby_id bigint,
	updatedby_id bigint,
	vocabulary_id bigint NOT NULL
);
	
ALTER TABLE vocabularytermset
	ADD CONSTRAINT vocabularytermset_vocabulary_id_fk
		FOREIGN KEY (vocabulary_id) REFERENCES vocabulary (id);

CREATE TABLE vocabularytermset_aud (
	id bigint,
	rev integer,
	revtype smallint,
	setdescription varchar(255),
	name varchar(255),
	vocabulary_id bigint,
	PRIMARY KEY (id, rev)
);
	

CREATE TABLE vocabularytermset_vocabularyterm (
	vocabularytermset_id bigint NOT NULL,
	memberterms_id bigint NOT NULL
);

ALTER TABLE vocabularytermset_vocabularyterm
	ADD CONSTRAINT vocabularytermset_vocabularyterm_vocabularytermset_id_fk
		FOREIGN KEY (vocabularytermset_id) REFERENCES vocabularytermset (id);


ALTER TABLE vocabularytermset_vocabularyterm
	ADD CONSTRAINT vocabularytermset_vocabularyterm_memberterms_id_fk
		FOREIGN KEY (memberterms_id) REFERENCES vocabularyterm (id);


CREATE TABLE vocabularytermset_vocabularyterm_aud (
	vocabularytermset_id bigint NOT NULL,
	memberterms_id bigint NOT NULL,
	rev integer,
	revtype smallint,
	PRIMARY KEY (rev, vocabularytermset_id, memberterms_id)
);

INSERT INTO vocabularytermset (id, name, vocabulary_id, setdescription)
	SELECT nextval('hibernate_sequence'), 'Allele disease relations', id, 'Disease relation vocabulary terms that are valid for allele disease annotations' FROM vocabulary WHERE name = 'Disease Relation Vocabulary';

INSERT INTO vocabularytermset (id, name, vocabulary_id, setdescription)
	SELECT nextval('hibernate_sequence'), 'AGM disease relations', id, 'Disease relation vocabulary terms that are valid for AGM disease annotations' FROM vocabulary WHERE name = 'Disease Relation Vocabulary';

INSERT INTO vocabularytermset (id, name, vocabulary_id, setdescription)
	SELECT nextval('hibernate_sequence'), 'Gene disease relations', id, 'Disease relation vocabulary terms that are valid for gene disease annotations' FROM vocabulary WHERE name = 'Disease Relation Vocabulary';

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermset_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE name = 'Allele disease relations'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'is_implicated_in' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE name = 'Disease Relation Vocabulary'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermset_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE name = 'Gene disease relations'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'is_implicated_in' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE name = 'Disease Relation Vocabulary'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermset_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE name = 'Gene disease relations'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'is_marker_for' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE name = 'Disease Relation Vocabulary'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermset_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE name = 'AGM disease relations'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'is_model_of' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE name = 'Disease Relation Vocabulary'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermset_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE name = 'AGM disease relations'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'is_ameliorated_model_of' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE name = 'Disease Relation Vocabulary'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermset_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE name = 'AGM disease relations'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'is_exacerbated_model_of' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE name = 'Disease Relation Vocabulary'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;
  	
UPDATE diseaseannotation
	SET diseaserelation_id = (
    	SELECT id FROM vocabularyterm WHERE name = 'is_implicated_in' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE name = 'Disease Relation Vocabulary'
    	)
	) WHERE diseaserelation_id = (
    	SELECT id FROM vocabularyterm WHERE name = 'is_implicated_in' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE name = 'Allele disease relations'
    	)
    );
  	
UPDATE diseaseannotation
	SET diseaserelation_id = (
    	SELECT id FROM vocabularyterm WHERE name = 'is_implicated_in' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE name = 'Disease Relation Vocabulary'
    	)
	) WHERE diseaserelation_id = (
    	SELECT id FROM vocabularyterm WHERE name = 'is_implicated_in' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE name = 'Gene disease relations'
    	)
    );
  	
UPDATE diseaseannotation
	SET diseaserelation_id = (
    	SELECT id FROM vocabularyterm WHERE name = 'is_marker_for' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE name = 'Disease Relation Vocabulary'
    	)
	) WHERE diseaserelation_id = (
    	SELECT id FROM vocabularyterm WHERE name = 'is_marker_for' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE name = 'Gene disease relations'
    	)
    );
  	
UPDATE diseaseannotation
	SET diseaserelation_id = (
    	SELECT id FROM vocabularyterm WHERE name = 'is_model_of' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE name = 'Disease Relation Vocabulary'
    	)
	) WHERE diseaserelation_id = (
    	SELECT id FROM vocabularyterm WHERE name = 'is_model_of' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE name = 'AGM disease relations'
    	)
    );
  	
UPDATE diseaseannotation
	SET diseaserelation_id = (
    	SELECT id FROM vocabularyterm WHERE name = 'is_ameliorated_model_of' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE name = 'Disease Relation Vocabulary'
    	)
	) WHERE diseaserelation_id = (
    	SELECT id FROM vocabularyterm WHERE name = 'is_ameliorated_model_of' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE name = 'AGM disease relations'
    	)
    );
  	
UPDATE diseaseannotation
	SET diseaserelation_id = (
    	SELECT id FROM vocabularyterm WHERE name = 'is_exacerbated_model_of' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE name = 'Disease Relation Vocabulary'
    	)
	) WHERE diseaserelation_id = (
    	SELECT id FROM vocabularyterm WHERE name = 'is_exacerbated_model_of' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE name = 'AGM disease relations'
    	)
    );
    
DELETE FROM vocabularyterm WHERE vocabulary_id IN (
	SELECT id FROM vocabulary WHERE name LIKE '% disease relations'
);

DELETE FROM vocabulary WHERE name LIKE '% disease relations';			