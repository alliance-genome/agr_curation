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


-- Added the following for the PersonSettings SCRUM-2098

CREATE TABLE public.personsetting (
		id bigint NOT NULL,
		datecreated timestamp without time zone,
		dateupdated timestamp without time zone,
		dbdatecreated timestamp without time zone,
		dbdateupdated timestamp without time zone,
		internal boolean DEFAULT false NOT NULL,
		obsolete boolean DEFAULT false NOT NULL,
		settingskey character varying(255),
		settingsmap jsonb,
		createdby_id bigint,
		updatedby_id bigint,
		person_id bigint
);

CREATE TABLE public.personsetting_aud (
		id bigint NOT NULL,
		rev integer NOT NULL,
		revtype smallint,
		settingskey character varying(255),
		settingsmap jsonb,
		person_id bigint
);

ALTER TABLE ONLY public.personsetting_aud
		ADD CONSTRAINT personsetting_aud_pkey PRIMARY KEY (id, rev);

ALTER TABLE ONLY public.personsetting
		ADD CONSTRAINT personsetting_pkey PRIMARY KEY (id);

ALTER TABLE ONLY public.personsetting
		ADD CONSTRAINT fke1vgebqeld0wd3sf6cunn80m9 FOREIGN KEY (createdby_id) REFERENCES public.person(id);

ALTER TABLE ONLY public.personsetting
		ADD CONSTRAINT fkgguiepii4pd00a8wefwds75wf FOREIGN KEY (updatedby_id) REFERENCES public.person(id);

ALTER TABLE ONLY public.personsetting
		ADD CONSTRAINT fkhi5m73qq4jmabfhrhmhlhxr4h FOREIGN KEY (person_id) REFERENCES public.person(id);

ALTER TABLE ONLY public.personsetting_aud
		ADD CONSTRAINT fkq0ul02mp3963koe1p6sntqc8e FOREIGN KEY (rev) REFERENCES public.revinfo(rev);

