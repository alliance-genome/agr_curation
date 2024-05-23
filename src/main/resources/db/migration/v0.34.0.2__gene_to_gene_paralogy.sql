CREATE SEQUENCE public.genetogeneparalogy_seq         START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE TABLE genetogeneparalogy (
 	id bigint CONSTRAINT genetogeneparalogy_pkey PRIMARY KEY,
 	subjectgene_id bigint,
 	objectgene_id bigint,
	identity integer,
	length integer,
	rank integer,
	similarity integer,
	confidence_id bigint,
 	datecreated timestamp without time zone,
 	dateupdated timestamp without time zone,
 	dbdatecreated timestamp without time zone,
	dbdateupdated timestamp without time zone,
 	internal boolean DEFAULT false,
 	obsolete boolean DEFAULT false,
 	createdby_id bigint,
 	updatedby_id bigint
	);
ALTER TABLE genetogeneparalogy
	ADD CONSTRAINT genetogeneparalogy_confidence_id_fk
		FOREIGN KEY (confidence_id) REFERENCES vocabularyterm (id);

ALTER TABLE genetogeneparalogy
	ADD CONSTRAINT genetogeneparalogy_createdby_id_fk
		FOREIGN KEY (createdby_id) REFERENCES person (id);

ALTER TABLE genetogeneparalogy
	ADD CONSTRAINT genetogeneparalogy_updatedby_id_fk
		FOREIGN KEY (updatedby_id) REFERENCES person (id);

ALTER TABLE genetogeneparalogy
	ADD CONSTRAINT genetogeneparalogy_subjectgene_id_fk
		FOREIGN KEY (subjectgene_id) REFERENCES gene (id);

ALTER TABLE genetogeneparalogy
	ADD CONSTRAINT genetogeneparalogy_objectgene_id_fk
		FOREIGN KEY (objectgene_id) REFERENCES gene (id);

CREATE INDEX genetogeneparalogy_createdby_index ON genetogeneparalogy USING btree (createdby_id);

CREATE INDEX genetogeneparalogy_updatedby_index ON genetogeneparalogy USING btree (updatedby_id);

CREATE INDEX genetogeneparalogy_subjectgene_index ON genetogeneparalogy USING btree (subjectgene_id);

CREATE INDEX genetogeneparalogy_objectgene_index ON genetogeneparalogy USING btree (objectgene_id);


CREATE TABLE genetogeneparalogy_predictionmethodsmatched (
  	genetogeneparalogy_id bigint NOT NULL,
	predictionmethodsmatched_id bigint NOT NULL
);

ALTER TABLE genetogeneparalogy_predictionmethodsmatched
	ADD CONSTRAINT g2gparalogy_pmm_g2gpara_id_fk
		FOREIGN KEY (genetogeneparalogy_id) REFERENCES genetogeneparalogy (id);

ALTER TABLE genetogeneparalogy_predictionmethodsmatched
	ADD CONSTRAINT g2gparalogy_pmm_pmm_id_fk
		FOREIGN KEY (predictionmethodsmatched_id) REFERENCES vocabularyterm (id);

CREATE INDEX g2gparalogypmm_paralogyid_index ON genetogeneparalogy_predictionmethodsmatched USING btree (genetogeneparalogy_id);

CREATE INDEX g2gparalogypmm_pmmid_index ON genetogeneparalogy_predictionmethodsmatched USING btree (predictionmethodsmatched_id);


CREATE TABLE genetogeneparalogy_predictionmethodsnotmatched (
															 genetogeneparalogy_id bigint NOT NULL,
															 predictionmethodsnotmatched_id bigint NOT NULL
);

ALTER TABLE genetogeneparalogy_predictionmethodsnotmatched
	ADD CONSTRAINT g2gparalogy_pmnm_g2gpara_id_fk
		FOREIGN KEY (genetogeneparalogy_id) REFERENCES genetogeneparalogy (id);

ALTER TABLE genetogeneparalogy_predictionmethodsnotmatched
	ADD CONSTRAINT g2gparalogy_pmnm_pmnm_id_fk
		FOREIGN KEY (predictionmethodsnotmatched_id) REFERENCES vocabularyterm (id);

CREATE INDEX g2gparalogypmnm_orthid_index ON genetogeneparalogy_predictionmethodsnotmatched USING btree (genetogeneparalogy_id);

CREATE INDEX g2gparalogypmnm_pmnmid_index ON genetogeneparalogy_predictionmethodsnotmatched USING btree (predictionmethodsnotmatched_id);



CREATE TABLE genetogeneparalogy_predictionmethodsnotcalled (
	 genetogeneparalogy_id bigint NOT NULL,
	 predictionmethodsnotcalled_id bigint NOT NULL
);

ALTER TABLE genetogeneparalogy_predictionmethodsnotcalled
	ADD CONSTRAINT g2gparalogy_pmnc_g2gpara_id_fk
		FOREIGN KEY (genetogeneparalogy_id) REFERENCES genetogeneparalogy (id);

ALTER TABLE genetogeneparalogy_predictionmethodsnotcalled
	ADD CONSTRAINT g2gparalogy_pmnc_pmnc_id_fk
		FOREIGN KEY (predictionmethodsnotcalled_id) REFERENCES vocabularyterm (id);

CREATE INDEX g2gparalogypmnc_orthid_index ON genetogeneparalogy_predictionmethodsnotcalled USING btree (genetogeneparalogy_id);

CREATE INDEX g2gparalogypmnc_pmncid_index ON genetogeneparalogy_predictionmethodsnotcalled USING btree (predictionmethodsnotcalled_id);

-- Create bulk loads

INSERT INTO bulkloadgroup (id, name) VALUES (nextval('bulkloadgroup_seq'), 'Paralogy Bulk Loads');
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'PARALOGY', 'FB Paralogy Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Paralogy Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'PARALOGY', 'HUMAN Paralogy Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Paralogy Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'PARALOGY', 'MGI Paralogy Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Paralogy Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'PARALOGY', 'RGD Paralogy Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Paralogy Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'PARALOGY', 'SGD Paralogy Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Paralogy Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'PARALOGY', 'WB Paralogy Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Paralogy Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'PARALOGY', 'XBXT Paralogy Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Paralogy Bulk Loads';
INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'PARALOGY', 'ZFIN Paralogy Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Paralogy Bulk Loads';
INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
SELECT id, '0 0 22 ? * SUN-THU', false FROM bulkload WHERE backendbulkloadtype = 'PARALOGY';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'PARALOGY', 'FB' FROM bulkload WHERE name = 'FB Paralogy Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'PARALOGY', 'HUMAN' FROM bulkload WHERE name = 'HUMAN Paralogy Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'PARALOGY', 'MGI' FROM bulkload WHERE name = 'MGI Paralogy Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'PARALOGY', 'RGD' FROM bulkload WHERE name = 'RGD Paralogy Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'PARALOGY', 'SGD' FROM bulkload WHERE name = 'SGD Paralogy Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'PARALOGY', 'WB' FROM bulkload WHERE name = 'WB Paralogy Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'PARALOGY', 'XBXT' FROM bulkload WHERE name = 'XBXT Paralogy Load';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
SELECT id, 'PARALOGY', 'ZFIN' FROM bulkload WHERE name = 'ZFIN Paralogy Load';
