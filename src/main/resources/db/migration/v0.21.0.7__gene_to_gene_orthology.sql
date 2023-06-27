CREATE TABLE genetogeneorthology (
	id bigint CONSTRAINT genetogeneorthology_pkey PRIMARY KEY,
	subjectgene_curie varchar (255),
	objectgene_curie varchar (255),
	datecreated timestamp without time zone,
	dateupdated timestamp without time zone,
	dbdatecreated timestamp without time zone,
	dbdateupdated timestamp without time zone,
	internal boolean DEFAULT false,
	obsolete boolean DEFAULT false,
	createdby_id bigint,
	updatedby_id bigint
	);
	
CREATE TABLE genetogeneorthology_aud (
	id bigint NOT NULL,
	rev integer NOT NULL,
	revtype smallint,
	subjectgene_curie varchar (255),
	objectgene_curie varchar (255),
	PRIMARY KEY (id, rev)
);
	
ALTER TABLE genetogeneorthology
	ADD CONSTRAINT genetogeneorthology_createdby_id_fk
		FOREIGN KEY (createdby_id) REFERENCES person (id);	

ALTER TABLE genetogeneorthology
	ADD CONSTRAINT genetogeneorthology_updatedby_id_fk
		FOREIGN KEY (updatedby_id) REFERENCES person (id);

ALTER TABLE genetogeneorthology
	ADD CONSTRAINT genetogeneorthology_subjectgene_curie_fk
		FOREIGN KEY (subjectgene_curie) REFERENCES gene (curie);

ALTER TABLE genetogeneorthology
	ADD CONSTRAINT genetogeneorthology_objectgene_curie_fk
		FOREIGN KEY (objectgene_curie) REFERENCES gene (curie);

ALTER TABLE genetogeneorthology_aud
	ADD CONSTRAINT genetogeneorthology_aud_rev_fk
		FOREIGN KEY (rev) REFERENCES revinfo (rev);

CREATE INDEX genetogeneorthology_createdby_index ON genetogeneorthology USING btree (createdby_id);

CREATE INDEX genetogeneorthology_updatedby_index ON genetogeneorthology USING btree (updatedby_id);

CREATE INDEX genetogeneorthology_subjectgene_index ON genetogeneorthology USING btree (subjectgene_curie);

CREATE INDEX genetogeneorthology_objectgene_index ON genetogeneorthology USING btree (objectgene_curie);

CREATE TABLE genetogeneorthologycurated (
	id bigint CONSTRAINT genetogeneorthologycurated_pkey PRIMARY KEY,
	singlereference_curie varchar (255),
	evidencecode_curie varchar (255)
);
	
CREATE TABLE genetogeneorthologycurated_aud (
	id bigint NOT NULL,
	rev integer NOT NULL,
	singlereference_curie varchar (255),
	evidencecode_curie varchar (255),
	PRIMARY KEY (id, rev)
);

ALTER TABLE genetogeneorthologycurated
	ADD CONSTRAINT genetogeneorthologycurated_id_fk
		FOREIGN KEY (id) REFERENCES genetogeneorthology (id);

ALTER TABLE genetogeneorthologycurated
	ADD CONSTRAINT genetogeneorthologycurated_singlereference_curie_fk
		FOREIGN KEY (singlereference_curie) REFERENCES reference (curie);

ALTER TABLE genetogeneorthologycurated
	ADD CONSTRAINT genetogeneorthologycurated_evidencecode_curie_fk
		FOREIGN KEY (evidencecode_curie) REFERENCES ecoterm (curie);

ALTER TABLE genetogeneorthologycurated_aud
	ADD CONSTRAINT genetogeneorthologycurated_aud_id_rev_fk
		FOREIGN KEY (id, rev) REFERENCES genetogeneorthology_aud (id, rev);

CREATE INDEX genetogeneorthologycurated_singlereference_index ON genetogeneorthologycurated USING btree (singlereference_curie);

CREATE INDEX genetogeneorthologycurated_evidencecode_index ON genetogeneorthologycurated USING btree (evidencecode_curie);

CREATE TABLE genetogeneorthologygenerated (
	id bigint CONSTRAINT genetogeneorthologygenerated_pkey PRIMARY KEY,
	isbestscore_id bigint,
	isbestscorereverse_id bigint,
	confidence_id bigint,
	strictfilter boolean,
	moderatefilter boolean
);
	
CREATE TABLE genetogeneorthologygenerated_aud (
	id bigint NOT NULL,
	rev integer NOT NULL,
	isbestscore_id bigint,
	isbestscorereverse_id bigint,
	confidence_id bigint,
	strictfilter boolean,
	moderatefilter boolean,
	PRIMARY KEY (id, rev)
);

ALTER TABLE genetogeneorthologygenerated
	ADD CONSTRAINT genetogeneorthologygenerated_id_fk
		FOREIGN KEY (id) REFERENCES genetogeneorthology (id);

ALTER TABLE genetogeneorthologygenerated
	ADD CONSTRAINT genetogeneorthologygenerated_isbestscore_id_fk
		FOREIGN KEY (isbestscore_id) REFERENCES vocabularyterm (id);

ALTER TABLE genetogeneorthologygenerated
	ADD CONSTRAINT genetogeneorthologygenerated_isbestscorereverse_id_fk
		FOREIGN KEY (isbestscorereverse_id) REFERENCES vocabularyterm (id);

ALTER TABLE genetogeneorthologygenerated
	ADD CONSTRAINT genetogeneorthologygenerated_confidence_id_fk
		FOREIGN KEY (confidence_id) REFERENCES vocabularyterm (id);

ALTER TABLE genetogeneorthologygenerated_aud
	ADD CONSTRAINT genetogeneorthologygenerated_aud_id_rev_fk
		FOREIGN KEY (id, rev) REFERENCES genetogeneorthology_aud (id, rev);

CREATE INDEX genetogeneorthologygenerated_isbestscore_index ON genetogeneorthologygenerated USING btree (isbestscore_id);

CREATE INDEX genetogeneorthologygenerated_isbestscorereverse_index ON genetogeneorthologygenerated USING btree (isbestscorereverse_id);

CREATE INDEX genetogeneorthologygenerated_confidence_index ON genetogeneorthologygenerated USING btree (confidence_id);

CREATE TABLE genetogeneorthologygenerated_predictionmethodsmatched (
	genetogeneorthologygenerated_id bigint NOT NULL,
	predictionmethodsmatched_id bigint NOT NULL
);

CREATE TABLE genetogeneorthologygenerated_predictionmethodsmatched_aud (
	rev integer NOT NULL,
	revtype smallint,
	genetogeneorthologygenerated_id bigint NOT NULL,
	predictionmethodsmatched_id bigint NOT NULL
);

ALTER TABLE genetogeneorthologygenerated_predictionmethodsmatched
	ADD CONSTRAINT g2gorthgenerated_pmm_g2gorth_id_fk
		FOREIGN KEY (genetogeneorthologygenerated_id) REFERENCES genetogeneorthologygenerated (id);

ALTER TABLE genetogeneorthologygenerated_predictionmethodsmatched
	ADD CONSTRAINT g2gorthgenerated_pmm_pmm_id_fk
		FOREIGN KEY (predictionmethodsmatched_id) REFERENCES vocabularyterm (id);

ALTER TABLE genetogeneorthologygenerated_predictionmethodsmatched_aud
	ADD CONSTRAINT g2gorthgenerated_pmm_aud_rev_fk
		FOREIGN KEY (rev) REFERENCES revinfo (rev);

CREATE INDEX g2gorthgeneratedpmm_orthid_index ON genetogeneorthologygenerated_predictionmethodsmatched USING btree (genetogeneorthologygenerated_id);

CREATE INDEX g2gorthgeneratedpmm_pmmid_index ON genetogeneorthologygenerated_predictionmethodsmatched USING btree (predictionmethodsmatched_id);

CREATE TABLE genetogeneorthologygenerated_predictionmethodsnotmatched (
	genetogeneorthologygenerated_id bigint NOT NULL,
	predictionmethodsnotmatched_id bigint NOT NULL
);

CREATE TABLE genetogeneorthologygenerated_predictionmethodsnotmatched_aud (
	rev integer NOT NULL,
	revtype smallint,
	genetogeneorthologygenerated_id bigint NOT NULL,
	predictionmethodsnotmatched_id bigint NOT NULL
);

ALTER TABLE genetogeneorthologygenerated_predictionmethodsnotmatched
	ADD CONSTRAINT g2gorthgenerated_pmnm_g2gorth_id_fk
		FOREIGN KEY (genetogeneorthologygenerated_id) REFERENCES genetogeneorthologygenerated (id);

ALTER TABLE genetogeneorthologygenerated_predictionmethodsnotmatched
	ADD CONSTRAINT g2gorthgenerated_pmnm_pmnm_id_fk
		FOREIGN KEY (predictionmethodsnotmatched_id) REFERENCES vocabularyterm (id);

ALTER TABLE genetogeneorthologygenerated_predictionmethodsnotmatched_aud
	ADD CONSTRAINT g2gorthgenerated_pmnm_aud_rev_fk
		FOREIGN KEY (rev) REFERENCES revinfo (rev);

CREATE INDEX g2gorthgeneratedpmnm_orthid_index ON genetogeneorthologygenerated_predictionmethodsnotmatched USING btree (genetogeneorthologygenerated_id);

CREATE INDEX g2gorthgeneratedpmnm_pmnmid_index ON genetogeneorthologygenerated_predictionmethodsnotmatched USING btree (predictionmethodsnotmatched_id);

CREATE TABLE genetogeneorthologygenerated_predictionmethodsnotcalled (
	genetogeneorthologygenerated_id bigint NOT NULL,
	predictionmethodsnotcalled_id bigint NOT NULL
);

CREATE TABLE genetogeneorthologygenerated_predictionmethodsnotcalled_aud (
	rev integer NOT NULL,
	revtype smallint,
	genetogeneorthologygenerated_id bigint NOT NULL,
	predictionmethodsnotcalled_id bigint NOT NULL
);

ALTER TABLE genetogeneorthologygenerated_predictionmethodsnotcalled
	ADD CONSTRAINT g2gorthgenerated_pmnc_g2gorth_id_fk
		FOREIGN KEY (genetogeneorthologygenerated_id) REFERENCES genetogeneorthologygenerated (id);

ALTER TABLE genetogeneorthologygenerated_predictionmethodsnotcalled
	ADD CONSTRAINT g2gorthgenerated_pmnc_pmnc_id_fk
		FOREIGN KEY (predictionmethodsnotcalled_id) REFERENCES vocabularyterm (id);

ALTER TABLE genetogeneorthologygenerated_predictionmethodsnotcalled_aud
	ADD CONSTRAINT g2gorthgenerated_pmnc_aud_rev_fk
		FOREIGN KEY (rev) REFERENCES revinfo (rev);

CREATE INDEX g2gorthgeneratedpmnc_orthid_index ON genetogeneorthologygenerated_predictionmethodsnotcalled USING btree (genetogeneorthologygenerated_id);

CREATE INDEX g2gorthgeneratedpmnc_pmncid_index ON genetogeneorthologygenerated_predictionmethodsnotcalled USING btree (predictionmethodsnotcalled_id);