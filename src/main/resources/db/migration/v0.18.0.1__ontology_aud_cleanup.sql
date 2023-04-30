-- Add missing PK on atpterm table
ALTER TABLE ONLY atpterm ADD CONSTRAINT atpterm_pkey PRIMARY KEY (curie);
ALTER TABLE ONLY atpterm_aud ADD CONSTRAINT atpterm_aud_pkey PRIMARY KEY (curie, rev);

ALTER TABLE mpterm_aud DROP CONSTRAINT fkg4sqxe4ofrkn9vaenvdrrffwt; -- Old FK to ontology term
ALTER TABLE mpterm DROP CONSTRAINT fkta9f30vmw7h1smmv68to1ipyq; -- Old FK to ontology term

SET session_replication_role = replica;

SELECT DISTINCT(rev) INTO unused_ontologyterm_rev1 FROM atpterm_aud;
DELETE FROM atpterm_aud;
ALTER TABLE unused_ontologyterm_rev1 ADD PRIMARY KEY (rev);

SELECT DISTINCT(rev) INTO unused_chemicalterm_rev FROM chebiterm_aud;
DELETE FROM chebiterm_aud;
ALTER TABLE unused_chemicalterm_rev ADD PRIMARY KEY (rev);

SELECT DISTINCT(rev) INTO unused_anatomicalterm_rev FROM daoterm_aud;
DELETE FROM daoterm_aud;
ALTER TABLE unused_anatomicalterm_rev ADD PRIMARY KEY (rev);

INSERT INTO unused_ontologyterm_rev1 (rev) SELECT DISTINCT(rev) FROM doterm_aud;
DELETE FROM doterm_aud;

SELECT DISTINCT(rev) INTO unused_ontologyterm_rev2 FROM ecoterm_aud;
DELETE FROM ecoterm_aud;
ALTER TABLE unused_ontologyterm_rev2 ADD PRIMARY KEY (rev);

INSERT INTO unused_anatomicalterm_rev (rev) SELECT DISTINCT(rev) FROM emapaterm_aud;
DELETE FROM emapaterm_aud;

SELECT DISTINCT(rev) INTO unused_stageterm_rev FROM fbdvterm_aud;
DELETE FROM fbdvterm_aud;
ALTER TABLE unused_stageterm_rev ADD PRIMARY KEY (rev);

INSERT INTO unused_ontologyterm_rev2 (rev) SELECT DISTINCT(rev) FROM goterm_aud;
DELETE FROM goterm_aud;

INSERT INTO unused_anatomicalterm_rev (rev) SELECT DISTINCT(rev) FROM materm_aud;
DELETE FROM materm_aud;

INSERT INTO unused_ontologyterm_rev2 (rev) SELECT DISTINCT(rev) FROM mmusdvterm_aud;
DELETE FROM mmusdvterm_aud;

SELECT DISTINCT(rev) INTO unused_phenotypeterm_rev FROM mpterm_aud;
DELETE FROM mpterm_aud;
ALTER TABLE unused_phenotypeterm_rev ADD PRIMARY KEY (rev);

SELECT DISTINCT(rev) INTO unused_ontologyterm_rev3 FROM obiterm_aud;
DELETE FROM obiterm_aud;
ALTER TABLE unused_ontologyterm_rev3 ADD PRIMARY KEY (rev);

INSERT INTO unused_ontologyterm_rev3 (rev) SELECT DISTINCT(rev) FROM roterm_aud;
DELETE FROM roterm_aud;

INSERT INTO unused_ontologyterm_rev3 (rev) SELECT DISTINCT(rev) FROM soterm_aud;
DELETE FROM soterm_aud;

INSERT INTO unused_anatomicalterm_rev (rev) SELECT DISTINCT(rev) FROM wbbtterm_aud;
DELETE FROM wbbtterm_aud;

INSERT INTO unused_stageterm_rev (rev) SELECT DISTINCT(rev) FROM wblsterm_aud;
DELETE FROM wblsterm_aud;

INSERT INTO unused_anatomicalterm_rev (rev) SELECT DISTINCT(rev) FROM xbaterm_aud;
DELETE FROM xbaterm_aud;

INSERT INTO unused_ontologyterm_rev3 (rev) SELECT DISTINCT(rev) FROM xbedterm_aud;
DELETE FROM xbedterm_aud;

INSERT INTO unused_stageterm_rev (rev) SELECT DISTINCT(rev) FROM xbsterm_aud;
DELETE FROM xbsterm_aud;

SELECT DISTINCT(rev) INTO unused_experimentalconditionontologyterm_rev FROM xcoterm_aud;
DELETE FROM xcoterm_aud;
ALTER TABLE unused_experimentalconditionontologyterm_rev ADD PRIMARY KEY (rev);

INSERT INTO unused_phenotypeterm_rev (rev) SELECT DISTINCT(rev) FROM xpoterm_aud;
DELETE FROM xpoterm_aud;

INSERT INTO unused_chemicalterm_rev (rev) SELECT DISTINCT(rev) FROM xsmoterm_aud;
DELETE FROM xsmoterm_aud;

INSERT INTO unused_experimentalconditionontologyterm_rev (rev) SELECT DISTINCT(rev) FROM zecoterm_aud;
DELETE FROM zecoterm_aud;

INSERT INTO unused_anatomicalterm_rev (rev) SELECT DISTINCT(rev) FROM zfaterm_aud;
DELETE FROM zfaterm_aud;

INSERT INTO unused_stageterm_rev (rev) SELECT DISTINCT(rev) FROM zfsterm_aud;
DELETE FROM zfsterm_aud;

DELETE FROM anatomicalterm_aud;

DELETE FROM chemicalterm_aud
	USING unused_chemicalterm_rev
	WHERE chemicalterm_aud.rev = unused_chemicalterm_rev.rev;

DELETE FROM experimentalconditionontologyterm_aud;

DELETE FROM phenotypeterm_aud
	USING unused_phenotypeterm_rev
	WHERE phenotypeterm_aud.rev = unused_phenotypeterm_rev.rev;

DELETE FROM stageterm_aud;

DELETE FROM ontologyterm_crossreference_aud
	USING unused_ontologyterm_rev1
	WHERE ontologyterm_crossreference_aud.rev = unused_ontologyterm_rev1.rev;

DELETE FROM ontologyterm_crossreference_aud
	USING unused_ontologyterm_rev2
	WHERE ontologyterm_crossreference_aud.rev = unused_ontologyterm_rev2.rev;

DELETE FROM ontologyterm_crossreference_aud
	USING unused_ontologyterm_rev3
	WHERE ontologyterm_crossreference_aud.rev = unused_ontologyterm_rev3.rev;

DELETE FROM ontologyterm_crossreference_aud
	USING unused_anatomicalterm_rev
	WHERE ontologyterm_crossreference_aud.rev = unused_anatomicalterm_rev.rev;

DELETE FROM ontologyterm_crossreference_aud
	USING unused_chemicalterm_rev
	WHERE ontologyterm_crossreference_aud.rev = unused_chemicalterm_rev.rev;

DELETE FROM ontologyterm_crossreference_aud
	USING unused_experimentalconditionontologyterm_rev
	WHERE ontologyterm_crossreference_aud.rev = unused_experimentalconditionontologyterm_rev.rev;

DELETE FROM ontologyterm_crossreference_aud
	USING unused_phenotypeterm_rev
	WHERE ontologyterm_crossreference_aud.rev = unused_phenotypeterm_rev.rev;

DELETE FROM ontologyterm_crossreference_aud
	USING unused_stageterm_rev
	WHERE ontologyterm_crossreference_aud.rev = unused_stageterm_rev.rev;
	
DELETE FROM ontologyterm_definitionurls_aud
	USING unused_ontologyterm_rev1
	WHERE ontologyterm_definitionurls_aud.rev = unused_ontologyterm_rev1.rev;

DELETE FROM ontologyterm_definitionurls_aud
	USING unused_ontologyterm_rev2
	WHERE ontologyterm_definitionurls_aud.rev = unused_ontologyterm_rev2.rev;

DELETE FROM ontologyterm_definitionurls_aud
	USING unused_ontologyterm_rev3
	WHERE ontologyterm_definitionurls_aud.rev = unused_ontologyterm_rev3.rev;

DELETE FROM ontologyterm_definitionurls_aud
	USING unused_anatomicalterm_rev
	WHERE ontologyterm_definitionurls_aud.rev = unused_anatomicalterm_rev.rev;

DELETE FROM ontologyterm_definitionurls_aud
	USING unused_chemicalterm_rev
	WHERE ontologyterm_definitionurls_aud.rev = unused_chemicalterm_rev.rev;

DELETE FROM ontologyterm_definitionurls_aud
	USING unused_experimentalconditionontologyterm_rev
	WHERE ontologyterm_definitionurls_aud.rev = unused_experimentalconditionontologyterm_rev.rev;

DELETE FROM ontologyterm_definitionurls_aud
	USING unused_phenotypeterm_rev
	WHERE ontologyterm_definitionurls_aud.rev = unused_phenotypeterm_rev.rev;

DELETE FROM ontologyterm_definitionurls_aud
	USING unused_stageterm_rev
	WHERE ontologyterm_definitionurls_aud.rev = unused_stageterm_rev.rev;
	
DELETE FROM ontologyterm_isa_ancestor_descendant_aud
	USING unused_ontologyterm_rev1
	WHERE ontologyterm_isa_ancestor_descendant_aud.rev = unused_ontologyterm_rev1.rev;

DELETE FROM ontologyterm_isa_ancestor_descendant_aud
	USING unused_ontologyterm_rev2
	WHERE ontologyterm_isa_ancestor_descendant_aud.rev = unused_ontologyterm_rev2.rev;

DELETE FROM ontologyterm_isa_ancestor_descendant_aud
	USING unused_ontologyterm_rev3
	WHERE ontologyterm_isa_ancestor_descendant_aud.rev = unused_ontologyterm_rev3.rev;

DELETE FROM ontologyterm_isa_ancestor_descendant_aud
	USING unused_anatomicalterm_rev
	WHERE ontologyterm_isa_ancestor_descendant_aud.rev = unused_anatomicalterm_rev.rev;

DELETE FROM ontologyterm_isa_ancestor_descendant_aud
	USING unused_chemicalterm_rev
	WHERE ontologyterm_isa_ancestor_descendant_aud.rev = unused_chemicalterm_rev.rev;

DELETE FROM ontologyterm_isa_ancestor_descendant_aud
	USING unused_experimentalconditionontologyterm_rev
	WHERE ontologyterm_isa_ancestor_descendant_aud.rev = unused_experimentalconditionontologyterm_rev.rev;

DELETE FROM ontologyterm_isa_ancestor_descendant_aud
	USING unused_phenotypeterm_rev
	WHERE ontologyterm_isa_ancestor_descendant_aud.rev = unused_phenotypeterm_rev.rev;

DELETE FROM ontologyterm_isa_ancestor_descendant_aud
	USING unused_stageterm_rev
	WHERE ontologyterm_isa_ancestor_descendant_aud.rev = unused_stageterm_rev.rev;
	
DELETE FROM ontologyterm_isa_parent_children_aud
	USING unused_ontologyterm_rev1
	WHERE ontologyterm_isa_parent_children_aud.rev = unused_ontologyterm_rev1.rev;

DELETE FROM ontologyterm_isa_parent_children_aud
	USING unused_ontologyterm_rev2
	WHERE ontologyterm_isa_parent_children_aud.rev = unused_ontologyterm_rev2.rev;

DELETE FROM ontologyterm_isa_parent_children_aud
	USING unused_ontologyterm_rev3
	WHERE ontologyterm_isa_parent_children_aud.rev = unused_ontologyterm_rev3.rev;

DELETE FROM ontologyterm_isa_parent_children_aud
	USING unused_anatomicalterm_rev
	WHERE ontologyterm_isa_parent_children_aud.rev = unused_anatomicalterm_rev.rev;

DELETE FROM ontologyterm_isa_parent_children_aud
	USING unused_chemicalterm_rev
	WHERE ontologyterm_isa_parent_children_aud.rev = unused_chemicalterm_rev.rev;

DELETE FROM ontologyterm_isa_parent_children_aud
	USING unused_experimentalconditionontologyterm_rev
	WHERE ontologyterm_isa_parent_children_aud.rev = unused_experimentalconditionontologyterm_rev.rev;

DELETE FROM ontologyterm_isa_parent_children_aud
	USING unused_phenotypeterm_rev
	WHERE ontologyterm_isa_parent_children_aud.rev = unused_phenotypeterm_rev.rev;

DELETE FROM ontologyterm_isa_parent_children_aud
	USING unused_stageterm_rev
	WHERE ontologyterm_isa_parent_children_aud.rev = unused_stageterm_rev.rev;
	
DELETE FROM ontologyterm_secondaryidentifiers_aud
	USING unused_ontologyterm_rev1
	WHERE ontologyterm_secondaryidentifiers_aud.rev = unused_ontologyterm_rev1.rev;

DELETE FROM ontologyterm_secondaryidentifiers_aud
	USING unused_ontologyterm_rev2
	WHERE ontologyterm_secondaryidentifiers_aud.rev = unused_ontologyterm_rev2.rev;

DELETE FROM ontologyterm_secondaryidentifiers_aud
	USING unused_ontologyterm_rev3
	WHERE ontologyterm_secondaryidentifiers_aud.rev = unused_ontologyterm_rev3.rev;

DELETE FROM ontologyterm_secondaryidentifiers_aud
	USING unused_anatomicalterm_rev
	WHERE ontologyterm_secondaryidentifiers_aud.rev = unused_anatomicalterm_rev.rev;

DELETE FROM ontologyterm_secondaryidentifiers_aud
	USING unused_chemicalterm_rev
	WHERE ontologyterm_secondaryidentifiers_aud.rev = unused_chemicalterm_rev.rev;

DELETE FROM ontologyterm_secondaryidentifiers_aud
	USING unused_experimentalconditionontologyterm_rev
	WHERE ontologyterm_secondaryidentifiers_aud.rev = unused_experimentalconditionontologyterm_rev.rev;

DELETE FROM ontologyterm_secondaryidentifiers_aud
	USING unused_phenotypeterm_rev
	WHERE ontologyterm_secondaryidentifiers_aud.rev = unused_phenotypeterm_rev.rev;

DELETE FROM ontologyterm_secondaryidentifiers_aud
	USING unused_stageterm_rev
	WHERE ontologyterm_secondaryidentifiers_aud.rev = unused_stageterm_rev.rev;
	
DELETE FROM ontologyterm_subsets_aud
	USING unused_ontologyterm_rev1
	WHERE ontologyterm_subsets_aud.rev = unused_ontologyterm_rev1.rev;

DELETE FROM ontologyterm_subsets_aud
	USING unused_ontologyterm_rev2
	WHERE ontologyterm_subsets_aud.rev = unused_ontologyterm_rev2.rev;

DELETE FROM ontologyterm_subsets_aud
	USING unused_ontologyterm_rev3
	WHERE ontologyterm_subsets_aud.rev = unused_ontologyterm_rev3.rev;

DELETE FROM ontologyterm_subsets_aud
	USING unused_anatomicalterm_rev
	WHERE ontologyterm_subsets_aud.rev = unused_anatomicalterm_rev.rev;

DELETE FROM ontologyterm_subsets_aud
	USING unused_chemicalterm_rev
	WHERE ontologyterm_subsets_aud.rev = unused_chemicalterm_rev.rev;

DELETE FROM ontologyterm_subsets_aud
	USING unused_experimentalconditionontologyterm_rev
	WHERE ontologyterm_subsets_aud.rev = unused_experimentalconditionontologyterm_rev.rev;

DELETE FROM ontologyterm_subsets_aud
	USING unused_phenotypeterm_rev
	WHERE ontologyterm_subsets_aud.rev = unused_phenotypeterm_rev.rev;

DELETE FROM ontologyterm_subsets_aud
	USING unused_stageterm_rev
	WHERE ontologyterm_subsets_aud.rev = unused_stageterm_rev.rev;
	
DELETE FROM ontologyterm_synonym_aud
	USING unused_ontologyterm_rev1
	WHERE ontologyterm_synonym_aud.rev = unused_ontologyterm_rev1.rev;

DELETE FROM ontologyterm_synonym_aud
	USING unused_ontologyterm_rev2
	WHERE ontologyterm_synonym_aud.rev = unused_ontologyterm_rev2.rev;

DELETE FROM ontologyterm_synonym_aud
	USING unused_ontologyterm_rev3
	WHERE ontologyterm_synonym_aud.rev = unused_ontologyterm_rev3.rev;

DELETE FROM ontologyterm_synonym_aud
	USING unused_anatomicalterm_rev
	WHERE ontologyterm_synonym_aud.rev = unused_anatomicalterm_rev.rev;

DELETE FROM ontologyterm_synonym_aud
	USING unused_chemicalterm_rev
	WHERE ontologyterm_synonym_aud.rev = unused_chemicalterm_rev.rev;

DELETE FROM ontologyterm_synonym_aud
	USING unused_experimentalconditionontologyterm_rev
	WHERE ontologyterm_synonym_aud.rev = unused_experimentalconditionontologyterm_rev.rev;

DELETE FROM ontologyterm_synonym_aud
	USING unused_phenotypeterm_rev
	WHERE ontologyterm_synonym_aud.rev = unused_phenotypeterm_rev.rev;

DELETE FROM ontologyterm_synonym_aud
	USING unused_stageterm_rev
	WHERE ontologyterm_synonym_aud.rev = unused_stageterm_rev.rev;
	
DELETE FROM synonym_aud
	USING unused_ontologyterm_rev1
	WHERE synonym_aud.rev = unused_ontologyterm_rev1.rev;

DELETE FROM synonym_aud
	USING unused_ontologyterm_rev2
	WHERE synonym_aud.rev = unused_ontologyterm_rev2.rev;

DELETE FROM synonym_aud
	USING unused_ontologyterm_rev3
	WHERE synonym_aud.rev = unused_ontologyterm_rev3.rev;

DELETE FROM synonym_aud
	USING unused_anatomicalterm_rev
	WHERE synonym_aud.rev = unused_anatomicalterm_rev.rev;

DELETE FROM synonym_aud
	USING unused_chemicalterm_rev
	WHERE synonym_aud.rev = unused_chemicalterm_rev.rev;

DELETE FROM synonym_aud
	USING unused_experimentalconditionontologyterm_rev
	WHERE synonym_aud.rev = unused_experimentalconditionontologyterm_rev.rev;

DELETE FROM synonym_aud
	USING unused_phenotypeterm_rev
	WHERE synonym_aud.rev = unused_phenotypeterm_rev.rev;

DELETE FROM synonym_aud
	USING unused_stageterm_rev
	WHERE synonym_aud.rev = unused_stageterm_rev.rev;
	
DELETE FROM reference_aud
	USING unused_ontologyterm_rev1
	WHERE reference_aud.rev = unused_ontologyterm_rev1.rev;

DELETE FROM reference_aud
	USING unused_ontologyterm_rev2
	WHERE reference_aud.rev = unused_ontologyterm_rev2.rev;

DELETE FROM reference_aud
	USING unused_ontologyterm_rev3
	WHERE reference_aud.rev = unused_ontologyterm_rev3.rev;

DELETE FROM reference_aud
	USING unused_anatomicalterm_rev
	WHERE reference_aud.rev = unused_anatomicalterm_rev.rev;

DELETE FROM reference_aud
	USING unused_chemicalterm_rev
	WHERE reference_aud.rev = unused_chemicalterm_rev.rev;

DELETE FROM reference_aud
	USING unused_experimentalconditionontologyterm_rev
	WHERE reference_aud.rev = unused_experimentalconditionontologyterm_rev.rev;

DELETE FROM reference_aud
	USING unused_phenotypeterm_rev
	WHERE reference_aud.rev = unused_phenotypeterm_rev.rev;

DELETE FROM reference_aud
	USING unused_stageterm_rev
	WHERE reference_aud.rev = unused_stageterm_rev.rev;
	
DELETE FROM informationcontententity_aud
	USING unused_ontologyterm_rev1
	WHERE informationcontententity_aud.rev = unused_ontologyterm_rev1.rev;

DELETE FROM informationcontententity_aud
	USING unused_ontologyterm_rev2
	WHERE informationcontententity_aud.rev = unused_ontologyterm_rev2.rev;

DELETE FROM informationcontententity_aud
	USING unused_ontologyterm_rev3
	WHERE informationcontententity_aud.rev = unused_ontologyterm_rev3.rev;

DELETE FROM informationcontententity_aud
	USING unused_anatomicalterm_rev
	WHERE informationcontententity_aud.rev = unused_anatomicalterm_rev.rev;

DELETE FROM informationcontententity_aud
	USING unused_chemicalterm_rev
	WHERE informationcontententity_aud.rev = unused_chemicalterm_rev.rev;

DELETE FROM informationcontententity_aud
	USING unused_experimentalconditionontologyterm_rev
	WHERE informationcontententity_aud.rev = unused_experimentalconditionontologyterm_rev.rev;

DELETE FROM informationcontententity_aud
	USING unused_phenotypeterm_rev
	WHERE informationcontententity_aud.rev = unused_phenotypeterm_rev.rev;

DELETE FROM informationcontententity_aud
	USING unused_stageterm_rev
	WHERE informationcontententity_aud.rev = unused_stageterm_rev.rev;
	
DELETE FROM ontologyterm_aud
	USING unused_ontologyterm_rev1
	WHERE ontologyterm_aud.rev = unused_ontologyterm_rev1.rev;

DELETE FROM ontologyterm_aud
	USING unused_ontologyterm_rev2
	WHERE ontologyterm_aud.rev = unused_ontologyterm_rev2.rev;

DELETE FROM ontologyterm_aud
	USING unused_ontologyterm_rev3
	WHERE ontologyterm_aud.rev = unused_ontologyterm_rev3.rev;

DELETE FROM ontologyterm_aud
	USING unused_anatomicalterm_rev
	WHERE ontologyterm_aud.rev = unused_anatomicalterm_rev.rev;

DELETE FROM ontologyterm_aud
	USING unused_chemicalterm_rev
	WHERE ontologyterm_aud.rev = unused_chemicalterm_rev.rev;

DELETE FROM ontologyterm_aud
	USING unused_experimentalconditionontologyterm_rev
	WHERE ontologyterm_aud.rev = unused_experimentalconditionontologyterm_rev.rev;

DELETE FROM ontologyterm_aud
	USING unused_phenotypeterm_rev
	WHERE ontologyterm_aud.rev = unused_phenotypeterm_rev.rev;

DELETE FROM ontologyterm_aud
	USING unused_stageterm_rev
	WHERE ontologyterm_aud.rev = unused_stageterm_rev.rev;
	
DELETE FROM revinfo
	USING unused_ontologyterm_rev1
	WHERE revinfo.rev = unused_ontologyterm_rev1.rev;

DELETE FROM revinfo
	USING unused_ontologyterm_rev2
	WHERE revinfo.rev = unused_ontologyterm_rev2.rev;

DELETE FROM revinfo
	USING unused_ontologyterm_rev3
	WHERE revinfo.rev = unused_ontologyterm_rev3.rev;

DELETE FROM revinfo
	USING unused_anatomicalterm_rev
	WHERE revinfo.rev = unused_anatomicalterm_rev.rev;

DELETE FROM revinfo
	USING unused_chemicalterm_rev
	WHERE revinfo.rev = unused_chemicalterm_rev.rev;

DELETE FROM revinfo
	USING unused_experimentalconditionontologyterm_rev
	WHERE revinfo.rev = unused_experimentalconditionontologyterm_rev.rev;

DELETE FROM revinfo
	USING unused_phenotypeterm_rev
	WHERE revinfo.rev = unused_phenotypeterm_rev.rev;

DELETE FROM revinfo
	USING unused_stageterm_rev
	WHERE revinfo.rev = unused_stageterm_rev.rev;

DROP TABLE unused_ontologyterm_rev1;
DROP TABLE unused_ontologyterm_rev2;
DROP TABLE unused_ontologyterm_rev3;
DROP TABLE unused_anatomicalterm_rev;
DROP TABLE unused_chemicalterm_rev;
DROP TABLE unused_experimentalconditionontologyterm_rev;
DROP TABLE unused_phenotypeterm_rev;
DROP TABLE unused_stageterm_rev;

SET session_replication_role = 'origin';