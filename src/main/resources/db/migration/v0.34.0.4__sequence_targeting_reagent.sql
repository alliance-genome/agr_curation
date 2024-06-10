-- Create sequencetargetingreagent tables and indexes
CREATE TABLE sequencetargetingreagent (
    name text,
    id bigint NOT NULL
);

ALTER TABLE sequencetargetingreagent OWNER TO postgres;

CREATE TABLE sequencetargetingreagent_reference (
    sequencetargetingreagent_id bigint NOT NULL,
    references_id bigint NOT NULL
);

ALTER TABLE sequencetargetingreagent_reference OWNER TO postgres;

CREATE TABLE sequencetargetingreagent_secondaryidentifiers (
    sequencetargetingreagent_id bigint NOT NULL,
    secondaryidentifiers character varying(255)
);

ALTER TABLE sequencetargetingreagent_secondaryidentifiers OWNER TO postgres;

CREATE TABLE sequencetargetingreagent_synonyms (
    sequencetargetingreagent_id bigint NOT NULL,
    synonyms character varying(255)
);

ALTER TABLE sequencetargetingreagent_synonyms OWNER TO postgres;
ALTER TABLE ONLY sequencetargetingreagent ADD CONSTRAINT sequencetargetingreagent_pkey PRIMARY KEY (id);

CREATE INDEX sequencetargetingreagent_reference_references_index ON sequencetargetingreagent_reference USING btree (references_id);
CREATE INDEX sequencetargetingreagent_reference_sqtr_index ON sequencetargetingreagent_reference USING btree (sequencetargetingreagent_id);
CREATE INDEX sequencetargetingreagent_secondaryidentifiers_sequencetargeting ON sequencetargetingreagent_secondaryidentifiers USING btree (sequencetargetingreagent_id);
CREATE INDEX sequencetargetingreagent_synonyms_sequencetargetingreagent_inde ON sequencetargetingreagent_synonyms USING btree (sequencetargetingreagent_id);

ALTER TABLE ONLY sequencetargetingreagent_reference ADD CONSTRAINT fk32mm52jxwmc7ad2ehx6aao39k FOREIGN KEY (references_id) REFERENCES  reference(id);
ALTER TABLE ONLY sequencetargetingreagent ADD CONSTRAINT fk7uk3x83djntuo2541gwjdh2gm FOREIGN KEY (id) REFERENCES  genomicentity(id);
ALTER TABLE ONLY sequencetargetingreagent_secondaryidentifiers ADD CONSTRAINT fk8owvqu69hylsvalgiwc3jiw4c FOREIGN KEY (sequencetargetingreagent_id) REFERENCES sequencetargetingreagent(id);
ALTER TABLE ONLY sequencetargetingreagent_reference ADD CONSTRAINT fkhy1pocbsto6tifv798hnjyk0g FOREIGN KEY (sequencetargetingreagent_id) REFERENCES sequencetargetingreagent(id);
ALTER TABLE ONLY sequencetargetingreagent_synonyms ADD CONSTRAINT fkkppdiik23lxyvtdcii4pd0ovt FOREIGN KEY (sequencetargetingreagent_id) REFERENCES sequencetargetingreagent(id);

-- Create bulk loads

INSERT INTO bulkloadgroup (id, name) VALUES (nextval('bulkloadgroup_seq'), 'Sequence Targeting Reagent Bulk Loads');
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'SEQUENCE_TARGETING_REAGENT', 'FB  Sequence Targeting Reagent Load', 'STOPPED', id FROM bulkloadgroup WHERE name = ' Sequence Targeting Reagent Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'SEQUENCE_TARGETING_REAGENT', 'HUMAN  Sequence Targeting Reagent Load', 'STOPPED', id FROM bulkloadgroup WHERE name = ' Sequence Targeting Reagent Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'SEQUENCE_TARGETING_REAGENT', 'MGI  Sequence Targeting Reagent Load', 'STOPPED', id FROM bulkloadgroup WHERE name = ' Sequence Targeting Reagent Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'SEQUENCE_TARGETING_REAGENT', 'RGD  Sequence Targeting Reagent Load', 'STOPPED', id FROM bulkloadgroup WHERE name = ' Sequence Targeting Reagent Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'SEQUENCE_TARGETING_REAGENT', 'SGD  Sequence Targeting Reagent Load', 'STOPPED', id FROM bulkloadgroup WHERE name = ' Sequence Targeting Reagent Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'SEQUENCE_TARGETING_REAGENT', 'WB  Sequence Targeting Reagent Load', 'STOPPED', id FROM bulkloadgroup WHERE name = ' Sequence Targeting Reagent Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'SEQUENCE_TARGETING_REAGENT', 'XBXL  Sequence Targeting Reagent Load', 'STOPPED', id FROM bulkloadgroup WHERE name = ' Sequence Targeting Reagent Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'SEQUENCE_TARGETING_REAGENT', 'XBXT  Sequence Targeting Reagent Load', 'STOPPED', id FROM bulkloadgroup WHERE name = ' Sequence Targeting Reagent Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'SEQUENCE_TARGETING_REAGENT', 'ZFIN  Sequence Targeting Reagent Load', 'STOPPED', id FROM bulkloadgroup WHERE name = ' Sequence Targeting Reagent Bulk Loads';
INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
	SELECT id, '0 0 22 ? * SUN-THU', false FROM bulkload WHERE backendbulkloadtype = 'SEQUENCE_TARGETING_REAGENT';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'SEQUENCE_TARGETING_REAGENT', 'FB' FROM bulkload WHERE name = 'FB  Sequence Targeting Reagent Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'SEQUENCE_TARGETING_REAGENT', 'HUMAN' FROM bulkload WHERE name = 'HUMAN  Sequence Targeting Reagent Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'SEQUENCE_TARGETING_REAGENT', 'MGI' FROM bulkload WHERE name = 'MGI  Sequence Targeting Reagent Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'SEQUENCE_TARGETING_REAGENT', 'RGD' FROM bulkload WHERE name = 'RGD  Sequence Targeting Reagent Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'SEQUENCE_TARGETING_REAGENT', 'SGD' FROM bulkload WHERE name = 'SGD  Sequence Targeting Reagent Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'SEQUENCE_TARGETING_REAGENT', 'WB' FROM bulkload WHERE name = 'WB  Sequence Targeting Reagent Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'SEQUENCE_TARGETING_REAGENT', 'XBXL' FROM bulkload WHERE name = 'XBXL  Sequence Targeting Reagent Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'SEQUENCE_TARGETING_REAGENT', 'XBXT' FROM bulkload WHERE name = 'XBXT  Sequence Targeting Reagent Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'SEQUENCE_TARGETING_REAGENT', 'ZFIN' FROM bulkload WHERE name = 'ZFIN  Sequence Targeting Reagent Load';