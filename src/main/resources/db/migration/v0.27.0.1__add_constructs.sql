CREATE TABLE reagent (
	id bigint PRIMARY KEY,
	datecreated timestamp without time zone,
	dateupdated timestamp without time zone,
	internal boolean NOT NULL DEFAULT false,
	obsolete boolean NOT NULL DEFAULT false,
	createdby_id bigint,
	updatedby_id bigint,
	dbdatecreated timestamp without time zone,
	dbdateupdated timestamp without time zone,
	curie varchar(255),
	modentityid varchar(255),
	modinternalid varchar(255),
	uniqueid varchar(2000),
	taxon_curie varchar(255),
	dataprovider_id bigint
);

ALTER TABLE reagent ADD CONSTRAINT reagent_createdby_id_fk FOREIGN KEY (createdby_id) REFERENCES person (id);
ALTER TABLE reagent ADD CONSTRAINT reagent_updatedby_id_fk FOREIGN KEY (updatedby_id) REFERENCES person (id);
ALTER TABLE reagent ADD CONSTRAINT reagent_taxon_curie_fk FOREIGN KEY (taxon_curie) REFERENCES ncbitaxonterm (curie);
ALTER TABLE reagent ADD CONSTRAINT reagent_dataprovider_id_fk FOREIGN KEY (dataprovider_id) REFERENCES dataprovider (id);

CREATE INDEX reagent_curie_index ON reagent USING btree (curie);
CREATE INDEX reagent_uniqueid_index ON reagent USING btree (uniqueid);
CREATE INDEX reagent_modentityid_index ON reagent USING btree (modentityid);
CREATE INDEX reagent_modinternalid_index ON reagent USING btree (modinternalid);
CREATE INDEX reagent_dataprovider_index ON reagent USING btree (dataprovider_id);
CREATE INDEX reagent_createdby_index ON reagent USING btree (createdby_id);
CREATE INDEX reagent_updatedby_index ON reagent USING btree (updatedby_id);
CREATE INDEX reagent_taxon_index ON reagent USING btree (taxon_curie);

CREATE TABLE reagent_aud (
	id bigint NOT NULL,
	curie varchar(255),
	modentityid varchar(255),
	modinternalid varchar(255),
	uniqueid varchar(2000),
	taxon_curie varchar(255),
	dataprovider_id bigint,
	rev integer NOT NULL,
	revtype smallint
);

ALTER TABLE reagent_aud ADD PRIMARY KEY (id, rev);

ALTER TABLE reagent_aud ADD CONSTRAINT reagent_aud_rev_fk FOREIGN KEY (rev) REFERENCES revinfo (rev);

CREATE TABLE construct (
	id bigint PRIMARY KEY,
	name varchar(255)
);

ALTER TABLE construct ADD CONSTRAINT construct_id_fk FOREIGN KEY (id) REFERENCES reagent (id);

CREATE TABLE construct_aud (
	id bigint NOT NULL,
    rev integer NOT NULL,
	name varchar(255)
);

ALTER TABLE construct_aud ADD PRIMARY KEY (id, rev);

ALTER TABLE construct_aud ADD CONSTRAINT construct_aud_id_rev_fk FOREIGN KEY (id, rev) REFERENCES reagent_aud (id, rev);

CREATE TABLE construct_reference (
	construct_id bigint,
	references_curie varchar(255)
);

ALTER TABLE construct_reference ADD CONSTRAINT construct_reference_construct_id_fk FOREIGN KEY (construct_id) REFERENCES construct (id);
ALTER TABLE construct_reference ADD CONSTRAINT construct_reference_references_curie_fk FOREIGN KEY (references_curie) REFERENCES reference (curie);

CREATE INDEX construct_reference_construct_id_index ON construct_reference USING btree (construct_id);
CREATE INDEX construct_reference_references_curie_index ON construct_reference USING btree (references_curie);

CREATE TABLE construct_reference_aud (
	construct_id bigint,
	references_curie varchar(255),
	rev integer NOT NULL,
	revtype smallint
);	
	
ALTER TABLE construct_reference_aud ADD PRIMARY KEY (construct_id, references_curie, rev);

ALTER TABLE construct_reference_aud ADD CONSTRAINT construct_reference_aud_rev_fk FOREIGN KEY (rev) REFERENCES revinfo (rev);

CREATE TABLE constructcomponentslotannotation (
	id bigint PRIMARY KEY,
	singleconstruct_id bigint,
	componentsymbol varchar(255),
	taxon_curie varchar(255),
	taxontext varchar(255)
);

ALTER TABLE constructcomponentslotannotation ADD CONSTRAINT constructcomponentslotannotation_id_fk FOREIGN KEY (id) REFERENCES slotannotation (id);
ALTER TABLE constructcomponentslotannotation ADD CONSTRAINT constructcomponentslotannotation_singleconstruct_id_fk FOREIGN KEY (singleconstruct_id) REFERENCES construct (id);
ALTER TABLE constructcomponentslotannotation ADD CONSTRAINT constructcomponentslotannotation_taxon_curie_fk FOREIGN KEY (taxon_curie) REFERENCES ncbitaxonterm (curie);

CREATE INDEX constructcomponentslotannotation_singleconstruct_index ON constructcomponentslotannotation USING btree (singleconstruct_id);
CREATE INDEX constructcomponentslotannotation_componentsymbol_index ON constructcomponentslotannotation USING btree (componentsymbol);
CREATE INDEX constructcomponentslotannotation_taxon_index ON constructcomponentslotannotation USING btree (taxon_curie);

CREATE TABLE constructcomponentslotannotation_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
	singleconstruct_id bigint,
	componentsymbol varchar(255),
	taxon_curie varchar(255),
	taxontext varchar(255)
);

ALTER TABLE constructcomponentslotannotation_aud ADD PRIMARY KEY (id, rev);

ALTER TABLE constructcomponentslotannotation_aud ADD CONSTRAINT constructcomponentslotannotation_aud_id_rev_fk FOREIGN KEY (id, rev) REFERENCES slotannotation_aud (id, rev);
	
	
CREATE TABLE constructcomponentslotannotation_note (
	constructcomponentslotannotation_id bigint,
	relatednotes_id bigint
);
	
ALTER TABLE constructcomponentslotannotation_note ADD CONSTRAINT constructcomponentslotannotation_note_ccsa_id_fk FOREIGN KEY (constructcomponentslotannotation_id) REFERENCES constructcomponentslotannotation (id);
ALTER TABLE constructcomponentslotannotation_note ADD CONSTRAINT constructcomponentslotannotation_note_relatednotes_id_fk FOREIGN KEY (relatednotes_id) REFERENCES note (id);

CREATE INDEX constructcomponentsa_note_ccsa_id_index ON constructcomponentslotannotation_note USING btree (constructcomponentslotannotation_id);
CREATE INDEX constructcomponentsa_note_relatednotes_id_index ON constructcomponentslotannotation_note USING btree (relatednotes_id);

CREATE TABLE constructcomponentslotannotation_note_aud (
	constructcomponentslotannotation_id bigint,
	relatednotes_id bigint,
	rev integer NOT NULL,
	revtype smallint
);	
	
ALTER TABLE constructcomponentslotannotation_note_aud ADD PRIMARY KEY (constructcomponentslotannotation_id, relatednotes_id, rev);

ALTER TABLE constructcomponentslotannotation_note_aud ADD CONSTRAINT constructcomponentslotannotation_note_aud_rev_fk FOREIGN KEY (rev) REFERENCES revinfo (rev);

	
INSERT INTO vocabulary (id, name, vocabularylabel) VALUES (nextval('hibernate_sequence'), 'Construct Component Note Type', 'construct_component_note_type');

INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'construct_component_note', id FROM vocabulary WHERE vocabularylabel = 'construct_component_note_type';
INSERT INTO vocabularyterm (id, name, vocabulary_id) SELECT nextval('hibernate_sequence'), 'construct_component_summary', id FROM vocabulary WHERE vocabularylabel = 'construct_component_note_type';

