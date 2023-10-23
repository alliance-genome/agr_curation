CREATE TABLE constructgenomicentityassociation (
	id bigint PRIMARY KEY,
	subject_id bigint,
	object_curie varchar(255),
	relation_id bigint
	);
	
ALTER TABLE constructgenomicentityassociation ADD CONSTRAINT constructgenomicentityassociation_id_fk FOREIGN KEY (id) REFERENCES association (id);
ALTER TABLE constructgenomicentityassociation ADD CONSTRAINT constructgenomicentityassociation_subject_id_fk FOREIGN KEY (subject_id) REFERENCES construct (id);
ALTER TABLE constructgenomicentityassociation ADD CONSTRAINT constructgenomicentityassociation_object_curie_fk FOREIGN KEY (object_curie) REFERENCES genomicentity (curie);
ALTER TABLE constructgenomicentityassociation ADD CONSTRAINT constructgenomicentityassociation_relation_id_fk FOREIGN KEY (relation_id) REFERENCES vocabularyterm (id);

CREATE INDEX constructgenomicentityassociation_subject_index ON constructgenomicentityassociation USING btree (subject_id);
CREATE INDEX constructgenomicentityassociation_object_index ON constructgenomicentityassociation USING btree (object_curie);
CREATE INDEX constructgenomicentityassociation_relation_index ON constructgenomicentityassociation USING btree (relation_id);

CREATE TABLE constructgenomicentityassociation_aud (
	id bigint NOT NULL,
	subject_id bigint,
	object_curie varchar(255),
	relation_id bigint,
	rev integer NOT NULL,
	PRIMARY KEY (id, rev)
	);

ALTER TABLE constructgenomicentityassociation_aud ADD CONSTRAINT constructgenomicentityassociation_aud_id_rev_fk FOREIGN KEY (id, rev) REFERENCES evidenceassociation_aud (id, rev);

CREATE TABLE constructgenomicentityassociation_note (
	constructgenomicentityassociation_id bigint,
	relatednotes_id bigint
);

ALTER TABLE constructgenomicentityassociation_note ADD CONSTRAINT cgeassociation_note_cgeassociation_id_fk FOREIGN KEY (constructgenomicentityassociation_id) REFERENCES constructgenomicentityassociation (id);
ALTER TABLE constructgenomicentityassociation_note ADD CONSTRAINT cgeassociation_note_relatednotes_id_fk FOREIGN KEY (relatednotes_id) REFERENCES note (id);

CREATE INDEX cgeassociation_note_cgeassociation_id_index ON constructgenomicentityassociation_note USING btree (constructgenomicentityassociation_id);
CREATE INDEX cgeassociation_note_relatednotes_id_index ON constructgenomicentityassociation_note USING btree (relatednotes_id);

CREATE TABLE constructgenomicentityassociation_note_aud (
	constructgenomicentityassociation_id bigint,
	relatednotes_id bigint,
	rev integer NOT NULL,
	revtype smallint
);	
	
ALTER TABLE constructgenomicentityassociation_note_aud ADD PRIMARY KEY (constructgenomicentityassociation_id, relatednotes_id, rev);

ALTER TABLE constructgenomicentityassociation_note_aud ADD CONSTRAINT constructgenomicentityassociation_note_aud_rev_fk FOREIGN KEY (rev) REFERENCES revinfo (rev);

CREATE TABLE tmp_vocab_link (
	vocabularytermsets_id bigint,
	memberterms_id bigint);
	
INSERT INTO tmp_vocab_link (memberterms_id)
	SELECT id FROM vocabularyterm WHERE vocabulary_id = (
		SELECT id from vocabulary where vocabularylabel = 'construct_relation'
	);
	
UPDATE tmp_vocab_link SET vocabularytermsets_id = subquery.id
	FROM (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'construct_genomic_entity_relation') AS subquery
	WHERE vocabularytermsets_id IS NULL;
	
INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
	SELECT vocabularytermsets_id, memberterms_id FROM tmp_vocab_link;
	
DROP TABLE tmp_vocab_link;