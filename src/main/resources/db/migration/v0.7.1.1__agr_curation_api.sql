ALTER TABLE allelediseaseannotation
	ADD COLUMN assertedgene_curie varchar (255);
	
ALTER TABLE allelediseaseannotation_aud
	ADD COLUMN assertedgene_curie varchar (255);
	
ALTER TABLE allelediseaseannotation
	ADD CONSTRAINT allelediseaseannotation_assertedgene_curie_fk
	FOREIGN KEY (assertedgene_curie) REFERENCES gene(curie);
	
ALTER TABLE agmdiseaseannotation
	ADD COLUMN assertedallele_curie varchar (255),
	ADD COLUMN assertedgene_curie varchar (255);
	
ALTER TABLE agmdiseaseannotation_aud
	ADD COLUMN assertedallele_curie varchar (255),
	ADD COLUMN assertedgene_curie varchar (255);
		
ALTER TABLE agmdiseaseannotation
	ADD CONSTRAINT agmdiseaseannotation_assertedallele_curie_fk
	FOREIGN KEY (assertedallele_curie) REFERENCES allele(curie);
		
ALTER TABLE agmdiseaseannotation
	ADD CONSTRAINT agmdiseaseannotation_assertedgene_curie_fk
	FOREIGN KEY (assertedgene_curie) REFERENCES gene(curie);