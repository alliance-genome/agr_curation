-- Create AlleleMutationTypeSlotAnnotation tables
CREATE TABLE allelemutationtypeslotannotation (
	id bigint CONSTRAINT allelemutationtypeslotannotation_pkey PRIMARY KEY,
	singleallele_curie varchar(255) NOT NULL
	);

ALTER TABLE allelemutationtypeslotannotation
	ADD CONSTRAINT allelemutationtypeslotannotationtype_id_fk
		FOREIGN KEY (id) REFERENCES slotannotation (id);
		
ALTER TABLE allelemutationtypeslotannotation
	ADD CONSTRAINT allelemutationtypeslotannotationtype_singleallele_curie_fk
		FOREIGN KEY (singleallele_curie) REFERENCES allele (curie);	

CREATE TABLE allelemutationtypeslotannotation_aud (
	id bigint,
	singleallele_curie varchar(255) NOT NULL,
	rev integer NOT NULL,
	PRIMARY KEY (id, rev)
);

ALTER TABLE allelemutationtypeslotannotation_aud
	ADD CONSTRAINT allelemutationtypeslotannotation_aud_id_rev_fk
		FOREIGN KEY (id, rev) REFERENCES slotannotation_aud;

CREATE TABLE allelemutationtypeslotannotation_soterm (
	allelemutationtypeslotannotation_id bigint NOT NULL,
	mutationtypes_curie varchar(255) NOT NULL
);

ALTER TABLE allelemutationtypeslotannotation_soterm
	ADD CONSTRAINT allelemutationtypeslotannotation_aud_amsa_id_fk
		FOREIGN KEY (allelemutationtypeslotannotation_id) REFERENCES allelemutationtypeslotannotation (id);
		
ALTER TABLE allelemutationtypeslotannotation_soterm
	ADD CONSTRAINT allelemutationtypeslotannotation_aud_mutationtypes_curie_fk
		FOREIGN KEY (mutationtypes_curie) REFERENCES soterm (curie);
		
CREATE TABLE allelemutationtypeslotannotation_soterm_aud (
	allelemutationtypeslotannotation_id bigint NOT NULL,
	mutationtypes_curie varchar(255) NOT NULL,
	rev integer NOT NULL,
	revtype smallint,
	PRIMARY KEY (rev, allelemutationtypeslotannotation_id, mutationtypes_curie)
);

ALTER TABLE allelemutationtypeslotannotation_soterm_aud
	ADD CONSTRAINT allelemutationtypeslotannotation_aud_rev_fk
		FOREIGN KEY (rev) REFERENCES revinfo (rev);