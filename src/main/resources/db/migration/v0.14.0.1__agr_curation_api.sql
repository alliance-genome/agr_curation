CREATE TABLE allelesecondaryidslotannotation (
	id bigint CONSTRAINT allelesecondaryidslotannotation_pkey PRIMARY KEY,
	singleallele_curie varchar(255),
	secondaryid varchar(255)
	);
	
ALTER TABLE allelesecondaryidslotannotation
	ADD CONSTRAINT allelesecondaryidslotannotationtype_id_fk
		FOREIGN KEY (id) REFERENCES slotannotation (id);
		
ALTER TABLE allelesecondaryidslotannotation
	ADD CONSTRAINT allelesecondaryidslotannotationtype_singleallele_curie_fk
		FOREIGN KEY (singleallele_curie) REFERENCES allele (curie);	

CREATE TABLE allelesecondaryidslotannotation_aud (
	id bigint,
	rev integer NOT NULL,
	singleallele_curie varchar(255),
	secondaryid varchar(255),
	PRIMARY KEY (id, rev)
);

ALTER TABLE allelesecondaryidslotannotation_aud
	ADD CONSTRAINT allelesecondaryidslotannotation_aud_id_rev_fk
		FOREIGN KEY (id, rev) REFERENCES slotannotation_aud;

CREATE INDEX allelesecondary_id_singleallele_curie_index ON public.allelesecondaryidslotannotation USING btree (singleallele_curie);