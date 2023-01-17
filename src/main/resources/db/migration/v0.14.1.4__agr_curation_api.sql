CREATE TABLE resourcedescriptor (
	id bigint CONSTRAINT resourcedescriptor_pkey PRIMARY KEY,
	prefix varchar(255) UNIQUE NOT NULL,
	name varchar(255),
	idpattern varchar(255),
	idexample timestamp without time zone,
	dateupdated timestamp without time zone,
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
		
ALTER TABLE resourcedescriptor_aud
	ADD CONSTRAINT resourcedescriptor_aud_id_fk
		FOREIGN KEY (id) REFERENCES resourcedescriptor (id);
		
CREATE TABLE resourcedescriptor_synonym (
	resourcedescriptor_id bigint NOT NULL,
	synonym varchar(255)
);
	
CREATE TABLE resourcedescriptor_synonym_aud (
	resourcedescriptor_id bigint NOT NULL,
	rev integer NOT Null,
	revtype smallint,
	synonym varchar(255),
	PRIMARY KEY (id, rev)
);
	
ALTER TABLE resourcedescriptor_synonym
	ADD CONSTRAINT resourcedescriptor_synonym_resourcedescriptor_id_fk
		FOREIGN KEY (resourcedescriptor_id) REFERENCES resourcedescriptor (id);
		
ALTER TABLE resourcedescriptor_synonym_aud
	ADD CONSTRAINT resourcedescriptor_synonym_aud_rev_fk
		FOREIGN_KEY (rev) REFERENCES revinfo (rev);	

CREATE TABLE resourcedescriptorpage (
	id bigint CONSTRAINT resourcedescriptorpage_pkey PRIMARY KEY,
	name varchar(255) UNIQUE NOT NULL,
	urltemplate varchar(255),
	pagedescription varchar(255),
	resourcedescriptor_id bigint
);

CREATE TABLE resourcedescriptorpage_aud (
	id bigint NOT NULL,
	rev integer NOT NULL,
	revtype smallint,
	name varchar(255),
	urltemplate varchar(255),
	pagedescriptrion varchar(255),
	resourcedescriptor_id bigint
);

ALTER TABLE resourcedescriptorpage
	ADD CONSTRAINT resourcedescriptorpage_resourcedescriptor_id_fk
		FOREIGN KEY (resourcedescriptor_id) REFERENCES resourcedescriptor (id);

ALTER TABLE resourcedescriptoroage_aud
	ADD CONSTRAINT resourcedescriptorpage_aud_rev_fk
		FOREIGN KEY (rev) REFERENCES revinfo (rev);
		
ALTER TABLE resourcedescriptorpage_aud
	ADD CONSTRAINT resourcedescriptorpage_aud_id_fk
		FOREIGN KEY (id) REFERENCES resourcedescriptorpage (id);