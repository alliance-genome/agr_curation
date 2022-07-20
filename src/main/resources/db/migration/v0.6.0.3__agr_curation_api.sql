DROP TABLE reference_secondarycrossreferences;

DROP TABLE reference_secondarycrossreferences_aud;

ALTER TABLE diseaseannotation
	DROP CONSTRAINT diseaseannotation_singlereference_submittedcrossreference_fk;

UPDATE diseaseannotation da
	SET singlereference_submittedcrossreference = (
		SELECT min(submittedcrossreference) FROM reference where curie = (
			SELECT curie from reference where submittedcrossreference = da.singlereference_submittedcrossreference
		)
	) WHERE singlereference_submittedcrossreference = da.singlereference_submittedcrossreference;

UPDATE diseaseannotation da
	SET singlereference_submittedcrossreference = (
		SELECT curie FROM reference WHERE submittedcrossreference = da.singlereference_submittedcrossreference 
	);

ALTER TABLE diseaseannotation
	RENAME singlereference_submittedcrossreference TO singlereference_curie;

ALTER TABLE diseaseannotation_aud
	RENAME singlereference_submittedcrossreference TO singlereference_curie;

ALTER TABLE conditionrelation
	DROP CONSTRAINT conditionrelation_singlereference_submittedcrossreference_fk;

UPDATE conditionrelation cr
	SET singlereference_submittedcrossreference = (
		SELECT min(submittedcrossreference) FROM reference where curie = (
			SELECT curie from reference where submittedcrossreference = cr.singlereference_submittedcrossreference
		)
	) WHERE singlereference_submittedcrossreference = cr.singlereference_submittedcrossreference;

UPDATE conditionrelation cr
	SET singlereference_submittedcrossreference = (
		SELECT curie FROM reference WHERE submittedcrossreference = cr.singlereference_submittedcrossreference 
	);

ALTER TABLE conditionrelation
	RENAME singlereference_submittedcrossreference TO singlereference_curie;

ALTER TABLE conditionrelation_aud
	RENAME singlereference_submittedcrossreference TO singlereference_curie;


UPDATE note_reference nr
	SET references_submittedcrossreference = (
		SELECT min(submittedcrossreference) FROM reference where curie = (
			SELECT curie from reference where submittedcrossreference = nr.references_submittedcrossreference
		)
	) WHERE references_submittedcrossreference = nr.references_submittedcrossreference;

UPDATE note_reference nr
	SET references_submittedcrossreference = (
		SELECT curie FROM reference WHERE submittedcrossreference = nr.references_submittedcrossreference
	);

ALTER TABLE note_reference
	RENAME references_submittedcrossreference TO references_curie;

ALTER TABLE note_reference_aud
	RENAME references_submittedcrossreference TO references_curie;

ALTER TABLE paperhandle
	DROP CONSTRAINT paperhandle_reference_submittedcrossreference_fk;

UPDATE paperhandle ph
	SET reference_submittedcrossreference = (
		SELECT min(submittedcrossreference) FROM reference where curie = (
			SELECT curie from reference where submittedcrossreference = ph.reference_submittedcrossreference
		)
	) WHERE reference_submittedcrossreference = ph.reference_submittedcrossreference;

UPDATE paperhandle ph
	SET reference_submittedcrossreference = (
		SELECT curie FROM reference WHERE submittedcrossreference = ph.reference_submittedcrossreference
	);

ALTER TABLE paperhandle
	RENAME reference_submittedcrossreference TO reference_curie;

ALTER TABLE paperhandle_aud
	RENAME reference_submittedcrossreference TO reference_curie;

DELETE FROM reference r
	WHERE submittedcrossreference != (
		SELECT min(submittedcrossreference) FROM reference where curie = r.curie
	); 

ALTER TABLE reference
	DROP CONSTRAINT reference_pkey;

ALTER TABLE reference
	ADD PRIMARY KEY (curie);

ALTER TABLE reference
	DROP COLUMN primarycrossreference,
	DROP COLUMN submittedcrossreference;

CREATE TABLE reference_crossreference (
	reference_curie varchar (255),
	crossreferences_curie varchar (255)
);

ALTER TABLE reference_crossreference
	ADD CONSTRAINT reference_crossreference_reference_curie_fk
	FOREIGN KEY (reference_curie) REFERENCES reference(curie);

ALTER TABLE reference_crossreference
	ADD CONSTRAINT reference_crossreference_crossreferences_curie_fk
	FOREIGN KEY (crossreferences_curie) REFERENCES crossreference(curie);

CREATE TABLE reference_crossreference_aud (
	rev integer,
	reference_curie varchar (255),
	crossreferences_curie varchar (255),
	revtype smallint
);

ALTER TABLE ONLY public.reference_crossreference_aud
    ADD CONSTRAINT fkbe9p60b4cghc6anvlj618ec3d FOREIGN KEY (rev) REFERENCES public.revinfo(rev);

ALTER TABLE diseaseannotation
	ADD CONSTRAINT diseaseannotation_singlereference_curie_fk
	FOREIGN KEY (singlereference_curie) REFERENCES reference(curie);

ALTER TABLE conditionrelation
	ADD CONSTRAINT conditionrelation_singlereference_curie_fk
	FOREIGN KEY (singlereference_curie) REFERENCES reference(curie);

ALTER TABLE paperhandle
	ADD CONSTRAINT paperhandle_reference_curie_fk
	FOREIGN KEY (reference_curie) REFERENCES reference(curie);
	
ALTER TABLE note_reference
	ADD CONSTRAINT note_reference_references_curie_fk
	FOREIGN KEY (references_curie) REFERENCES reference(curie);
	
ALTER TABLE ONLY public.diseaseannotation
    ADD CONSTRAINT uk_2ea912q3hgfs30y1wo2c868wx UNIQUE (modentityid);


-- Added DB auto updated fields

ALTER TABLE public.association
   ADD COLUMN IF NOT EXISTS dbdatecreated timestamp without time zone,
   ADD COLUMN IF NOT EXISTS dbdateupdated timestamp without time zone;
ALTER TABLE public.biologicalentity
   ADD COLUMN IF NOT EXISTS dbdatecreated timestamp without time zone,
   ADD COLUMN IF NOT EXISTS dbdateupdated timestamp without time zone;
ALTER TABLE public.bulkload
   ADD COLUMN IF NOT EXISTS dbdatecreated timestamp without time zone,
   ADD COLUMN IF NOT EXISTS dbdateupdated timestamp without time zone;
ALTER TABLE public.bulkloadfile
   ADD COLUMN IF NOT EXISTS dbdatecreated timestamp without time zone,
   ADD COLUMN IF NOT EXISTS dbdateupdated timestamp without time zone;
ALTER TABLE public.bulkloadfileexception
   ADD COLUMN IF NOT EXISTS dbdatecreated timestamp without time zone,
   ADD COLUMN IF NOT EXISTS dbdateupdated timestamp without time zone;
ALTER TABLE public.bulkloadfilehistory
   ADD COLUMN IF NOT EXISTS dbdatecreated timestamp without time zone,
   ADD COLUMN IF NOT EXISTS dbdateupdated timestamp without time zone;
ALTER TABLE public.bulkloadgroup
   ADD COLUMN IF NOT EXISTS dbdatecreated timestamp without time zone,
   ADD COLUMN IF NOT EXISTS dbdateupdated timestamp without time zone;
ALTER TABLE public.conditionrelation
   ADD COLUMN IF NOT EXISTS dbdatecreated timestamp without time zone,
   ADD COLUMN IF NOT EXISTS dbdateupdated timestamp without time zone;
ALTER TABLE public.crossreference
   ADD COLUMN IF NOT EXISTS dbdatecreated timestamp without time zone,
   ADD COLUMN IF NOT EXISTS dbdateupdated timestamp without time zone;
ALTER TABLE public.curationreport
   ADD COLUMN IF NOT EXISTS dbdatecreated timestamp without time zone,
   ADD COLUMN IF NOT EXISTS dbdateupdated timestamp without time zone;
ALTER TABLE public.curationreportgroup
   ADD COLUMN IF NOT EXISTS dbdatecreated timestamp without time zone,
   ADD COLUMN IF NOT EXISTS dbdateupdated timestamp without time zone;
ALTER TABLE public.curationreporthistory
   ADD COLUMN IF NOT EXISTS dbdatecreated timestamp without time zone,
   ADD COLUMN IF NOT EXISTS dbdateupdated timestamp without time zone;
ALTER TABLE public.experimentalcondition
   ADD COLUMN IF NOT EXISTS dbdatecreated timestamp without time zone,
   ADD COLUMN IF NOT EXISTS dbdateupdated timestamp without time zone;
ALTER TABLE public.genegenomiclocation
   ADD COLUMN IF NOT EXISTS dbdatecreated timestamp without time zone,
   ADD COLUMN IF NOT EXISTS dbdateupdated timestamp without time zone;
ALTER TABLE public.note
   ADD COLUMN IF NOT EXISTS dbdatecreated timestamp without time zone,
   ADD COLUMN IF NOT EXISTS dbdateupdated timestamp without time zone;
ALTER TABLE public.ontologyterm
   ADD COLUMN IF NOT EXISTS dbdatecreated timestamp without time zone,
   ADD COLUMN IF NOT EXISTS dbdateupdated timestamp without time zone;
ALTER TABLE public.person
   ADD COLUMN IF NOT EXISTS dbdatecreated timestamp without time zone,
   ADD COLUMN IF NOT EXISTS dbdateupdated timestamp without time zone;
ALTER TABLE public.reference
   ADD COLUMN IF NOT EXISTS dbdatecreated timestamp without time zone,
   ADD COLUMN IF NOT EXISTS dbdateupdated timestamp without time zone;
ALTER TABLE public.synonym
   ADD COLUMN IF NOT EXISTS dbdatecreated timestamp without time zone,
   ADD COLUMN IF NOT EXISTS dbdateupdated timestamp without time zone;
ALTER TABLE public.vocabularyterm
   ADD COLUMN IF NOT EXISTS dbdatecreated timestamp without time zone,
   ADD COLUMN IF NOT EXISTS dbdateupdated timestamp without time zone;
ALTER TABLE public.vocabulary
   ADD COLUMN IF NOT EXISTS dbdatecreated timestamp without time zone,
   ADD COLUMN IF NOT EXISTS dbdateupdated timestamp without time zone;
