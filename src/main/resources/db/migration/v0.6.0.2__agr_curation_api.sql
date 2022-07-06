ALTER TABLE association
	DROP CONSTRAINT IF EXISTS "fk4hugd4syivmg65wvbnlajtbqp";
	
ALTER TABLE association
	RENAME modifiedby_id TO updatedby_id;
	
ALTER TABLE association
	ADD CONSTRAINT association_updatedby_id_fk
	FOREIGN KEY (updatedby_id) REFERENCES person(id);

ALTER TABLE biologicalentity
	DROP CONSTRAINT IF EXISTS "fk36gq0macvyrb6objapp3o8bwp";
	
ALTER TABLE biologicalentity
	RENAME modifiedby_id TO updatedby_id;
	
ALTER TABLE biologicalentity
	ADD CONSTRAINT biologicalentity_updatedby_id_fk
	FOREIGN KEY (updatedby_id) REFERENCES person(id);

ALTER TABLE bulkload
	DROP CONSTRAINT IF EXISTS "fkoyli2i7necktxvvqeamcisk8d";
	
ALTER TABLE bulkload
	RENAME modifiedby_id TO updatedby_id;
	
ALTER TABLE bulkload
	ADD CONSTRAINT bulkload_updatedby_id_fk
	FOREIGN KEY (updatedby_id) REFERENCES person(id);
	
ALTER TABLE bulkloadfile
	DROP CONSTRAINT IF EXISTS "fk8x22rg3nrv53h5cx2cr1ktpik";
	
ALTER TABLE bulkloadfile
	RENAME modifiedby_id TO updatedby_id;
	
ALTER TABLE bulkloadfile
	ADD CONSTRAINT bulkloadfile_updatedby_id_fk
	FOREIGN KEY (updatedby_id) REFERENCES person(id);
	
ALTER TABLE bulkloadfileexception
	DROP CONSTRAINT IF EXISTS "fkkixcrsm0avf27cqtxdj9hhm9v";
	
ALTER TABLE bulkloadfileexception
	RENAME modifiedby_id TO updatedby_id;
	
ALTER TABLE bulkloadfileexception
	ADD CONSTRAINT bulkloadfileexception_updatedby_id_fk
	FOREIGN KEY (updatedby_id) REFERENCES person(id);
	
ALTER TABLE bulkloadfilehistory
	DROP CONSTRAINT IF EXISTS "fkh69f048adbnwqj0w32wme943l";
	
ALTER TABLE bulkloadfilehistory
	RENAME modifiedby_id TO updatedby_id;
	
ALTER TABLE bulkloadfilehistory
	ADD CONSTRAINT bulkloadfilehistory_updatedby_id_fk
	FOREIGN KEY (updatedby_id) REFERENCES person(id);
	
ALTER TABLE bulkloadgroup
	DROP CONSTRAINT IF EXISTS "fk4j3eii20chv4644ww8abl3rai";
	
ALTER TABLE bulkloadgroup
	RENAME modifiedby_id TO updatedby_id;
	
ALTER TABLE bulkloadgroup
	ADD CONSTRAINT bulkloadgroup_updatedby_id_fk
	FOREIGN KEY (updatedby_id) REFERENCES person(id);
	
ALTER TABLE conditionrelation
	DROP CONSTRAINT IF EXISTS "fk76h2a6c78nwngxavbl9jhgk83";
	
ALTER TABLE conditionrelation
	RENAME modifiedby_id TO updatedby_id;
	
ALTER TABLE conditionrelation
	ADD CONSTRAINT conditionrelation_updatedby_id_fk
	FOREIGN KEY (updatedby_id) REFERENCES person(id);
	
ALTER TABLE crossreference
	DROP CONSTRAINT IF EXISTS "fk5x1tcwy014uoda88nbro7h443";
	
ALTER TABLE crossreference
	RENAME modifiedby_id TO updatedby_id;
	
ALTER TABLE crossreference
	ADD CONSTRAINT crossreference_updatedby_id_fk
	FOREIGN KEY (updatedby_id) REFERENCES person(id);
	
ALTER TABLE curationreport
	DROP CONSTRAINT IF EXISTS "fkns8e08gsl14gra1obg63jmm3m";
	
ALTER TABLE curationreport
	RENAME modifiedby_id TO updatedby_id;
	
ALTER TABLE curationreport
	ADD CONSTRAINT curationreport_updatedby_id_fk
	FOREIGN KEY (updatedby_id) REFERENCES person(id);
	
ALTER TABLE curationreportgroup
	DROP CONSTRAINT IF EXISTS "fklt1jeksri20mben7ku0qin4si";
	
ALTER TABLE curationreportgroup
	RENAME modifiedby_id TO updatedby_id;
	
ALTER TABLE curationreportgroup
	ADD CONSTRAINT curationreportgroup_updatedby_id_fk
	FOREIGN KEY (updatedby_id) REFERENCES person(id);
	
ALTER TABLE curationreporthistory
	DROP CONSTRAINT IF EXISTS "fk73td89gv1cchrqjdgu4fjq7ri";
	
ALTER TABLE curationreporthistory
	RENAME modifiedby_id TO updatedby_id;
	
ALTER TABLE curationreporthistory
	ADD CONSTRAINT curationreporthistory_updatedby_id_fk
	FOREIGN KEY (updatedby_id) REFERENCES person(id);
	
ALTER TABLE diseaseannotation
	DROP COLUMN IF EXISTS createdby,
	DROP COLUMN IF EXISTS modifiedby;
	
ALTER TABLE experimentalcondition
	DROP CONSTRAINT IF EXISTS "fk8wx5lbnkk1026m46lpa67bl5s";
	
ALTER TABLE experimentalcondition
	RENAME modifiedby_id TO updatedby_id;
	
ALTER TABLE experimentalcondition
	ADD CONSTRAINT experimentalcondition_updatedby_id_fk
	FOREIGN KEY (updatedby_id) REFERENCES person(id);
	
ALTER TABLE genegenomiclocation
	DROP CONSTRAINT IF EXISTS "fk7ub2juqlwvc30f4ptun1hf7oe";
	
ALTER TABLE genegenomiclocation
	RENAME modifiedby_id TO updatedby_id;
	
ALTER TABLE genegenomiclocation
	ADD CONSTRAINT genegenomiclocation_updatedby_id_fk
	FOREIGN KEY (updatedby_id) REFERENCES person(id);
	
ALTER TABLE note
	DROP CONSTRAINT IF EXISTS "fk3ru1c5ymn3vcqfb32p93ywimk";
	
ALTER TABLE note
	RENAME modifiedby_id TO updatedby_id;
	
ALTER TABLE note
	ADD CONSTRAINT note_updatedby_id_fk
	FOREIGN KEY (updatedby_id) REFERENCES person(id);
	
ALTER TABLE ontologyterm
	DROP CONSTRAINT IF EXISTS "fkovkusgncyac2o27pb4vp7ni0n";
	
ALTER TABLE ontologyterm
	RENAME modifiedby_id TO updatedby_id;
	
ALTER TABLE ontologyterm
	ADD CONSTRAINT ontologyterm_updatedby_id_fk
	FOREIGN KEY (updatedby_id) REFERENCES person(id);
	
ALTER TABLE person
	DROP CONSTRAINT IF EXISTS "fk6ujtrho00dn63yrdkw87wq8e2";
	
ALTER TABLE person
	RENAME modifiedby_id TO updatedby_id;
	
ALTER TABLE person
	ADD CONSTRAINT person_updatedby_id_fk
	FOREIGN KEY (updatedby_id) REFERENCES person(id);
	
ALTER TABLE reference
	DROP CONSTRAINT IF EXISTS "fkhgidt2wmi7xvxv7hpom0vq6gt";
	
ALTER TABLE reference
	RENAME modifiedby_id TO updatedby_id;
	
ALTER TABLE reference
	ADD CONSTRAINT reference_updatedby_id_fk
	FOREIGN KEY (updatedby_id) REFERENCES person(id);

ALTER TABLE synonym
	DROP CONSTRAINT IF EXISTS "fkf6mf8g3b7cy1our37ief0dgr6";
	
ALTER TABLE synonym
	RENAME modifiedby_id TO updatedby_id;
	
ALTER TABLE synonym
	ADD CONSTRAINT synonym_updatedby_id_fk
	FOREIGN KEY (updatedby_id) REFERENCES person(id);

ALTER TABLE vocabulary
	DROP CONSTRAINT IF EXISTS "fkf3bctkbi3fg4cy8nqpakt5ujl";
	
ALTER TABLE vocabulary
	RENAME modifiedby_id TO updatedby_id;
	
ALTER TABLE vocabulary
	ADD CONSTRAINT vocabulary_updatedby_id_fk
	FOREIGN KEY (updatedby_id) REFERENCES person(id);

ALTER TABLE vocabularyterm
	DROP CONSTRAINT IF EXISTS "fk7al3e88sx9urmj8exf5cfqvuk";
	
ALTER TABLE vocabularyterm
	RENAME modifiedby_id TO updatedby_id;
	
ALTER TABLE vocabularyterm
	ADD CONSTRAINT vocabularyterm_updatedby_id_fk
	FOREIGN KEY (updatedby_id) REFERENCES person(id);
