-- Remove taxon from reagent tables
ALTER TABLE reagent DROP COLUMN taxon_curie;
ALTER TABLE reagent_aud DROP COLUMN taxon_curie;

-- Add allianceMemberReleaseVersion to bulkloadfile tables
ALTER TABLE bulkloadfile ADD COLUMN alliancememberreleaseversion varchar(255);
ALTER TABLE bulkloadfile_aud ADD COLUMN alliancememberreleaseversion varchar(255);

-- Add constructSymbolSlotAnnotation tables, constraints and indexes
CREATE TABLE constructsymbolslotannotation (
	id bigint PRIMARY KEY,
	singleconstruct_id bigint
	);
	
ALTER TABLE constructsymbolslotannotation ADD CONSTRAINT constructsymbolslotannotation_id_fk FOREIGN KEY (id) REFERENCES nameslotannotation (id);
ALTER TABLE constructsymbolslotannotation ADD CONSTRAINT constructsymbolslotannotation_singleconstruct_id_fk FOREIGN KEY (singleconstruct_id) REFERENCES construct (id);

CREATE INDEX constructsymbol_singleconstruct_id_index ON constructsymbolslotannotation USING btree (singleconstruct_id);

CREATE TABLE constructsymbolslotannotation_aud (
	id bigint NOT NULL,
	singleconstruct_id bigint,
	rev integer NOT NULL
	);

ALTER TABLE constructsymbolslotannotation_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE constructsymbolslotannotation_aud ADD CONSTRAINT constructsymbolslotannotation_aud_id_rev_fk FOREIGN KEY (id, rev) REFERENCES nameslotannotation_aud (id, rev);

-- Add constructSynonymSlotAnnotation tables, constraints and indexes
CREATE TABLE constructsynonymslotannotation (
	id bigint PRIMARY KEY,
	singleconstruct_id bigint
	);
	
ALTER TABLE constructsynonymslotannotation ADD CONSTRAINT constructsynonymslotannotation_id_fk FOREIGN KEY (id) REFERENCES nameslotannotation (id);
ALTER TABLE constructsynonymslotannotation ADD CONSTRAINT constructsynonymslotannotation_singleconstruct_id_fk FOREIGN KEY (singleconstruct_id) REFERENCES construct (id);

CREATE INDEX constructsynonym_singleconstruct_id_index ON constructsynonymslotannotation USING btree (singleconstruct_id);

CREATE TABLE constructsynonymslotannotation_aud (
	id bigint NOT NULL,
	singleconstruct_id bigint,
	rev integer NOT NULL
	);

ALTER TABLE constructsynonymslotannotation_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE constructsynonymslotannotation_aud ADD CONSTRAINT constructsynonymslotannotation_aud_id_rev_fk FOREIGN KEY (id, rev) REFERENCES nameslotannotation_aud (id, rev);

-- Add constructFullNameSlotAnnotation tables, constraints and indexes
CREATE TABLE constructfullnameslotannotation (
	id bigint PRIMARY KEY,
	singleconstruct_id bigint
	);
	
ALTER TABLE constructfullnameslotannotation ADD CONSTRAINT constructfullnameslotannotation_id_fk FOREIGN KEY (id) REFERENCES nameslotannotation (id);
ALTER TABLE constructfullnameslotannotation ADD CONSTRAINT constructfullnameslotannotation_singleconstruct_id_fk FOREIGN KEY (singleconstruct_id) REFERENCES construct (id);

CREATE INDEX constructfullname_singleconstruct_id_index ON constructfullnameslotannotation USING btree (singleconstruct_id);

CREATE TABLE constructfullnameslotannotation_aud (
	id bigint NOT NULL,
	singleconstruct_id bigint,
	rev integer NOT NULL
	);

ALTER TABLE constructfullnameslotannotation_aud ADD PRIMARY KEY (id, rev);
ALTER TABLE constructfullnameslotannotation_aud ADD CONSTRAINT constructfullnameslotannotation_aud_id_rev_fk FOREIGN KEY (id, rev) REFERENCES nameslotannotation_aud (id, rev);

-- Transfer data and remove name field
ALTER TABLE slotannotation ADD COLUMN tmp_construct_id bigint;
ALTER TABLE slotannotation ADD COLUMN tmp_construct_symbol varchar(255);
ALTER TABLE slotannotation ADD COLUMN tmp_nametype_id bigint;
INSERT INTO slotannotation (id, tmp_construct_id, tmp_construct_symbol) SELECT nextval('hibernate_sequence'), id, name FROM construct;
UPDATE slotannotation SET tmp_nametype_id = subquery.id FROM (SELECT id FROM vocabularyterm WHERE name = 'nomenclature_symbol' AND vocabulary_id = (SELECT id FROM vocabulary WHERE vocabularylabel = 'name_type')) AS subquery WHERE slotannotation.tmp_construct_id IS NOT NULL;
INSERT INTO nameslotannotation (id, displaytext, formattext, nametype_id) SELECT id, tmp_construct_symbol, tmp_construct_symbol, tmp_nametype_id FROM slotannotation WHERE tmp_construct_id IS NOT NULL;
INSERT INTO constructsymbolslotannotation (id, singleconstruct_id) SELECT id, tmp_construct_id FROM slotannotation WHERE tmp_construct_id IS NOT NULL;
ALTER TABLE slotannotation DROP COLUMN tmp_construct_id;
ALTER TABLE slotannotation DROP COLUMN tmp_construct_symbol;
ALTER TABLE slotannotation DROP COLUMN tmp_nametype_id;
ALTER TABLE construct DROP COLUMN name;

-- Add reagent_secondaryidentifiers tables
CREATE TABLE reagent_secondaryidentifiers (
	reagent_id bigint NOT NULL,
	secondaryidentifiers varchar(255)
	);

ALTER TABLE reagent_secondaryidentifiers ADD CONSTRAINT reagent_secondaryidentifiers_reagent_id_fk FOREIGN KEY (reagent_id) REFERENCES reagent (id);

CREATE INDEX reagent_secondaryidentifiers_reagent_id_index ON reagent_secondaryidentifiers USING btree (reagent_id);

CREATE TABLE reagent_secondaryidentifiers_aud (
	reagent_id bigint NOT NULL,
	secondaryidentifiers varchar(255),
	rev integer NOT NULl,
	revtype smallint
	);
	
ALTER TABLE reagent_secondaryidentifiers_aud ADD PRIMARY KEY (reagent_id, secondaryidentifiers, rev);

ALTER TABLE reagent_secondaryidentifiers_aud ADD CONSTRAINT reagent_secondaryidentifiers_aud_rev_fk FOREIGN KEY (rev) REFERENCES revinfo (rev);

-- Add construct component relation

ALTER TABLE constructcomponentslotannotation ADD COLUMN relation_id bigint;
ALTER TABLE constructcomponentslotannotation_aud ADD COLUMN relation_id bigint;

ALTER TABLE constructcomponentslotannotation ADD CONSTRAINT constructcomponentslotannotation_relation_id_fk FOREIGN KEY (relation_id) REFERENCES vocabularyterm (id);

CREATE INDEX constructcomponentslotannotation_relation_index ON constructcomponentslotannotation USING btree (relation_id);

-- Need longer display and format text fields for long construct names
ALTER TABLE nameslotannotation ALTER COLUMN displaytext TYPE varchar(1500);
ALTER TABLE nameslotannotation ALTER COLUMN formattext TYPE varchar(1500);
ALTER TABLE nameslotannotation_aud ALTER COLUMN displaytext TYPE varchar(1500);
ALTER TABLE nameslotannotation_aud ALTER COLUMN formattext TYPE varchar(1500);
