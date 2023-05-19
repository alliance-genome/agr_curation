ALTER TABLE allelesecondaryidslotannotation RENAME CONSTRAINT allelesecondaryidslotannotationtype_id_fk TO secondaryidslotannotation_id_fk;

ALTER TABLE allelesecondaryidslotannotation DROP CONSTRAINT allelesecondaryidslotannotationtype_singleallele_curie_fk;

DROP INDEX allelesecondary_id_singleallele_curie_index;

ALTER TABLE allelesecondaryidslotannotation RENAME TO secondaryidslotannotation;

CREATE TABLE allelesecondaryidslotannotation AS SELECT id, singleallele_curie FROM secondaryidslotannotation;

ALTER TABLE allelesecondaryidslotannotation ADD PRIMARY KEY (id);

ALTER TABLE allelesecondaryidslotannotation ADD CONSTRAINT allelesecondaryidslotannotation_singleallele_curie_fk FOREIGN KEY (singleallele_curie) REFERENCES allele (curie);

CREATE INDEX allelesecondaryid_singleallele_curie_index ON allelesecondaryidslotannotation USING btree (singleallele_curie);

ALTER TABLE secondaryidslotannotation DROP COLUMN singleallele_curie;

ALTER TABLE allelesecondaryidslotannotation_aud RENAME CONSTRAINT allelesecondaryidslotannotation_aud_id_rev_fk TO secondaryidslotannotation_aud_id_rev_fk;

ALTER TABLE allelesecondaryidslotannotation_aud RENAME TO secondaryidslotannotation_aud;

CREATE TABLE allelesecondaryidslotannotation_aud AS SELECT id, rev, singleallele_curie FROM secondaryidslotannotation_aud;

ALTER TABLE allelesecondaryidslotannotation_aud ADD PRIMARY KEY (id, rev);

ALTER TABLE allelesecondaryidslotannotation_aud ADD CONSTRAINT allelesecondaryidslotannotation_aud_id_rev_fk FOREIGN KEY (id, rev) REFERENCES secondaryidslotannotation_aud (id, rev);

ALTER TABLE secondaryidslotannotation_aud DROP COLUMN singleallele_curie;

CREATE TABLE genesecondaryidslotannotation (
	id bigint PRIMARY KEY,
	singlegene_curie varchar(255)
);

CREATE INDEX genesecondaryid_singlegene_curie_index ON genesecondaryidslotannotation USING btree (singlegene_curie);

CREATE TABLE genesecondaryidslotannotation_aud (
    id bigint NOT NULL,
    rev integer NOT NULL,
    singlegene_curie varchar(255)
);

ALTER TABLE genesecondaryidslotannotation_aud ADD PRIMARY KEY (id, rev);

ALTER TABLE genesecondaryidslotannotation_aud ADD CONSTRAINT genesecondaryidslotannotation_aud_id_rev_fk FOREIGN KEY (id, rev) REFERENCES secondaryidslotannotation_aud (id, rev);