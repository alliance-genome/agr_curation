CREATE TABLE variant (
	curie varchar(255) PRIMARY KEY,
	varianttype_curie varchar(255),
	variantstatus_id bigint,
	sourcegeneralconsequence_curie varchar(255)
);

ALTER TABLE variant ADD CONSTRAINT variant_curie_fk FOREIGN KEY (curie) REFERENCES genomicentity (curie);
ALTER TABLE variant ADD CONSTRAINT variant_varianttype_curie_fk FOREIGN KEY (varianttype_curie) REFERENCES soterm (curie);
ALTER TABLE variant ADD CONSTRAINT variant_variantstatus_id_fk FOREIGN KEY (variantstatus_id) REFERENCES vocabularyterm (id);
ALTER TABLE variant ADD CONSTRAINT variant_sourcegeneralconsequence_curie_fk FOREIGN KEY (sourcegeneralconsequence_curie) REFERENCES soterm (curie);

CREATE INDEX variant_varianttype_index ON variant USING btree (varianttype_curie);
CREATE INDEX variant_variantstatus_index ON variant USING btree (variantstatus_id);
CREATE INDEX variant_sourcegeneralconsequence_index ON variant USING btree (sourcegeneralconsequence_curie);

CREATE TABLE variant_aud (
	curie varchar(255),
	varianttype_curie varchar(255),
	variantstatus_id bigint,
	sourcegeneralconsequence_curie varchar(255),
	rev integer NOT NULL
);

ALTER TABLE variant_aud ADD PRIMARY KEY (curie, rev);

ALTER TABLE variant_aud ADD CONSTRAINT variant_aud_curie_rev_fk FOREIGN KEY (curie, rev) REFERENCES genomicentity_aud (curie, rev);

CREATE TABLE variant_note (
	variant_curie varchar(255),
	relatednotes_id bigint
);

ALTER TABLE variant_note ADD CONSTRAINT variant_note_variant_curie_fk FOREIGN KEY (variant_curie) REFERENCES variant (curie);
ALTER TABLE variant_note ADD CONSTRAINT variant_note_relatednotes_id_fk FOREIGN KEY (relatednotes_id) REFERENCES note (id);

CREATE INDEX variant_note_variant_curie_index ON variant_note USING btree (variant_curie);
CREATE INDEX variant_note_relatednotes_id_index ON variant_note USING btree (relatednotes_id);

CREATE TABLE variant_note_aud (
	variant_curie varchar(255),
	relatednotes_id bigint,
	rev integer NOT NULL,
	revtype smallint
);	
	
ALTER TABLE variant_note_aud ADD PRIMARY KEY (variant_curie, relatednotes_id, rev);

ALTER TABLE variant_note_aud ADD CONSTRAINT variant_note_aud_rev_fk FOREIGN KEY (rev) REFERENCES revinfo (rev);

	
INSERT INTO vocabulary (id, name, vocabularylabel) VALUES (nextval('hibernate_sequence'), 'Variant Status', 'variant_status');

-- INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'live', id FROM vocabulary WHERE vocabularylabel = 'variant_status';

CREATE TABLE tmp_vocab_link (
	vocabularytermsets_id bigint,
	memberterms_id bigint
	);
	
INSERT INTO vocabularytermset(id, name, vocabularylabel, vocabularytermsetvocabulary_id) SELECT nextval('hibernate_sequence'), 'Variant Note Type', 'variant_note_type', id FROM vocabulary WHERE vocabulary.vocabularylabel = 'note_type';

INSERT INTO tmp_vocab_link (memberterms_id) SELECT id FROM vocabularyterm WHERE name = 'comment' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'note_type');

UPDATE tmp_vocab_link SET vocabularytermsets_id = subquery.id
	FROM (SELECT id FROM vocabularytermset WHERE vocabularylabel = 'variant_note_type') AS subquery
	WHERE vocabularytermsets_id IS NULL;
	
INSERT INTO vocabularytermset_vocabularyterm (vocabularytermsets_id, memberterms_id)
	SELECT vocabularytermsets_id, memberterms_id FROM tmp_vocab_link;
	
DROP TABLE tmp_vocab_link;