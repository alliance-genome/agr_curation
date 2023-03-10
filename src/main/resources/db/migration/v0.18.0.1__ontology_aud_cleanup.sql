SELECT rev INTO unused_ontology_rev FROM atpterm_aud;
DELETE FROM atpterm_aud;

INSERT INTO unused_ontology_rev (rev) SELECT rev FROM chebiterm_aud;
DELETE FROM chebiterm_aud;

INSERT INTO unused_ontology_rev (rev) SELECT rev FROM daoterm_aud;
DELETE FROM daoterm_aud;

INSERT INTO unused_ontology_rev (rev) SELECT rev FROM doterm_aud;
DELETE FROM doterm_aud;

INSERT INTO unused_ontology_rev (rev) SELECT rev FROM ecoterm_aud;
DELETE FROM ecoterm_aud;

INSERT INTO unused_ontology_rev (rev) SELECT rev FROM emapaterm_aud;
DELETE FROM emapaterm_aud;

INSERT INTO unused_ontology_rev (rev) SELECT rev FROM fbdvterm_aud;
DELETE FROM fbdvterm_aud;

INSERT INTO unused_ontology_rev (rev) SELECT rev FROM goterm_aud;
DELETE FROM goterm_aud;

INSERT INTO unused_ontology_rev (rev) SELECT rev FROM materm_aud;
DELETE FROM materm_aud;

INSERT INTO unused_ontology_rev (rev) SELECT rev FROM mmusdvterm_aud;
DELETE FROM mmusdvterm_aud;

INSERT INTO unused_ontology_rev (rev) SELECT rev FROM mpterm_aud;
DELETE FROM mpterm_aud;

INSERT INTO unused_ontology_rev (rev) SELECT rev FROM obiterm_aud;
DELETE FROM obiterm_aud;

INSERT INTO unused_ontology_rev (rev) SELECT rev FROM roterm_aud;
DELETE FROM roterm_aud;

INSERT INTO unused_ontology_rev (rev) SELECT rev FROM soterm_aud;
DELETE FROM soterm_aud;

INSERT INTO unused_ontology_rev (rev) SELECT rev FROM wbbtterm_aud;
DELETE FROM wbbtterm_aud;

INSERT INTO unused_ontology_rev (rev) SELECT rev FROM wblsterm_aud;
DELETE FROM wblsterm_aud;

INSERT INTO unused_ontology_rev (rev) SELECT rev FROM xbaterm_aud;
DELETE FROM xbaterm_aud;

INSERT INTO unused_ontology_rev (rev) SELECT rev FROM xbedterm_aud;
DELETE FROM xbedterm_aud;

INSERT INTO unused_ontology_rev (rev) SELECT rev FROM xbsterm_aud;
DELETE FROM xbsterm_aud;

INSERT INTO unused_ontology_rev (rev) SELECT rev FROM xcoterm_aud;
DELETE FROM xcoterm_aud;

INSERT INTO unused_ontology_rev (rev) SELECT rev FROM xpoterm_aud;
DELETE FROM xpoterm_aud;

INSERT INTO unused_ontology_rev (rev) SELECT rev FROM xsmoterm_aud;
DELETE FROM xsmoterm_aud;

INSERT INTO unused_ontology_rev (rev) SELECT rev FROM zecoterm_aud;
DELETE FROM zecoterm_aud;

INSERT INTO unused_ontology_rev (rev) SELECT rev FROM zfaterm_aud;
DELETE FROM zfaterm_aud;

INSERT INTO unused_ontology_rev (rev) SELECT rev FROM zfsterm_aud;
DELETE FROM zfsterm_aud;

DELETE FROM anatomicalterm_aud;

DELETE FROM chemicalterm_aud WHERE rev IN (SELECT rev FROM unused_ontology_rev);

DELETE FROM experimentalconditionontologyterm_aud;

DELETE FROM phenotypeterm_aud WHERE rev IN (SELECT rev FROM unused_ontology_rev);

DELETE FROM stageterm_aud;

DELETE FROM ontologyterm_crossreference_aud WHERE rev in (SELECT rev FROM unused_ontology_rev);

DELETE FROM ontologyterm_definitionurls_aud WHERE rev in (SELECT rev FROM unused_ontology_rev);

DELETE FROM ontologyterm_isa_ancestor_descendant_aud WHERE rev in (SELECT rev FROM unused_ontology_rev);

DELETE FROM ontologyterm_isa_parent_children_aud WHERE rev in (SELECT rev FROM unused_ontology_rev);

DELETE FROM ontologyterm_secondaryidentifiers_aud WHERE rev in (SELECT rev FROM unused_ontology_rev);

DELETE FROM ontologyterm_subsets_aud WHERE rev in (SELECT rev FROM unused_ontology_rev);

DELETE FROM ontologyterm_synonym_aud WHERE rev in (SELECT rev FROM unused_ontology_rev);

DELETE FROM ontologyterm_aud WHERE rev IN (SELECT rev FROM unused_ontology_rev);

DELETE FROM revinfo WHERE rev IN (SELECT rev FROM unused_ontology_rev);

DROP TABLE unused_ontology_rev;