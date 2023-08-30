 -- Rename DiseaseAnnotation_updatedby_index and DiseaseAnnotation_createdby_index
 -- Rename Association tables as Annotation and add columns
 -- Recreate Association tables
 
 -- Delete DiseaseAnnotation_dataProvider_index DiseaseAnnotation_singleReference_index
 -- Add columns to annotation, copy data and delete from diseaseannotation
 -- Create annotation_dataprovider_index annotation_singlereference_index
 
ALTER TABLE association RENAME TO annotation;
ALTER TABLE annotation RENAME CONSTRAINT association_pkey TO annotation_pkey;
ALTER TABLE annotation RENAME CONSTRAINT fkpsajgrrbs4x9panhka0cq9ihy TO annotation_createdby_id_fk;
ALTER TABLE annotation RENAME CONSTRAINT association_updatedby_id_fk TO annotation_updatedby_id_fk;
ALTER INDEX diseaseannotation_createdby_index RENAME TO annotation_createdby_index;
ALTER INDEX diseaseannotation_updatedby_index RENAME TO annotation_updatedby_index;


ALTER TABLE association_aud RENAME TO annotation_aud;
ALTER TABLE annotation_aud RENAME CONSTRAINT association_aud_pkey TO annotation_aud_pkey;
ALTER TABLE annotation_aud RENAME CONSTRAINT fk2cnuv5m2xs6vaupmjsnce0sq6 TO annotation_aud_rev_fk;

CREATE TABLE association (
	id bigint PRIMARY KEY,
	datecreated timestamp without time zone,
	dateupdated timestamp without time zone,
	internal boolean NOT NULL DEFAULT false,
	obsolete boolean NOT NULL DEFAULT false,
	createdby_id bigint,
	updatedby_id bigint,
	dbdatecreated timestamp without time zone,
	dbdateupdated timestamp without time zone
	);
ALTER TABLE association ADD CONSTRAINT association_createdby_id_fk FOREIGN KEY (createdby_id) REFERENCES person (id);
ALTER TABLE association ADD CONSTRAINT association_updatedby_id_fk FOREIGN KEY (updatedby_id) REFERENCES person (id);
CREATE INDEX association_createdby_index ON association  USING btree (createdby_id);
CREATE INDEX association_updatedby_index ON association  USING btree (updatedby_id);

CREATE TABLE association_aud (
	id bigint NOT NULL,
	rev integer NOT NULL,
	revtype smallint,
	PRIMARY KEY (id, rev)
	);
ALTER TABLE association_aud ADD CONSTRAINT annotation_aud_rev_fk FOREIGN KEY (rev) REFERENCES revinfo (rev);

ALTER TABLE association_informationcontententity DROP CONSTRAINT association_informationcontententity_association_id_fk;
ALTER TABLE association_informationcontententity ADD CONSTRAINT association_informationcontententity_association_id_fk FOREIGN KEY (association_id) REFERENCES association (id);

ALTER TABLE diseaseannotation RENAME CONSTRAINT fk5a3i0leqdmstsdfpq1j1b15el TO diseaseannotation_id_fk;
ALTER TABLE diseaseannotation RENAME CONSTRAINT fk9xjbpkdh1ffv0568wb8op838n TO diseaseannotation_annotationtype_id_fk;
ALTER TABLE diseaseannotation RENAME CONSTRAINT fkgtlivor9244ndobm9w5c0lxff TO diseaseannotation_diseaserelation_id_fk;
ALTER TABLE diseaseannotation RENAME CONSTRAINT fklns1tn5kseoys0xvq0f26h2vl TO diseaseannotation_geneticsex_id_fk;
ALTER TABLE diseaseannotation RENAME CONSTRAINT fknagkqf0yu1qib0wkp8s0n60hn TO diseaseannotation_diseasegeneticmodifierrelation_id_fk;
ALTER TABLE diseaseannotation RENAME CONSTRAINT fkp8u0w7kiirnjfcmdl4oyjhcs3 TO diseaseannotation_object_curie_fk;
ALTER TABLE diseaseannotation RENAME CONSTRAINT uk_2ea912q3hgfs30y1wo2c868wx TO diseaseannotation_modentityid_key;
ALTER TABLE diseaseannotation DROP CONSTRAINT uk_hvn7cmiviowf91epvfp1r67vy;
ALTER TABLE diseaseannotation DROP CONSTRAINT uk_hlsp8ic6sxwpd99k8gc90eq4a;
ALTER TABLE diseaseannotation DROP CONSTRAINT diseaseannotation_dataprovider_id_fk;
ALTER TABLE diseaseannotation DROP CONSTRAINT diseaseannotation_singlereference_curie_fk;

ALTER TABLE diseaseannotation_note RENAME TO annotation_note;
ALTER TABLE annotation_note RENAME COLUMN diseaseannotation_id TO annotation_id;
ALTER TABLE annotation_note RENAME CONSTRAINT fk52oyqivgshum07elq4opyfr5o TO annotation_note_relatednotes_id_fk;
ALTER TABLE annotation_note RENAME CONSTRAINT uk_f3o1apeoj9un48jw6qmiihpjc TO annotation_note_relatednotes_id_key;
ALTER TABLE annotation_note DROP CONSTRAINT fkeb3hi0d63vs83vg7lbqavs6pu;
ALTER INDEX idx2nxnuty631qew681vvjn4wd75 RENAME TO annotation_note_annotation_id_index;
ALTER INDEX idx42skj23oouce6tf5x57pxycba RENAME TO annotation_note_relatednotes_id_index;
ALTER TABLE annotation_note ADD CONSTRAINT annotation_note_annotation_id_fk FOREIGN KEY (annotation_id) REFERENCES annotation(id);

ALTER TABLE diseaseannotation_note_aud RENAME TO annotation_note_aud;
ALTER TABLE annotation_note_aud RENAME COLUMN diseaseannotation_id to annotation_id;
ALTER TABLE annotation_note_aud RENAME CONSTRAINT diseaseannotation_note_aud_pkey TO annotation_note_aud_pkey;
ALTER TABLE annotation_note_aud RENAME CONSTRAINT fka8x8613e1vmoaxljdrvhftj7a TO annotation_note_aud_rev_fk;

ALTER TABLE diseaseannotation_conditionrelation RENAME TO annotation_conditionrelation;
ALTER TABLE annotation_conditionrelation RENAME COLUMN diseaseannotation_id TO annotation_id;
ALTER TABLE annotation_conditionrelation DROP CONSTRAINT fk2wrhxll4ol0fxdpynbmue5stm;
ALTER TABLE annotation_conditionrelation RENAME CONSTRAINT fklvpswihwk7vf7ijnyustt47na TO annotation_conditionrelation_conditionrelations_id_fk;
ALTER TABLE annotation_conditionrelation ADD CONSTRAINT annotation_conditionrelation_annotation_id_fk FOREIGN KEY (annotation_id) REFERENCES annotation(id);
ALTER INDEX idxhnh1h715wjhoulhu2k77ibdfs RENAME TO annotation_conditionrelation_annotation_id_index;
ALTER INDEX idxtrk7q1phvahyem27wlkmiyw8w RENAME TO annotation_conditionrelation_conditionrelations_id_index;

ALTER TABLE diseaseannotation_conditionrelation_aud RENAME TO annotation_conditionrelation_aud;
ALTER TABLE annotation_conditionrelation_aud RENAME COLUMN diseaseannotation_id TO annotation_id;
ALTER TABLE annotation_conditionrelation_aud RENAME CONSTRAINT diseaseannotation_conditionrelation_aud_pkey TO annotation_conditionrelation_aud_pkey;
ALTER TABLE annotation_conditionrelation_aud RENAME CONSTRAINT fk2i4y7kdvdurhtxhq4qb8ijy0c TO annotation_conditionrelation_aud_rev_fk;

ALTER TABLE annotation
	ADD COLUMN curie varchar,
	ADD COLUMN modentityid varchar,
	ADD COLUMN modinternalid varchar,
	ADD COLUMN uniqueid varchar (2000),
	ADD COLUMN dataprovider_id bigint,
	ADD COLUMN singlereference_curie varchar;

UPDATE annotation SET curie = diseaseannotation.curie FROM diseaseannotation WHERE annotation.id = diseaseannotation.id;
UPDATE annotation SET modentityid = diseaseannotation.modentityid FROM diseaseannotation WHERE annotation.id = diseaseannotation.id;
UPDATE annotation SET modinternalid = diseaseannotation.modinternalid FROM diseaseannotation WHERE annotation.id = diseaseannotation.id;
UPDATE annotation SET uniqueid = diseaseannotation.uniqueid FROM diseaseannotation WHERE annotation.id = diseaseannotation.id;
UPDATE annotation SET dataprovider_id = diseaseannotation.dataprovider_id FROM diseaseannotation WHERE annotation.id = diseaseannotation.id;
UPDATE annotation SET singlereference_curie = diseaseannotation.singlereference_curie FROM diseaseannotation WHERE annotation.id = diseaseannotation.id;
DELETE FROM annotation WHERE dataprovider_id IS NULL;

ALTER TABLE diseaseannotation
	DROP COLUMN curie,
	DROP COLUMN modentityid,
	DROP COLUMN modinternalid,
	DROP COLUMN uniqueid,
	DROP COLUMN dataprovider_id,
	DROP COLUMN singlereference_curie;

ALTER TABLE annotation_aud
	ADD COLUMN curie varchar,
	ADD COLUMN modentityid varchar,
	ADD COLUMN modinternalid varchar,
	ADD COLUMN uniqueid varchar (2000),
	ADD COLUMN dataprovider_id bigint,
	ADD COLUMN singlereference_curie varchar;
	
UPDATE annotation_aud SET curie = diseaseannotation_aud.curie FROM diseaseannotation_aud WHERE annotation_aud.id = diseaseannotation_aud.id AND annotation_aud.rev = diseaseannotation_aud.rev;
UPDATE annotation_aud SET modentityid = diseaseannotation_aud.modentityid FROM diseaseannotation_aud WHERE annotation_aud.id = diseaseannotation_aud.id AND annotation_aud.rev = diseaseannotation_aud.rev;
UPDATE annotation_aud SET modinternalid = diseaseannotation_aud.modinternalid FROM diseaseannotation_aud WHERE  annotation_aud.id = diseaseannotation_aud.id AND annotation_aud.rev = diseaseannotation_aud.rev;
UPDATE annotation_aud SET uniqueid = diseaseannotation_aud.uniqueid FROM diseaseannotation_aud WHERE  annotation_aud.id = diseaseannotation_aud.id AND annotation_aud.rev = diseaseannotation_aud.rev;
UPDATE annotation_aud SET dataprovider_id = diseaseannotation_aud.dataprovider_id FROM diseaseannotation_aud WHERE  annotation_aud.id = diseaseannotation_aud.id AND annotation_aud.rev = diseaseannotation_aud.rev;
UPDATE annotation_aud SET singlereference_curie = diseaseannotation_aud.singlereference_curie FROM diseaseannotation_aud WHERE  annotation_aud.id = diseaseannotation_aud.id AND annotation_aud.rev = diseaseannotation_aud.rev;

ALTER TABLE diseaseannotation_aud
	DROP COLUMN curie,
	DROP COLUMN modentityid,
	DROP COLUMN modinternalid,
	DROP COLUMN uniqueid,
	DROP COLUMN dataprovider_id,
	DROP COLUMN singlereference_curie;
