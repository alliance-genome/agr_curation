SET session_replication_role = replica;

DELETE FROM atpterm_aud;
DELETE FROM chebiterm_aud;
DELETE FROM daoterm_aud;
DELETE FROM doterm_aud;
DELETE FROM ecoterm_aud;
DELETE FROM emapaterm_aud;
DELETE FROM fbdvterm_aud;
DELETE FROM goterm_aud;
DELETE FROM materm_aud;
DELETE FROM mmusdvterm_aud;
DELETE FROM mpterm_aud;
DELETE FROM ncbitaxonterm_aud;
DELETE FROM obiterm_aud;
DELETE FROM roterm_aud;
DELETE FROM soterm_aud;
DELETE FROM wbbtterm_aud;
DELETE FROM wblsterm_aud;
DELETE FROM xbaterm_aud;
DELETE FROM xbedterm_aud;
DELETE FROM xbsterm_aud;
DELETE FROM xcoterm_aud;
DELETE FROM xpoterm_aud;
DELETE FROM wbphenotypeterm_aud;
DELETE FROM xsmoterm_aud;
DELETE FROM zecoterm_aud;
DELETE FROM zfaterm_aud;
DELETE FROM zfsterm_aud;
DELETE FROM molecule_aud;

DELETE FROM phenotypeterm_aud;
DELETE FROM anatomicalterm_aud;
DELETE FROM phenotypeterm_aud;
DELETE FROM chemicalterm_aud;
DELETE FROM experimentalconditionontologyterm_aud;
DELETE FROM stageterm_aud;

DELETE FROM ontologyterm_crossreference_aud;
DELETE FROM ontologyterm_definitionurls_aud;
DELETE FROM ontologyterm_isa_ancestor_descendant_aud;
DELETE FROM ontologyterm_isa_parent_children_aud;
DELETE FROM ontologyterm_secondaryidentifiers_aud;
DELETE FROM ontologyterm_subsets_aud;
DELETE FROM ontologyterm_synonym_aud;

DELETE FROM ontologyterm_aud;

SET session_replication_role = 'origin';
