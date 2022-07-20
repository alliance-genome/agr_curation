CREATE TABLE IF NOT EXISTS loggedinperson (
	id bigint PRIMARY KEY,
	oktaemail varchar(255),
	oktaid varchar(255),
	apitoken varchar(255),
usersettings jsonb
	);
	
INSERT INTO loggedinperson (id)
	SELECT id FROM person WHERE id IS NOT NULL;
	
UPDATE loggedinperson
	SET oktaemail = person.email
	FROM person
	WHERE loggedinperson.id = person.id;
	
UPDATE loggedinperson
	SET apitoken = person.apitoken
	FROM person
	WHERE loggedinperson.id = person.id;
	
UPDATE person 
	SET uniqueid = CONCAT(person.firstname, '|', person.lastname, '|', oktaemail)
	FROM loggedinperson
	WHERE loggedinperson.id = person.id;
	
ALTER TABLE person
	ADD COLUMN IF NOT EXISTS modentityid varchar(255);
	
UPDATE person
	SET modentityid = modid;
	
ALTER TABLE person
	DROP COLUMN IF EXISTS email,
	DROP COLUMN IF EXISTS apitoken,
	DROP COLUMN IF EXISTS modid;
	
ALTER TABLE association
	DROP COLUMN IF EXISTS created,
	DROP COLUMN IF EXISTS lastupdated;

ALTER TABLE biologicalentity
	DROP COLUMN IF EXISTS created,
	DROP COLUMN IF EXISTS lastupdated;
	
ALTER TABLE bulkload
	ADD COLUMN IF NOT EXISTS datecreated timestamp without time zone,
	ADD COLUMN IF NOT EXISTS dateupdated timestamp without time zone;
	
UPDATE bulkload
	SET datecreated = created;
	
UPDATE bulkload
	SET dateupdated = lastupdated;
	
ALTER TABLE bulkload
	DROP COLUMN IF EXISTS created,
	DROP COLUMN IF EXISTS lastupdated;
	
ALTER TABLE bulkloadfile
	ADD COLUMN IF NOT EXISTS datecreated timestamp without time zone,
	ADD COLUMN IF NOT EXISTS dateupdated timestamp without time zone;
	
UPDATE bulkloadfile
	SET datecreated = created;
	
UPDATE bulkloadfile
	SET dateupdated = lastupdated;
	
ALTER TABLE bulkloadfile
	DROP COLUMN IF EXISTS created,
	DROP COLUMN IF EXISTS lastupdated;
	
ALTER TABLE bulkloadfileexception
	ADD COLUMN IF NOT EXISTS datecreated timestamp without time zone,
	ADD COLUMN IF NOT EXISTS dateupdated timestamp without time zone;
	
UPDATE bulkloadfileexception
	SET datecreated = created;
	
UPDATE bulkloadfileexception
	SET dateupdated = lastupdated;
	
ALTER TABLE bulkloadfileexception
	DROP COLUMN IF EXISTS created,
	DROP COLUMN IF EXISTS lastupdated;
	
ALTER TABLE bulkloadfilehistory
	ADD COLUMN IF NOT EXISTS datecreated timestamp without time zone,
	ADD COLUMN IF NOT EXISTS dateupdated timestamp without time zone;
	
UPDATE bulkloadfilehistory
	SET datecreated = created;
	
UPDATE bulkloadfilehistory
	SET dateupdated = lastupdated;
	
ALTER TABLE bulkloadfilehistory
	DROP COLUMN IF EXISTS created,
	DROP COLUMN IF EXISTS lastupdated;
	
ALTER TABLE bulkloadgroup
	ADD COLUMN IF NOT EXISTS datecreated timestamp without time zone,
	ADD COLUMN IF NOT EXISTS dateupdated timestamp without time zone;
	
UPDATE bulkloadgroup
	SET datecreated = created;
	
UPDATE bulkloadgroup
	SET dateupdated = lastupdated;
	
ALTER TABLE bulkloadgroup
	DROP COLUMN IF EXISTS created,
	DROP COLUMN IF EXISTS lastupdated;
	
ALTER TABLE conditionrelation
	DROP COLUMN IF EXISTS created,
	DROP COLUMN IF EXISTS lastupdated;

ALTER TABLE conditionrelation
	ADD COLUMN IF NOT EXISTS datecreated timestamp without time zone,
	ADD COLUMN IF NOT EXISTS dateupdated timestamp without time zone;
		
ALTER TABLE crossreference
	DROP COLUMN IF EXISTS created,
	DROP COLUMN IF EXISTS lastupdated;
		
ALTER TABLE diseaseannotation
	ADD COLUMN IF NOT EXISTS datelastmodified timestamp without time zone,
	ADD COLUMN IF NOT EXISTS creationdate timestamp without time zone,
	ADD COLUMN IF NOT EXISTS datecreated timestamp without time zone,
	ADD COLUMN IF NOT EXISTS dateupdated timestamp without time zone;
	
UPDATE diseaseannotation
	SET datecreated = creationdate;
	
UPDATE diseaseannotation
	SET dateupdated = datelastmodified;

ALTER TABLE diseaseannotation
	DROP COLUMN IF EXISTS datelastmodified,
	DROP COLUMN IF EXISTS creationdate;
	
ALTER TABLE experimentalcondition
	DROP COLUMN IF EXISTS created,
	DROP COLUMN IF EXISTS lastupdated;
	
ALTER TABLE genegenomiclocation
	DROP COLUMN IF EXISTS created,
	DROP COLUMN IF EXISTS lastupdated;
	
ALTER TABLE ontologyterm
	DROP COLUMN IF EXISTS created,
	DROP COLUMN IF EXISTS lastupdated;
	
ALTER TABLE person
	ADD COLUMN IF NOT EXISTS datecreated timestamp without time zone,
	ADD COLUMN IF NOT EXISTS dateupdated timestamp without time zone;
	
UPDATE person
	SET datecreated = created;
	
UPDATE person
	SET dateupdated = lastupdated;
	
ALTER TABLE person
	DROP COLUMN IF EXISTS created,
	DROP COLUMN IF EXISTS lastupdated;
	
ALTER TABLE reference
	ADD COLUMN IF NOT EXISTS datecreated timestamp without time zone,
	ADD COLUMN IF NOT EXISTS dateupdated timestamp without time zone;
	
UPDATE reference
	SET datecreated = created;
	
UPDATE reference
	SET dateupdated = lastupdated;
	
ALTER TABLE reference
	DROP COLUMN IF EXISTS created,
	DROP COLUMN IF EXISTS lastupdated;
	
ALTER TABLE synonym
	DROP COLUMN IF EXISTS created,
	DROP COLUMN IF EXISTS lastupdated;

ALTER TABLE synonym
	ADD COLUMN IF NOT EXISTS datecreated timestamp without time zone,
	ADD COLUMN IF NOT EXISTS dateupdated timestamp without time zone;
	
ALTER TABLE vocabulary
	ADD COLUMN IF NOT EXISTS datecreated timestamp without time zone,
	ADD COLUMN IF NOT EXISTS dateupdated timestamp without time zone;
	
UPDATE vocabulary
	SET datecreated = created;
	
UPDATE vocabulary
	SET dateupdated = lastupdated;
	
ALTER TABLE vocabulary
	DROP COLUMN IF EXISTS created,
	DROP COLUMN IF EXISTS lastupdated;
	
ALTER TABLE vocabularyterm
	ADD COLUMN IF NOT EXISTS datecreated timestamp without time zone,
	ADD COLUMN IF NOT EXISTS dateupdated timestamp without time zone;
	
UPDATE vocabularyterm
	SET datecreated = created;
	
UPDATE vocabularyterm
	SET dateupdated = lastupdated;
	
ALTER TABLE vocabularyterm
	DROP COLUMN IF EXISTS created,
	DROP COLUMN IF EXISTS lastupdated;
	

ALTER TABLE ONLY public.loggedinperson
    ADD CONSTRAINT uk_1x2yi78pxpi1vnjh8kp6jknbj UNIQUE (oktaid);
ALTER TABLE ONLY public.loggedinperson
    ADD CONSTRAINT uk_783vgdwg0048goe9pkt9npvqt UNIQUE (oktaemail);
ALTER TABLE ONLY public.person
    ADD CONSTRAINT uk_eqlixdg38sapqqrm25vt079jb UNIQUE (uniqueid);
ALTER TABLE ONLY public.person
    ADD CONSTRAINT uk_mh8bc6koyamm7yav5132wje7j UNIQUE (orcid);
ALTER TABLE ONLY public.person
    ADD CONSTRAINT uk_9omqedixfrwkqq9bdts63g65u UNIQUE (modentityid);
ALTER TABLE ONLY public.diseaseannotation_note
    ADD CONSTRAINT uk_f3o1apeoj9un48jw6qmiihpjc UNIQUE (relatednotes_id);

ALTER TABLE ONLY public.loggedinperson
    ADD CONSTRAINT fk6f4fytsv6sfgyggjjaq7s7wuw FOREIGN KEY (id) REFERENCES public.person(id);
