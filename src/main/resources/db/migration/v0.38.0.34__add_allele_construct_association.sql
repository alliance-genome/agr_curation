CREATE TABLE alleleconstructassociation (
	id bigint NOT NULL,
	datecreated timestamp(6) with time zone,
	dateupdated timestamp(6) with time zone,
	dbdatecreated timestamp(6) with time zone,
	dbdateupdated timestamp(6) with time zone,
	internal boolean DEFAULT false NOT NULL,
	obsolete boolean DEFAULT false NOT NULL,
	evidencecode_id bigint,
	relatednote_id bigint,
	createdby_id bigint,
	updatedby_id bigint,
	alleleconstructassociationobject_id bigint,
	alleleassociationsubject_id bigint,
	relation_id bigint
);

CREATE SEQUENCE alleleconstructassociation_seq
	START WITH 1
	INCREMENT BY 50
	NO MINVALUE
	NO MAXVALUE
	CACHE 1;

ALTER TABLE alleleconstructassociation ADD CONSTRAINT alleleconstructassociation_pkey PRIMARY KEY (id);

CREATE INDEX alleleconstructassociation_alleleconstructassociationobject_index ON alleleconstructassociation USING btree (alleleconstructassociationobject_id);
CREATE INDEX alleleconstructassociation_alleleassociationsubject_index ON alleleconstructassociation USING btree (alleleassociationsubject_id);
CREATE INDEX alleleconstructassociation_createdby_index ON alleleconstructassociation USING btree (createdby_id);
CREATE INDEX alleleconstructassociation_relation_index ON alleleconstructassociation USING btree (relation_id);
CREATE INDEX alleleconstructassociation_evidencecode_index ON alleleconstructassociation USING btree (evidencecode_id);
CREATE INDEX alleleconstructassociation_relatednote_index ON alleleconstructassociation USING btree (relatednote_id);
CREATE INDEX alleleconstructassociation_updatedby_index ON alleleconstructassociation USING btree (updatedby_id);

ALTER TABLE alleleconstructassociation ADD CONSTRAINT alleleconstructassociation_alleleconstructassociationobject_id FOREIGN KEY (alleleconstructassociationobject_id) REFERENCES construct(id);
ALTER TABLE alleleconstructassociation ADD CONSTRAINT alleleconstructassociation_alleleassociationsubject_id FOREIGN KEY (alleleassociationsubject_id) REFERENCES allele(id);
ALTER TABLE alleleconstructassociation ADD CONSTRAINT alleleconstructassociation_createdby_id FOREIGN KEY (createdby_id) REFERENCES person(id);
ALTER TABLE alleleconstructassociation ADD CONSTRAINT alleleconstructassociation_updatedby_id FOREIGN KEY (updatedby_id) REFERENCES person(id);
ALTER TABLE alleleconstructassociation ADD CONSTRAINT alleleconstructassociation_relation_id FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);
ALTER TABLE alleleconstructassociation ADD CONSTRAINT alleleconstructassociation_relatednote_id FOREIGN KEY (relatednote_id) REFERENCES note(id);
ALTER TABLE alleleconstructassociation ADD CONSTRAINT alleleconstructassociation_evidencecode_id FOREIGN KEY (evidencecode_id) REFERENCES ontologyterm(id);

  	
CREATE TABLE alleleconstructassociation_informationcontententity (
	association_id bigint NOT NULL,
	evidence_id bigint NOT NULL
);

CREATE INDEX idx2hpytnjrlx25iqq967hhg9rt4 ON alleleconstructassociation_informationcontententity USING btree(association_id);
CREATE INDEX idx3coboj3hrakhtffr5vyqft132 ON alleleconstructassociation_informationcontententity USING btree(evidence_id);

ALTER TABLE alleleconstructassociation_informationcontententity ADD CONSTRAINT alleleconstructassociation_ice_association_id_fk FOREIGN KEY (association_id) REFERENCES alleleconstructassociation(id);
ALTER TABLE alleleconstructassociation_informationcontententity ADD CONSTRAINT alleleconstructassociation_ice_evidence_id_fk FOREIGN KEY (evidence_id) REFERENCES informationcontententity(id);

INSERT INTO vocabularytermset(id, name, vocabularylabel, vocabularytermsetvocabulary_id) SELECT nextval('vocabularytermset_seq'), 'Allele Construct Association Relation', 'allele_construct_relation', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'allele_relation';

INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id) 
	WITH
  	t1 AS (
    	SELECT id FROM vocabularytermset WHERE name = 'Allele Construct Association Relation'
  	),
  	t2 AS (
    	SELECT id FROM vocabularyterm WHERE name = 'contains' AND vocabulary_id = (
    		SELECT id FROM vocabulary WHERE vocabularylabel = 'allele_relation'
    	)
  	)
  	SELECT t1.id, t2.id FROM t1,t2;
