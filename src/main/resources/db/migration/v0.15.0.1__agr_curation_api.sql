CREATE TABLE alleleinheritancemodeslotannotation (
	id bigint CONSTRAINT alleleinheritancemodeslotannotation_pkey PRIMARY KEY,
	singleallele_curie varchar(255),
	inheritancemode_id bigint,
	phenotypeterm_curie varchar(255),
	phenotypestatement text
	);

ALTER TABLE alleleinheritancemodeslotannotation
	ADD CONSTRAINT alleleinheritancemodeslotannotation_inheritancemode_id_fk
		FOREIGN KEY (inheritancemode_id) REFERENCES vocabularyterm (id);
		
ALTER TABLE alleleinheritancemodeslotannotation
	ADD CONSTRAINT alleleinheritancemodeslotannotation_phenotypeterm_curie_fk
		FOREIGN KEY (phenotypeterm_curie) REFERENCES phenotypeterm (curie);	

CREATE TABLE alleleinheritancemodeslotannotation_aud (
	id bigint,
	singleallele_curie varchar(255),
	inheritancemode_id bigint,
	phenotypeterm_curie varchar(255),
	phenotypestatement text,
	rev integer NOT NULL,
	PRIMARY KEY (id, rev)
);

ALTER TABLE alleleinheritancemodeslotannotation_aud
	ADD CONSTRAINT alleleinheritancemodeslotannotation_aud_id_rev_fk
		FOREIGN KEY (id, rev) REFERENCES slotannotation_aud;

CREATE INDEX alleleinheritancemode_singleallele_curie_index ON public.alleleinheritancemodeslotannotation USING btree (singleallele_curie);
CREATE INDEX alleleinheritancemode_inheritancemode_id_index ON public.alleleinheritancemodeslotannotation USING btree (inheritancemode_id);
CREATE INDEX alleleinheritancemode_phenotypeterm_curie_index ON public.alleleinheritancemodeslotannotation USING btree (phenotypeterm_curie);

INSERT INTO alleleinheritancemodeslotannotation (id, singleallele_curie, inheritancemode_id)
	SELECT nextval('hibernate_sequence'), curie, inheritancemode_id FROM allele WHERE inheritancemode_id IS NOT NULL;
	
INSERT INTO slotannotation (id)
	SELECT id FROM alleleinheritancemodeslotannotation;
	
DROP INDEX allele_inheritancemode_index;
	
ALTER TABLE allele
	DROP COLUMN inheritancemode_id;
	
ALTER TABLE allele_aud
	DROP COLUMN inheritancemode_id;
	
ALTER TABLE alleleinheritancemodeslotannotation
	ADD CONSTRAINT alleleinheritancemodeslotannotation_id_fk
		FOREIGN KEY (id) REFERENCES slotannotation (id);