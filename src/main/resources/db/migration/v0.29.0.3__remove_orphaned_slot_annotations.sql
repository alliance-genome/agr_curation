CREATE TABLE tmp_sa_ids (
	id bigint
);

CREATE TABLE tmp_nsa_ids (
	id bigint
);

CREATE TABLE tmp_sidsa_ids (
	id bigint
);

CREATE TABLE tmp_note_ids (
	id bigint
);

INSERT INTO tmp_sa_ids SELECT id FROM alleledatabasestatusslotannotation WHERE singleallele_curie IS NULL;
DELETE FROM alleledatabasestatusslotannotation WHERE singleallele_curie IS NULL;

INSERT INTO tmp_sa_ids SELECT id FROM allelefullnameslotannotation WHERE singleallele_curie IS NULL;
INSERT INTO tmp_nsa_ids SELECT id FROM allelefullnameslotannotation WHERE singleallele_curie IS NULL;
DELETE FROM allelefullnameslotannotation WHERE singleallele_curie IS NULL;

INSERT INTO tmp_sa_ids SELECT id FROM allelefunctionalimpactslotannotation WHERE singleallele_curie IS NULL;
DELETE FROM allelefunctionalimpactslotannotation_vocabularyterm WHERE allelefunctionalimpactslotannotation_id IN (SELECT id FROM allelefunctionalimpactslotannotation WHERE singleallele_curie IS NULL);
DELETE FROM allelefunctionalimpactslotannotation WHERE singleallele_curie IS NULL;

INSERT INTO tmp_sa_ids SELECT id FROM allelegermlinetransmissionstatusslotannotation WHERE singleallele_curie IS NULL;
DELETE FROM allelegermlinetransmissionstatusslotannotation WHERE singleallele_curie IS NULL;

INSERT INTO tmp_sa_ids SELECT id FROM alleleinheritancemodeslotannotation WHERE singleallele_curie IS NULL;
DELETE FROM alleleinheritancemodeslotannotation WHERE singleallele_curie IS NULL;

INSERT INTO tmp_sa_ids SELECT id FROM allelemutationtypeslotannotation WHERE singleallele_curie IS NULL;
DELETE FROM allelemutationtypeslotannotation_soterm WHERE allelemutationtypeslotannotation_id IN (SELECT id FROM allelemutationtypeslotannotation WHERE singleallele_curie IS NULL);
DELETE FROM allelemutationtypeslotannotation WHERE singleallele_curie IS NULL;

INSERT INTO tmp_sa_ids SELECT id FROM allelenomenclatureeventslotannotation WHERE singleallele_curie IS NULL;
DELETE FROM allelenomenclatureeventslotannotation WHERE singleallele_curie IS NULL;

INSERT INTO tmp_sa_ids SELECT id FROM allelesecondaryidslotannotation WHERE singleallele_curie IS NULL;
INSERT INTO tmp_sidsa_ids SELECT id FROM allelesecondaryidslotannotation WHERE singleallele_curie IS NULL;
DELETE FROM allelesecondaryidslotannotation WHERE singleallele_curie IS NULL;

INSERT INTO tmp_sa_ids SELECT id FROM allelesymbolslotannotation WHERE singleallele_curie IS NULL;
INSERT INTO tmp_nsa_ids SELECT id FROM allelesymbolslotannotation WHERE singleallele_curie IS NULL;
DELETE FROM allelesymbolslotannotation WHERE singleallele_curie IS NULL;

INSERT INTO tmp_sa_ids SELECT id FROM allelesynonymslotannotation WHERE singleallele_curie IS NULL;
INSERT INTO tmp_nsa_ids SELECT id FROM allelesynonymslotannotation WHERE singleallele_curie IS NULL;
DELETE FROM allelesynonymslotannotation WHERE singleallele_curie IS NULL;

INSERT INTO tmp_note_ids SELECT relatednotes_id FROM constructcomponentslotannotation_note WHERE constructcomponentslotannotation_id IN (SELECT id FROM constructcomponentslotannotation WHERE singleconstruct_id IS NULL);
INSERT INTO tmp_sa_ids SELECT id FROM constructcomponentslotannotation WHERE singleconstruct_id IS NULL;
DELETE FROM constructcomponentslotannotation_note WHERE constructcomponentslotannotation_id IN (SELECT id FROM constructcomponentslotannotation WHERE singleconstruct_id IS NULL);
DELETE FROM constructcomponentslotannotation WHERE singleconstruct_id IS NULL;

INSERT INTO tmp_sa_ids SELECT id FROM constructfullnameslotannotation WHERE singleconstruct_id IS NULL;
INSERT INTO tmp_nsa_ids SELECT id FROM constructfullnameslotannotation WHERE singleconstruct_id IS NULL;
DELETE FROM constructfullnameslotannotation WHERE singleconstruct_id IS NULL;

INSERT INTO tmp_sa_ids SELECT id FROM constructsymbolslotannotation WHERE singleconstruct_id IS NULL;
INSERT INTO tmp_nsa_ids SELECT id FROM constructsymbolslotannotation WHERE singleconstruct_id IS NULL;
DELETE FROM constructsymbolslotannotation WHERE singleconstruct_id IS NULL;

INSERT INTO tmp_sa_ids SELECT id FROM constructsynonymslotannotation WHERE singleconstruct_id IS NULL;
INSERT INTO tmp_nsa_ids SELECT id FROM constructsynonymslotannotation WHERE singleconstruct_id IS NULL;
DELETE FROM constructsynonymslotannotation WHERE singleconstruct_id IS NULL;

INSERT INTO tmp_sa_ids SELECT id FROM genefullnameslotannotation WHERE singlegene_curie IS NULL;
INSERT INTO tmp_nsa_ids SELECT id FROM genefullnameslotannotation WHERE singlegene_curie IS NULL;
DELETE FROM genefullnameslotannotation WHERE singlegene_curie IS NULL;

INSERT INTO tmp_sa_ids SELECT id FROM genesecondaryidslotannotation WHERE singlegene_curie IS NULL;
INSERT INTO tmp_sidsa_ids SELECT id FROM genesecondaryidslotannotation WHERE singlegene_curie IS NULL;
DELETE FROM genesecondaryidslotannotation WHERE singlegene_curie IS NULL;

INSERT INTO tmp_sa_ids SELECT id FROM genesymbolslotannotation WHERE singlegene_curie IS NULL;
INSERT INTO tmp_nsa_ids SELECT id FROM genesymbolslotannotation WHERE singlegene_curie IS NULL;
DELETE FROM genesymbolslotannotation WHERE singlegene_curie IS NULL;

INSERT INTO tmp_sa_ids SELECT id FROM genesynonymslotannotation WHERE singlegene_curie IS NULL;
INSERT INTO tmp_nsa_ids SELECT id FROM genesynonymslotannotation WHERE singlegene_curie IS NULL;
DELETE FROM genesynonymslotannotation WHERE singlegene_curie IS NULL;

INSERT INTO tmp_sa_ids SELECT id FROM genesystematicnameslotannotation WHERE singlegene_curie IS NULL;
INSERT INTO tmp_nsa_ids SELECT id FROM genesystematicnameslotannotation WHERE singlegene_curie IS NULL;
DELETE FROM genesystematicnameslotannotation WHERE singlegene_curie IS NULL;

DELETE FROM note_reference WHERE note_id IN (SELECT id FROM tmp_note_ids);
DELETE FROM note WHERE id IN (SELECT id FROM tmp_note_ids);
DROP TABLE tmp_note_ids;

DELETE FROM nameslotannotation WHERE id IN (SELECT id FROM tmp_nsa_ids);
DROP TABLE tmp_nsa_ids;

DELETE FROM secondaryidslotannotation WHERE id IN (SELECT id FROM tmp_sidsa_ids);
DROP TABLE tmp_sidsa_ids;

DELETE FROM slotannotation_informationcontententity WHERE slotannotation_id IN (SELECT id FROM tmp_sa_ids);
DELETE FROM slotannotation WHERE id IN (SELECT id FROM tmp_sa_ids);
DROP TABLE tmp_sa_ids;