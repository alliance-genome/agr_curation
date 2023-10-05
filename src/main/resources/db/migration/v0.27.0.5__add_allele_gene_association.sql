CREATE TABLE allelegenomicentityassociation (
	id bigint PRIMARY KEY,
	evidencecode_curie varchar(255),
	relatednote_id bigint,
	relation_id bigint,
	subject_curie varchar(255)
	);
	
ALTER TABLE allelegenomicentityassociation ADD CONSTRAINT allelegenomicentityassociation_id_fk FOREIGN KEY (id) REFERENCES association (id);
ALTER TABLE allelegenomicentityassociation ADD CONSTRAINT allelegenomicentityassociation_evidencecode_curie_fk FOREIGN KEY (evidencecode_curie) REFERENCES ecoterm (curie);
ALTER TABLE allelegenomicentityassociation ADD CONSTRAINT allelegenomicentityassociation_relatednote_id_fk FOREIGN KEY (relatednote_id) REFERENCES note (id);
ALTER TABLE allelegenomicentityassociation ADD CONSTRAINT allelegenomicentityassociation_relation_id_fk FOREIGN KEY (relation_id) REFERENCES vocabularyterm (id);
ALTER TABLE allelegenomicentityassociation ADD CONSTRAINT allelegenomicentityassociation_subject_curie_fk FOREIGN KEY (subject_curie) REFERENCES allele (curie);

CREATE INDEX allelegenomicentityassociation_subject_index ON allelegenomicentityassociation USING btree (subject_curie);
CREATE INDEX allelegenomicentityassociation_relation_index ON allelegenomicentityassociation USING btree (relation_id);

CREATE TABLE allelegenomicentityassociation_aud (
	id bigint NOT NULL,
	evidencecode_curie varchar(255),
	relatednote_id bigint,
	relation_id bigint,
	subject_curie varchar(255),
	rev integer NOT NULL,
	PRIMARY KEY (id, rev)
	);

ALTER TABLE allelegenomicentityassociation_aud ADD CONSTRAINT allelegenomicentityassociation_aud_id_rev_fk FOREIGN KEY (id, rev) REFERENCES evidenceassociation_aud (id, rev);

CREATE TABLE allelegeneassociation (
	id bigint PRIMARY KEY,
	object_curie varchar(255),
	subject_curie varchar (255)
	);
	
ALTER TABLE allelegeneassociation ADD CONSTRAINT allelegeneassociation_id_fk FOREIGN KEY (id) REFERENCES allelegenomicentityassociation (id);
ALTER TABLE allelegeneassociation ADD CONSTRAINT allelegeneassociation_subject_curie_fk FOREIGN KEY (subject_curie) REFERENCES allele (curie);
ALTER TABLE allelegeneassociation ADD CONSTRAINT allelegeneassociation_object_curie_fk FOREIGN KEY (object_curie) REFERENCES gene (curie);

CREATE INDEX allelegeneassociation_object_index ON allelegeneassociation USING btree (object_curie);

CREATE TABLE allelegeneassociation_aud (
	id bigint NOT NULL,
	rev integer NOT NULL,
	object_curie varchar(255),
	PRIMARY KEY (id, rev)
	);

ALTER TABLE allelegeneassociation_aud ADD CONSTRAINT allelegeneassociation_aud_id_rev_fk FOREIGN KEY (id, rev) REFERENCES allelegenomicentityassociation_aud (id, rev);