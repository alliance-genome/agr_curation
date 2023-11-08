CREATE TABLE species(
							 id bigint PRIMARY KEY,
							 datecreated timestamp without time zone,
							 dateupdated timestamp without time zone,
							 dbdatecreated timestamp without time zone,
							 dbdateupdated timestamp without time zone,
							 internal boolean NOT NULL DEFAULT false,
							 obsolete boolean NOT NULL DEFAULT false,
							 assembly varchar(255),
							 fullname varchar(255),
							 phylogenicorder int,
							 shortname varchar(255),
							 createdby_id bigint,
							 updatedby_id bigint,
							 sourceorganization_id bigint,
							 taxon_curie varchar(255)
);


ALTER TABLE species ADD CONSTRAINT species_createdby_id_fk FOREIGN KEY (createdby_id) REFERENCES person (id);
ALTER TABLE species ADD CONSTRAINT species_updatedby_id_fk FOREIGN KEY (updatedby_id) REFERENCES person (id);
ALTER TABLE species ADD CONSTRAINT species_taxon_curie_fk FOREIGN KEY (taxon_curie) REFERENCES ncbitaxonterm (curie);
ALTER TABLE species ADD CONSTRAINT species_sourceorganization_id_fk FOREIGN KEY (sourceorganization_id) REFERENCES organization (id);


CREATE INDEX species_createdby_index ON species USING btree(createdby_id);
CREATE INDEX species_updatedby_index ON species USING btree(updatedby_id);

CREATE TABLE species_aud (
							 id bigint NOT NULL,
							 shortname varchar(255),
							 fullname varchar(255),
							 taxon_curie varchar(255),
							 sourceorganization_id bigint,
							 rev integer NOT NULL,
							 revtype smallint,
							 assembly varchar(255),
							 phylogenicorder int
);

ALTER TABLE species_aud ADD PRIMARY KEY (id, rev);

ALTER TABLE species_aud ADD CONSTRAINT species_aud_rev_fk FOREIGN KEY (rev) REFERENCES revinfo (rev);

CREATE TABLE species_commonnames (
    						species_id bigint NOT NULL,
							commonnames varchar(255)
);

ALTER TABLE species_commonnames ADD CONSTRAINT species_commonnames_species_id_fk FOREIGN KEY (species_id) REFERENCES species(id);

CREATE INDEX species_commonnames_species_id_index ON species_commonnames USING btree(species_id);

CREATE TABLE species_commonnames_aud (
    						rev integer NOT NULL,
    						species_id bigint NOT NULL,
    						commonnames varchar(255),
    						retype smallint
);

ALTER TABLE species_commonnames_aud ADD PRIMARY KEY (species_id, rev, commonnames);

ALTER TABLE species_commonnames_aud ADD CONSTRAINT species_commonnames_aud_rev_fk FOREIGN KEY (species_id) REFERENCES species(id);


INSERT INTO species (id, taxon_curie, shortname, fullname, sourceorganization_id, phylogenicorder) VALUES (nextval('hibernate_sequence'), 'NCBITaxon:7955', 'Dre', 'Danio rerio', (SELECT id FROM organization WHERE fullname = 'Zebrafish Information Network'), 40);

INSERT INTO species_commonnames (species_id, commonnames)
SELECT
	(SELECT id FROM species WHERE fullname = 'Danio rerio'), commonnames
FROM
	(VALUES
		 ('zebrafish'),
		 ('fish'),
		 ('dre')
	) AS common_names (commonnames);



INSERT INTO species (id, taxon_curie, shortname, fullname, sourceorganization_id, phylogenicorder) VALUES (nextval('hibernate_sequence'), 'NCBITaxon:2697049', 'SARS-CoV-2', 'SARS-CoV-2', (SELECT id FROM organization WHERE fullname = 'Alliance of Genome Resources'), 80);

INSERT INTO species_commonnames (species_id, commonnames)
SELECT
	(SELECT id FROM species WHERE fullname = 'SARS-CoV-2'), commonnames
FROM
	(VALUES
		 ('SARS-CoV-2'),
		 ('Severe acute respiratory syndrome coronavirus 2'),
		 ('SARS-CoV2'),
		 ('sars cov 2'),
		 ('SARS-2'),
		 ('SARS2'),
		 ('COVID'),
		 ('COVID19'),
		 ('COVID-19'),
		 ('COVID-19 virus'),
		 ('2019-nCoV'),
		 ('HCoV-19'),
		 ('Human coronavirus 2019')
	) AS common_names (commonnames);



INSERT INTO species (id, taxon_curie, shortname, fullname, sourceorganization_id, phylogenicorder) VALUES (nextval('hibernate_sequence'), 'NCBITaxon:7227', 'Dme', 'Drosophila melanogaster', (SELECT id FROM organization WHERE fullname = 'FlyBase'), 50);

INSERT INTO species_commonnames (species_id, commonnames)
SELECT
	(SELECT id FROM species WHERE fullname = 'Drosophila melanogaster'), commonnames
FROM
	(VALUES
		 ('fly'),
		 ('fruit fly'),
		 ('dme')
	) AS common_names (commonnames);



INSERT INTO species (id, taxon_curie, shortname, fullname, sourceorganization_id, phylogenicorder) VALUES (nextval('hibernate_sequence'), 'NCBITaxon:6239', 'Cel', 'Caenorhabditis elegans', (SELECT id FROM organization WHERE fullname = 'WormBase'), 60);

INSERT INTO species_commonnames (species_id, commonnames)
SELECT
	(SELECT id FROM species WHERE fullname = 'Caenorhabditis elegans'), commonnames
FROM
	(VALUES
		 ('worm'),
		 ('cel')
	) AS common_names (commonnames);



INSERT INTO species (id, taxon_curie, shortname, fullname, sourceorganization_id, phylogenicorder) VALUES (nextval('hibernate_sequence'), 'NCBITaxon:10116', 'Rno', 'Rattus norvegicus', (SELECT id FROM organization WHERE fullname = 'Rat Genome Database'), 20);

INSERT INTO species_commonnames (species_id, commonnames)
SELECT
	(SELECT id FROM species WHERE fullname = 'Rattus norvegicus'), commonnames
FROM
	(VALUES
		 ('rat'),
		 ('rno')
	) AS common_names (commonnames);



INSERT INTO species (id, taxon_curie, shortname, fullname, sourceorganization_id, phylogenicorder) VALUES (nextval('hibernate_sequence'), 'NCBITaxon:10090', 'Mmu', 'Mus musculus', (SELECT id FROM organization WHERE fullname = 'Mouse Genome Informatics'), 30);

INSERT INTO species_commonnames (species_id, commonnames)
SELECT
	(SELECT id FROM species WHERE fullname = 'Mus musculus'), commonnames
FROM
	(VALUES
		 ('mouse'),
		 ('mmu')
	) AS common_names (commonnames);




INSERT INTO species (id, taxon_curie, shortname, fullname, sourceorganization_id, phylogenicorder) VALUES (nextval('hibernate_sequence'), 'NCBITaxon:559292', 'Sce', 'Saccharomyces cerevisiae', (SELECT id FROM organization WHERE fullname = 'Saccharomyces Genome Database'), 70);

INSERT INTO species_commonnames (species_id, commonnames)
SELECT
	(SELECT id FROM species WHERE fullname = 'Saccharomyces cerevisiae'), commonnames
FROM
	(VALUES
		 ('yeast'),
		 ('sce')
	) AS common_names (commonnames);




INSERT INTO species (id, taxon_curie, shortname, fullname, sourceorganization_id, phylogenicorder) VALUES (nextval('hibernate_sequence'), 'NCBITaxon:9606', 'Hsa', 'Homo sapiens', (SELECT id FROM organization WHERE fullname = 'Rat Genome Database'), 10);

INSERT INTO species_commonnames (species_id, commonnames)
SELECT
	(SELECT id FROM species WHERE fullname = 'Homo sapiens'), commonnames
FROM
	(VALUES
		 ('human'),
		 ('hsa')
	) AS common_names (commonnames);




INSERT INTO species (id, taxon_curie, shortname, fullname, sourceorganization_id, phylogenicorder) VALUES (nextval('hibernate_sequence'), 'NCBITaxon:8355', 'Xla', 'Xenopus laevis', (SELECT id FROM organization WHERE fullname = 'Xenbase'), 46);

INSERT INTO species_commonnames (species_id, commonnames)
SELECT
	(SELECT id FROM species WHERE fullname = 'Xenopus laevis'), commonnames
FROM
	(VALUES
		 ('African clawed frog'),
		 ('xbxl'),
		 ('X.laevis'),
		 ('X. laevis'),
		 ('Bufo laevis'),
		 ('Common platanna'),
		 ('Platanna'),
		 ('African claw-toed frog')
	) AS common_names (commonnames);



INSERT INTO species (id, taxon_curie, shortname, fullname, sourceorganization_id, phylogenicorder) VALUES (nextval('hibernate_sequence'), 'NCBITaxon:8364', 'Xtr', 'Xenopus tropicalis', (SELECT id FROM organization WHERE fullname = 'Xenbase'), 45);

INSERT INTO species_commonnames (species_id, commonnames)
SELECT
	(SELECT id FROM species WHERE fullname = 'Xenopus tropicalis'), commonnames
FROM
	(VALUES
		 ('Western clawed frog'),
		 ('xbxt'),
		 ('X.tropicalis'),
		 ('X. tropicalis'),
		 ('Tropical clawed frog'),
		 ('Silurana tropicalis')
	) AS common_names (commonnames);



