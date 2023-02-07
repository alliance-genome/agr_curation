CREATE TABLE resourcedescriptor (
	id bigint CONSTRAINT resourcedescriptor_pkey PRIMARY KEY,
	prefix varchar(255) UNIQUE NOT NULL,
	name varchar(255),
	idpattern varchar(255),
	idexample varchar(255),
	defaulturltemplate varchar(255),
	dateupdated timestamp without time zone,
	datecreated timestamp without time zone,
	dbdatecreated timestamp without time zone,
	dbdateupdated timestamp without time zone,
	internal boolean DEFAULT false,
	obsolete boolean DEFAULT false,
	createdby_id bigint,
	updatedby_id bigint
);
	
CREATE TABLE resourcedescriptor_aud (
	id bigint NOT NULL,
	rev integer NOT NULL,
	revtype smallint,
	prefix varchar(255) NOT NULL,
	name varchar(255),
	idpattern varchar(255),
	idexample varchar(255),
	defaulturltemplate varchar(255),
	PRIMARY KEY (id, rev)
);
	
ALTER TABLE resourcedescriptor
	ADD CONSTRAINT resourcedescriptor_createdby_id_fk
		FOREIGN KEY (createdby_id) REFERENCES person (id);	

ALTER TABLE resourcedescriptor
	ADD CONSTRAINT resourcedescriptor_updatedby_id_fk
		FOREIGN KEY (updatedby_id) REFERENCES person (id);

ALTER TABLE resourcedescriptor_aud
	ADD CONSTRAINT resourcedescriptor_aud_rev_fk
		FOREIGN KEY (rev) REFERENCES revinfo (rev);
		
CREATE TABLE resourcedescriptor_synonyms (
	resourcedescriptor_id bigint NOT NULL,
	synonyms varchar(255)
);
	
CREATE TABLE resourcedescriptor_synonyms_aud (
	resourcedescriptor_id bigint NOT NULL,
	rev integer NOT NULL,
	revtype smallint,
	synonyms varchar(255),
	PRIMARY KEY (resourcedescriptor_id, synonyms, rev)
);
	
ALTER TABLE resourcedescriptor_synonyms
	ADD CONSTRAINT resourcedescriptor_synonyms_resourcedescriptor_id_fk
		FOREIGN KEY (resourcedescriptor_id) REFERENCES resourcedescriptor (id);
		
ALTER TABLE resourcedescriptor_synonyms_aud
	ADD CONSTRAINT resourcedescriptor_synonyms_aud_rev_fk
		FOREIGN KEY (rev) REFERENCES revinfo (rev);	

CREATE TABLE resourcedescriptorpage (
	id bigint CONSTRAINT resourcedescriptorpage_pkey PRIMARY KEY,
	name varchar(255),
	urltemplate varchar(255),
	pagedescription varchar(255),
	dateupdated timestamp without time zone,
	datecreated timestamp without time zone,
	dbdatecreated timestamp without time zone,
	dbdateupdated timestamp without time zone,
	internal boolean DEFAULT false,
	obsolete boolean DEFAULT false,
	createdby_id bigint,
	updatedby_id bigint,
	resourcedescriptor_id bigint
);

CREATE TABLE resourcedescriptorpage_aud (
	id bigint NOT NULL,
	rev integer NOT NULL,
	revtype smallint,
	name varchar(255),
	urltemplate varchar(255),
	pagedescription varchar(255),
	resourcedescriptor_id bigint,
	PRIMARY KEY (id, rev)
);
	
ALTER TABLE resourcedescriptorpage
	ADD CONSTRAINT resourcedescriptorpage_createdby_id_fk
		FOREIGN KEY (createdby_id) REFERENCES person (id);	

ALTER TABLE resourcedescriptorpage
	ADD CONSTRAINT resourcedescriptorpage_updatedby_id_fk
		FOREIGN KEY (updatedby_id) REFERENCES person (id);

ALTER TABLE resourcedescriptorpage
	ADD CONSTRAINT resourcedescriptorpage_resourcedescriptor_id_fk
		FOREIGN KEY (resourcedescriptor_id) REFERENCES resourcedescriptor (id);

ALTER TABLE resourcedescriptorpage_aud
	ADD CONSTRAINT resourcedescriptorpage_aud_rev_fk
		FOREIGN KEY (rev) REFERENCES revinfo (rev);