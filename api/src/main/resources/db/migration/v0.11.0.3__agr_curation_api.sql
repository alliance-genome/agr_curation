ALTER TABLE allele
	ADD COLUMN inheritancemode_id bigint,
	ADD COLUMN incollection_id bigint,
	ADD COLUMN sequencingstatus_id bigint,
	ADD COLUMN isextinct boolean;
	
ALTER TABLE allele
	ADD CONSTRAINT allele_inheritancemode_id_fk
		FOREIGN KEY (inheritancemode_id) REFERENCES vocabularyterm (id);
	
ALTER TABLE allele
	ADD CONSTRAINT allele_incollection_id_fk
		FOREIGN KEY (incollection_id) REFERENCES vocabularyterm (id);
	
ALTER TABLE allele
	ADD CONSTRAINT allele_sequencingstatus_id_fk
		FOREIGN KEY (sequencingstatus_id) REFERENCES vocabularyterm (id);
		
ALTER TABLE allele_aud
	ADD COLUMN inheritancemode_id bigint,
	ADD COLUMN incollection_id bigint,
	ADD COLUMN sequencingstatus_id bigint,
	ADD COLUMN isextinct boolean;
	
CREATE TABLE allele_reference (
	allele_curie varchar(255) NOT NULL,
	references_curie varchar(255) NOT NULL
);
	
ALTER TABLE allele_reference
	ADD CONSTRAINT allele_reference_allele_curie_fk
		FOREIGN KEY (allele_curie) REFERENCES allele (curie);
	
ALTER TABLE allele_reference
	ADD CONSTRAINT allele_reference_references_curie_fk
		FOREIGN KEY (references_curie) REFERENCES reference (curie);
		
CREATE TABLE allele_reference_aud (
	rev integer NOT NULL,
	allele_curie varchar(255) NOT NULL,
	references_curie varchar(255) NOT NULL,
	revtype smallint,
	CONSTRAINT allele_reference_aud_pkey PRIMARY KEY(allele_curie, references_curie, rev)
);

INSERT INTO vocabulary (id, name)
	VALUES
		(nextval('hibernate_sequence'), 'Allele inheritance mode vocabulary'),
		(nextval('hibernate_sequence'), 'Allele collection vocabulary'),
		(nextval('hibernate_sequence'), 'Sequencing status vocabulary');
		
INSERT INTO vocabularyterm (id, name, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'dominant', id FROM vocabulary WHERE name = 'Allele inheritance mode vocabulary';
	
INSERT INTO vocabularyterm (id, name, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'semi-dominant', id FROM vocabulary WHERE name = 'Allele inheritance mode vocabulary';

INSERT INTO vocabularyterm (id, name, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'recessive', id FROM vocabulary WHERE name = 'Allele inheritance mode vocabulary';

INSERT INTO vocabularyterm (id, name, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'unknown', id FROM vocabulary WHERE name = 'Allele inheritance mode vocabulary';

INSERT INTO vocabularyterm (id, name, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'sequenced', id FROM vocabulary WHERE name = 'Sequencing status vocabulary';
	
INSERT INTO vocabularyterm (id, name, vocabulary_id)	
	SELECT nextval('hibernate_sequence'), 'not_sequenced', id FROM vocabulary WHERE name = 'Sequencing status vocabulary';

INSERT INTO vocabularyterm (id, name, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'pending_curation', id FROM vocabulary WHERE name = 'Sequencing status vocabulary';

INSERT INTO vocabularyterm (id, name, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'Million mutations project', id FROM vocabulary WHERE name = 'Allele collection vocabulary';

INSERT INTO vocabularyterm (id, name, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'KO consortium allele', id FROM vocabulary WHERE name = 'Allele collection vocabulary';
	
INSERT INTO vocabularyterm (id, name, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'CGH allele', id FROM vocabulary WHERE name = 'Allele collection vocabulary';
	
INSERT INTO vocabularyterm (id, name, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'NBP knockout allele', id FROM vocabulary WHERE name = 'Allele collection vocabulary';
	
INSERT INTO vocabularyterm (id, name, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'NemaGENETAG consortium allele', id FROM vocabulary WHERE name = 'Allele collection vocabulary';
	
INSERT INTO vocabularyterm (id, name, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'SNP Swan', id FROM vocabulary WHERE name = 'Allele collection vocabulary';
	
INSERT INTO vocabularyterm (id, name, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'SNP Wicks', id FROM vocabulary WHERE name = 'Allele collection vocabulary';
	
INSERT INTO vocabularyterm (id, name, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'WGS Anderson', id FROM vocabulary WHERE name = 'Allele collection vocabulary';
	
INSERT INTO vocabularyterm (id, name, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'WGS De Bono', id FROM vocabulary WHERE name = 'Allele collection vocabulary';
	
INSERT INTO vocabularyterm (id, name, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'WGS Flibotte', id FROM vocabulary WHERE name = 'Allele collection vocabulary';
	
INSERT INTO vocabularyterm (id, name, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'WGS Hawaiian Waterston', id FROM vocabulary WHERE name = 'Allele collection vocabulary';
	
INSERT INTO vocabularyterm (id, name, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'WGS Hobert', id FROM vocabulary WHERE name = 'Allele collection vocabulary';
	
INSERT INTO vocabularyterm (id, name, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'WGS Jarriault', id FROM vocabulary WHERE name = 'Allele collection vocabulary';
	
INSERT INTO vocabularyterm (id, name, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'WGS McGrath', id FROM vocabulary WHERE name = 'Allele collection vocabulary';
	
INSERT INTO vocabularyterm (id, name, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'WGS Pasadena Quinlan', id FROM vocabulary WHERE name = 'Allele collection vocabulary';
	
INSERT INTO vocabularyterm (id, name, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'WGS Stein', id FROM vocabulary WHERE name = 'Allele collection vocabulary';
	
INSERT INTO vocabularyterm (id, name, vocabulary_id)
	SELECT nextval('hibernate_sequence'), 'WGS Yanai', id FROM vocabulary WHERE name = 'Allele collection vocabulary';
		
	