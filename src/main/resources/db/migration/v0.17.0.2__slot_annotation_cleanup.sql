SELECT id INTO null_ids FROM genefullnameslotannotation WHERE singlegene_curie IS NULL;

DELETE FROM genefullnameslotannotation_aud WHERE id IN (SELECT id FROM genefullnameslotannotation WHERE singlegene_curie IS NULL);

DELETE FROM genefullnameslotannotation WHERE singlegene_curie IS NULL;

INSERT INTO null_ids (id) SELECT id FROM genesymbolslotannotation WHERE singlegene_curie IS NULL;

DELETE FROM genesymbolslotannotation_aud WHERE id IN (SELECT id FROM genesymbolslotannotation WHERE singlegene_curie IS NULL);

DELETE FROM genesymbolslotannotation WHERE singlegene_curie IS NULL;

INSERT INTO null_ids (id) SELECT id FROM genesynonymslotannotation WHERE singlegene_curie IS NULL;

DELETE FROM genesynonymslotannotation_aud WHERE id IN (SELECT id FROM genesynonymslotannotation WHERE singlegene_curie IS NULL);

DELETE FROM genesynonymslotannotation WHERE singlegene_curie IS NULL;

INSERT INTO null_ids (id) SELECT id FROM genesystematicnameslotannotation WHERE singlegene_curie IS NULL;

DELETE FROM genesystematicnameslotannotation_aud WHERE id IN (SELECT id FROM genesystematicnameslotannotation WHERE singlegene_curie IS NULL);

DELETE FROM genesystematicnameslotannotation WHERE singlegene_curie IS NULL;

INSERT INTO null_ids (id) SELECT id FROM allelefullnameslotannotation WHERE singleallele_curie IS NULL;

DELETE FROM allelefullnameslotannotation_aud WHERE id IN (SELECT id FROM allelefullnameslotannotation WHERE singleallele_curie IS NULL);

DELETE FROM allelefullnameslotannotation WHERE singleallele_curie IS NULL;

INSERT INTO null_ids (id) SELECT id FROM allelesymbolslotannotation WHERE singleallele_curie IS NULL;

DELETE FROM allelesymbolslotannotation_aud WHERE id IN (SELECT id FROM allelesymbolslotannotation WHERE singleallele_curie IS NULL);

DELETE FROM allelesymbolslotannotation WHERE singleallele_curie IS NULL;

INSERT INTO null_ids (id) SELECT id FROM allelesynonymslotannotation WHERE singleallele_curie IS NULL;

DELETE FROM allelesynonymslotannotation_aud WHERE id IN (SELECT id FROM allelesynonymslotannotation WHERE singleallele_curie IS NULL);

DELETE FROM allelesynonymslotannotation WHERE singleallele_curie IS NULL;

INSERT INTO null_ids (id) SELECT id FROM allelesecondaryidslotannotation WHERE singleallele_curie IS NULL;

DELETE FROM allelesecondaryidslotannotation_aud WHERE id IN (SELECT id FROM allelesecondaryidslotannotation WHERE singleallele_curie IS NULL);

DELETE FROM allelesecondaryidslotannotation WHERE singleallele_curie IS NULL;

INSERT INTO null_ids (id) SELECT id FROM alleleinheritancemodeslotannotation WHERE singleallele_curie IS NULL;

DELETE FROM alleleinheritancemodeslotannotation_aud WHERE id IN (SELECT id FROM alleleinheritancemodeslotannotation WHERE singleallele_curie IS NULL);

DELETE FROM alleleinheritancemodeslotannotation WHERE singleallele_curie IS NULL;

INSERT INTO null_ids (id) SELECT id FROM allelemutationtypeslotannotation WHERE singleallele_curie IS NULL;

DELETE FROM allelemutationtypeslotannotation_soterm_aud WHERE allelemutationtypeslotannotation_id IN (SELECT id FROM allelemutationtypeslotannotation WHERE singleallele_curie IS NULL);

DELETE FROM allelemutationtypeslotannotation_soterm WHERE allelemutationtypeslotannotation_id IN (SELECT id FROM allelemutationtypeslotannotation WHERE singleallele_curie IS NULL);

DELETE FROM allelemutationtypeslotannotation_aud WHERE id IN (SELECT id FROM allelemutationtypeslotannotation WHERE singleallele_curie IS NULL);

DELETE FROM allelemutationtypeslotannotation WHERE singleallele_curie IS NULL;

DELETE FROM nameslotannotation_aud WHERE id IN (select id FROM null_ids);

DELETE FROM nameslotannotation WHERE id IN (select id FROM null_ids);

DELETE FROM slotannotation_informationcontententity_aud WHERE slotannotation_id IN (select id FROM null_ids);

DELETE FROM slotannotation_informationcontententity WHERE slotannotation_id IN (select id FROM null_ids);

DELETE FROM slotannotation_aud WHERE id IN (select id FROM null_ids);

DELETE FROM slotannotation WHERE id IN (select id FROM null_ids);

DROP TABLE null_ids;
