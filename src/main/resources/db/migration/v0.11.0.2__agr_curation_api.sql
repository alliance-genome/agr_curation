CREATE TABLE agmdiseaseannotation_gene (
	agmdiseaseannotation_id bigint,
	assertedgenes_curie varchar (255)
	);

CREATE TABLE allelediseaseannotation_gene (
	allelediseaseannotation_id bigint,
	assertedgenes_curie varchar (255)
	);	
	
CREATE TABLE agmdiseaseannotation_gene_aud (
	rev integer,
	agmdiseaseannotation_id bigint,
	assertedgenes_curie varchar (255),
	revtype smallint
	);
	
CREATE TABLE allelediseaseannotation_gene_aud (
	rev integer,
	allelediseaseannotation_id bigint,
	assertedgenes_curie varchar (255),
	revtype smallint
	);
		
ALTER TABLE agmdiseaseannotation_gene
	ADD CONSTRAINT agmdiseaseannotation_gene_assertedgenes_curie_fk
	FOREIGN KEY (assertedgenes_curie) REFERENCES gene(curie);

ALTER TABLE agmdiseaseannotation_gene
	ADD CONSTRAINT agmdiseaseannotation_gene_agmdiseaseannotation_id_fk
	FOREIGN KEY (agmdiseaseannotation_id) REFERENCES agmdiseaseannotation(id);
		
ALTER TABLE allelediseaseannotation_gene
	ADD CONSTRAINT allelediseaseannotation_gene_assertedgenes_curie_fk
	FOREIGN KEY (assertedgenes_curie) REFERENCES gene(curie);

ALTER TABLE allelediseaseannotation_gene
	ADD CONSTRAINT allelediseaseannotation_gene_allelediseaseannotation_id_fk
	FOREIGN KEY (allelediseaseannotation_id) REFERENCES allelediseaseannotation(id);
	
ALTER TABLE agmdiseaseannotation
	DROP CONSTRAINT agmdiseaseannotation_assertedgene_curie_fk;
	
ALTER TABLE allelediseaseannotation
	DROP CONSTRAINT allelediseaseannotation_assertedgene_curie_fk;
	
ALTER TABLE diseaseannotation_gene_aud
	ADD assertedgene_curie character varying(255);
	
INSERT INTO agmdiseaseannotation_gene (agmdiseaseannotation_id, assertedgenes_curie)
	SELECT id, assertedgene_curie FROM agmdiseaseannotation WHERE assertedgene_curie IS NOT NULL;
	
INSERT INTO allelediseaseannotation_gene (allelediseaseannotation_id, assertedgenes_curie)
	SELECT id, assertedgene_curie FROM allelediseaseannotation WHERE assertedgene_curie IS NOT NULL;
	
ALTER TABLE agmdiseaseannotation
	DROP COLUMN assertedgene_curie;
	
ALTER TABLE allelediseaseannotation
	DROP COLUMN assertedgene_curie;
