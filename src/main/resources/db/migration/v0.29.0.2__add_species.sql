CREATE TABLE species(
							 id bigint PRIMARY KEY,
							 datecreated timestamp without time zone,
							 dateupdated timestamp without time zone,
							 dbdatecreated timestamp without time zone,
							 dbdateupdated timestamp without time zone,
							 internal boolean NOT NULL DEFAULT false,
							 obsolete boolean NOT NULL DEFAULT false,
							 assembly_curie varchar(255),
							 fullname varchar(255),
							 phylogeneticorder int,
							 abbreviation varchar(255),
							 displayname varchar(255),
							 createdby_id bigint,
							 updatedby_id bigint,
							 dataprovider_id bigint,
							 taxon_curie varchar(255)
);

CREATE SEQUENCE species_seq     START WITH 1 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;



ALTER TABLE species ADD CONSTRAINT species_createdby_id_fk FOREIGN KEY (createdby_id) REFERENCES person (id);
ALTER TABLE species ADD CONSTRAINT species_updatedby_id_fk FOREIGN KEY (updatedby_id) REFERENCES person (id);
ALTER TABLE species ADD CONSTRAINT species_taxon_curie_fk FOREIGN KEY (taxon_curie) REFERENCES ncbitaxonterm (curie);
ALTER TABLE species ADD CONSTRAINT species_dataprovider_id_fk FOREIGN KEY (dataprovider_id) REFERENCES dataprovider (id);


CREATE INDEX species_createdby_index ON species USING btree(createdby_id);
CREATE INDEX species_updatedby_index ON species USING btree(updatedby_id);

CREATE TABLE species_aud (
							 id bigint NOT NULL,
							 abbreviation varchar(255),
							 displayname varchar(255),
							 fullname varchar(255),
							 taxon_curie varchar(255),
							 dataprovider_id bigint,
							 rev integer NOT NULL,
							 revtype smallint,
							 assembly_curie varchar(255),
							 phylogeneticorder int
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
    						revtype smallint
);

ALTER TABLE species_commonnames_aud ADD PRIMARY KEY (species_id, rev, commonnames);

ALTER TABLE species_commonnames_aud ADD CONSTRAINT species_commonnames_aud_rev_fk FOREIGN KEY (species_id) REFERENCES species(id);

INSERT INTO dataprovider (id, sourceorganization_id)
SELECT
    nextval('dataprovider_seq'), id
FROM organization
WHERE organization.abbreviation = 'ZFIN'
  AND EXISTS (
		SELECT 1
		FROM ncbitaxonterm
		WHERE curie = 'NCBITaxon:7955'
	);

INSERT INTO species (id, taxon_curie, abbreviation, fullname, dataprovider_id, phylogeneticorder,displayname,assembly_curie)
SELECT
	nextval('species_seq'),
	'NCBITaxon:7955',
	'Dre',
	'Danio rerio',
	currval('dataprovider_seq'),
	40,
	'ZFIN',
	'GRCz11'
WHERE EXISTS (
			  SELECT 1
			  FROM ncbitaxonterm
			  WHERE curie = 'NCBITaxon:7955'
		  );

INSERT INTO species_commonnames (species_id, commonnames)
SELECT
	(SELECT id FROM species WHERE fullname = 'Danio rerio'), commonnames
FROM
	(VALUES
		 ('zebrafish'),
		 ('fish'),
		 ('dre')
	) AS common_names (commonnames)
WHERE EXISTS (
			  SELECT 1
			  FROM ncbitaxonterm
			  WHERE curie = 'NCBITaxon:7955'
		  );


INSERT INTO dataprovider (id, sourceorganization_id)
SELECT
	nextval('dataprovider_seq'), id
FROM organization
WHERE organization.abbreviation = 'Alliance'
  AND EXISTS (
		SELECT 1
		FROM ncbitaxonterm
		WHERE curie = 'NCBITaxon:2697049'
	);

INSERT INTO species (id, taxon_curie, abbreviation, fullname, dataprovider_id, phylogeneticorder,displayname)
SELECT
	   nextval('species_seq'),
	   'NCBITaxon:2697049',
	   'SARS-CoV-2',
	   'SARS-CoV-2',
	   currval('dataprovider_seq'),
	   80,
	   'Alliance'
WHERE EXISTS (
			SELECT 1
			FROM ncbitaxonterm
			WHERE curie = 'NCBITaxon:2697049'
		);

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
	) AS common_names (commonnames)
WHERE EXISTS (
			  SELECT 1
			  FROM ncbitaxonterm
			  WHERE curie = 'NCBITaxon:2697049'
		  );



INSERT INTO dataprovider (id, sourceorganization_id)
SELECT
	nextval('dataprovider_seq'), id
FROM organization
WHERE organization.abbreviation = 'FB'
  AND EXISTS (
		SELECT 1
		FROM ncbitaxonterm
		WHERE curie = 'NCBITaxon:7227'
	);


INSERT INTO species (id, taxon_curie, abbreviation, fullname, dataprovider_id, phylogeneticorder,displayname,assembly_curie)
	SELECT
	    nextval('species_seq'),
	    'NCBITaxon:7227',
	    'Dme',
	    'Drosophila melanogaster',
		currval('dataprovider_seq'),
	    50,
	    'FB',
	    'R6'
	WHERE EXISTS (
			SELECT 1
			FROM ncbitaxonterm
			WHERE curie = 'NCBITaxon:7227'
		);

INSERT INTO species_commonnames (species_id, commonnames)
SELECT
	(SELECT id FROM species WHERE fullname = 'Drosophila melanogaster'), commonnames
FROM
	(VALUES
		 ('fly'),
		 ('fruit fly'),
		 ('dme')
	) AS common_names (commonnames)
WHERE EXISTS (
			  SELECT 1
			  FROM ncbitaxonterm
			  WHERE curie = 'NCBITaxon:7227'
		  );



INSERT INTO dataprovider (id, sourceorganization_id)
SELECT
	nextval('dataprovider_seq'), id
FROM organization
WHERE organization.abbreviation = 'WB'
  AND EXISTS (
		SELECT 1
		FROM ncbitaxonterm
		WHERE curie = 'NCBITaxon:6239'
	);


INSERT INTO species (id, taxon_curie, abbreviation, fullname, dataprovider_id, phylogeneticorder,displayname,assembly_curie)
	SELECT
	    nextval('species_seq'),
	    'NCBITaxon:6239',
	    'Cel',
	    'Caenorhabditis elegans',
		currval('dataprovider_seq'),
	    60,
	    'WB',
	    'WBcel235'
	WHERE EXISTS (
			SELECT 1
			FROM ncbitaxonterm
			WHERE curie = 'NCBITaxon:6239'
		);

INSERT INTO species_commonnames (species_id, commonnames)
SELECT
	(SELECT id FROM species WHERE fullname = 'Caenorhabditis elegans'), commonnames
FROM
	(VALUES
		 ('worm'),
		 ('cel')
	) AS common_names (commonnames)
WHERE EXISTS (
			  SELECT 1
			  FROM ncbitaxonterm
			  WHERE curie = 'NCBITaxon:6239'
		  );


INSERT INTO dataprovider (id, sourceorganization_id)
SELECT
	nextval('dataprovider_seq'), id
FROM organization
WHERE organization.abbreviation = 'RGD'
  AND EXISTS (
		SELECT 1
		FROM ncbitaxonterm
		WHERE curie = 'NCBITaxon:10116'
	);


INSERT INTO species (id, taxon_curie, abbreviation, fullname, dataprovider_id, phylogeneticorder,displayname,assembly_curie)
	SELECT
	    nextval('species_seq'),
	    'NCBITaxon:10116',
	    'Rno',
	    'Rattus norvegicus',
		currval('dataprovider_seq'),
	    20,
	    'RGD',
	    'mRatBN7.2'
	WHERE EXISTS (
			SELECT 1
			FROM ncbitaxonterm
			WHERE curie = 'NCBITaxon:10116'
		);

INSERT INTO species_commonnames (species_id, commonnames)
SELECT
	(SELECT id FROM species WHERE fullname = 'Rattus norvegicus'), commonnames
FROM
	(VALUES
		 ('rat'),
		 ('rno')
	) AS common_names (commonnames)
WHERE EXISTS (
			  SELECT 1
			  FROM ncbitaxonterm
			  WHERE curie = 'NCBITaxon:10116'
		  );


INSERT INTO dataprovider (id, sourceorganization_id)
SELECT
	nextval('dataprovider_seq'), id
FROM organization
WHERE organization.abbreviation = 'MGI'
  AND EXISTS (
		SELECT 1
		FROM ncbitaxonterm
		WHERE curie = 'NCBITaxon:10090'
	);

INSERT INTO species (id, taxon_curie, abbreviation, fullname, dataprovider_id, phylogeneticorder,displayname,assembly_curie)
	SELECT
	    nextval('species_seq'),
	    'NCBITaxon:10090',
	    'Mmu',
	    'Mus musculus',
		currval('dataprovider_seq'),
	    30,
	    'MGI',
	    'GRCm39'
	WHERE EXISTS (
			SELECT 1
			FROM ncbitaxonterm
			WHERE curie = 'NCBITaxon:10090'
		);

INSERT INTO species_commonnames (species_id, commonnames)
SELECT
	(SELECT id FROM species WHERE fullname = 'Mus musculus'), commonnames
FROM
	(VALUES
		 ('mouse'),
		 ('mmu')
	) AS common_names (commonnames)
WHERE EXISTS (
			  SELECT 1
			  FROM ncbitaxonterm
			  WHERE curie = 'NCBITaxon:10090'
		  );



INSERT INTO dataprovider (id, sourceorganization_id)
SELECT
	nextval('dataprovider_seq'), id
FROM organization
WHERE organization.abbreviation = 'SGD'
  AND EXISTS (
		SELECT 1
		FROM ncbitaxonterm
		WHERE curie = 'NCBITaxon:559292'
	);


INSERT INTO species (id, taxon_curie, abbreviation, fullname, dataprovider_id, phylogeneticorder,displayname,assembly_curie)
	SELECT
	    nextval('species_seq'),
	    'NCBITaxon:559292',
	    'Sce',
	    'Saccharomyces cerevisiae',
		currval('dataprovider_seq'),
	    70,
	    'SGD',
	    'R64-3-1'
	WHERE EXISTS (
			SELECT 1
			FROM ncbitaxonterm
			WHERE curie = 'NCBITaxon:559292'
		);

INSERT INTO species_commonnames (species_id, commonnames)
SELECT
	(SELECT id FROM species WHERE fullname = 'Saccharomyces cerevisiae'), commonnames
FROM
	(VALUES
		 ('yeast'),
		 ('sce')
	) AS common_names (commonnames)
WHERE EXISTS (
			  SELECT 1
			  FROM ncbitaxonterm
			  WHERE curie = 'NCBITaxon:559292'
		  );


INSERT INTO dataprovider (id, sourceorganization_id)
SELECT
	nextval('dataprovider_seq'), id
FROM organization
WHERE organization.abbreviation = 'RGD'
  AND EXISTS (
		SELECT 1
		FROM ncbitaxonterm
		WHERE curie = 'NCBITaxon:9606'
	);


INSERT INTO species (id, taxon_curie, abbreviation, fullname, dataprovider_id, phylogeneticorder,displayname,assembly_curie)
	SELECT
	    nextval('species_seq'),
	    'NCBITaxon:9606',
	    'Hsa',
	    'Homo sapiens',
		currval('dataprovider_seq'),
	    10,
	    'HUMAN',
	    'GRCh38'
	WHERE EXISTS (
			SELECT 1
			FROM ncbitaxonterm
			WHERE curie = 'NCBITaxon:9606'
		);

INSERT INTO species_commonnames (species_id, commonnames)
SELECT
	(SELECT id FROM species WHERE fullname = 'Homo sapiens'), commonnames
FROM
	(VALUES
		 ('human'),
		 ('hsa')
	) AS common_names (commonnames)
WHERE EXISTS (
			  SELECT 1
			  FROM ncbitaxonterm
			  WHERE curie = 'NCBITaxon:9606'
		  );


INSERT INTO dataprovider (id, sourceorganization_id)
SELECT
	nextval('dataprovider_seq'), id
FROM organization
WHERE organization.abbreviation = 'XB'
  AND EXISTS (
		SELECT 1
		FROM ncbitaxonterm
		WHERE curie = 'NCBITaxon:8355'
	);

INSERT INTO species (id, taxon_curie, abbreviation, fullname, dataprovider_id, phylogeneticorder,displayname,assembly_curie)
	SELECT
	    nextval('species_seq'),
	    'NCBITaxon:8355',
	    'Xla',
	    'Xenopus laevis',
		currval('dataprovider_seq'),
	    46,
	    'XBXL',
	    'XL10.1'
	WHERE EXISTS (
			SELECT 1
			FROM ncbitaxonterm
			WHERE curie = 'NCBITaxon:8355'
		);

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
	) AS common_names (commonnames)
WHERE EXISTS (
			  SELECT 1
			  FROM ncbitaxonterm
			  WHERE curie = 'NCBITaxon:8355'
		  );



INSERT INTO dataprovider (id, sourceorganization_id)
SELECT
	nextval('dataprovider_seq'), id
FROM organization
WHERE organization.abbreviation = 'XB'
  AND EXISTS (
		SELECT 1
		FROM ncbitaxonterm
		WHERE curie = 'NCBITaxon:8364'
	);

INSERT INTO species (id, taxon_curie, abbreviation, fullname, dataprovider_id, phylogeneticorder,displayname,assembly_curie)
	SELECT
	    nextval('species_seq'),
	    'NCBITaxon:8364',
	    'Xtr',
	    'Xenopus tropicalis',
		currval('dataprovider_seq'),
	    45,
	    'XBXT',
		'XT10.0'
	WHERE EXISTS (
			SELECT 1
			FROM ncbitaxonterm
			WHERE curie = 'NCBITaxon:8364'
		);

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
	) AS common_names (commonnames)
WHERE EXISTS (
			  SELECT 1
			  FROM ncbitaxonterm
			  WHERE curie = 'NCBITaxon:8364'
		  );
