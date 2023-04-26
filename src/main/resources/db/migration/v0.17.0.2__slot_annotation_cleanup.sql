CREATE TABLE null_ids ( id integer NOT NULL );
CREATE INDEX null_id_index on null_ids (id);

---

INSERT INTO null_ids (id) SELECT id FROM genefullnameslotannotation WHERE singlegene_curie IS NULL;
DELETE FROM genefullnameslotannotation_aud WHERE id IN (SELECT id FROM genefullnameslotannotation WHERE singlegene_curie IS NULL);
DELETE FROM genefullnameslotannotation WHERE singlegene_curie IS NULL;

DELETE FROM nameslotannotation_aud USING null_ids nameslotannotation_aud.id = null_ids.id;
DELETE FROM nameslotannotation USING null_ids nameslotannotation.id = null_ids.id;
DELETE FROM slotannotation_informationcontententity_aud USING null_ids slotannotation_informationcontententity_aud.id = null_ids.id;
DELETE FROM slotannotation_informationcontententity USING null_ids slotannotation_informationcontententity.id = null_ids.id;
DELETE FROM slotannotation_aud USING null_ids slotannotation_aud.id = null_ids.id;
DELETE FROM slotannotation USING null_ids slotannotation.id = null_ids.id;
DELETE FROM null_ids

---

INSERT INTO null_ids (id) SELECT id FROM genesymbolslotannotation WHERE singlegene_curie IS NULL;
DELETE FROM genesymbolslotannotation_aud WHERE id IN (SELECT id FROM genesymbolslotannotation WHERE singlegene_curie IS NULL);
DELETE FROM genesymbolslotannotation WHERE singlegene_curie IS NULL;

DELETE FROM nameslotannotation_aud USING null_ids nameslotannotation_aud.id = null_ids.id;
DELETE FROM nameslotannotation USING null_ids nameslotannotation.id = null_ids.id;
DELETE FROM slotannotation_informationcontententity_aud USING null_ids slotannotation_informationcontententity_aud.id = null_ids.id;
DELETE FROM slotannotation_informationcontententity USING null_ids slotannotation_informationcontententity.id = null_ids.id;
DELETE FROM slotannotation_aud USING null_ids slotannotation_aud.id = null_ids.id;
DELETE FROM slotannotation USING null_ids slotannotation.id = null_ids.id;
DELETE FROM null_ids

---

INSERT INTO null_ids (id) SELECT id FROM genesynonymslotannotation WHERE singlegene_curie IS NULL;
DELETE FROM genesynonymslotannotation_aud WHERE id IN (SELECT id FROM genesynonymslotannotation WHERE singlegene_curie IS NULL);
DELETE FROM genesynonymslotannotation WHERE singlegene_curie IS NULL;

DELETE FROM nameslotannotation_aud USING null_ids nameslotannotation_aud.id = null_ids.id;
DELETE FROM nameslotannotation USING null_ids nameslotannotation.id = null_ids.id;
DELETE FROM slotannotation_informationcontententity_aud USING null_ids slotannotation_informationcontententity_aud.id = null_ids.id;
DELETE FROM slotannotation_informationcontententity USING null_ids slotannotation_informationcontententity.id = null_ids.id;
DELETE FROM slotannotation_aud USING null_ids slotannotation_aud.id = null_ids.id;
DELETE FROM slotannotation USING null_ids slotannotation.id = null_ids.id;
DELETE FROM null_ids

---

INSERT INTO null_ids (id) SELECT id FROM genesystematicnameslotannotation WHERE singlegene_curie IS NULL;
DELETE FROM genesystematicnameslotannotation_aud WHERE id IN (SELECT id FROM genesystematicnameslotannotation WHERE singlegene_curie IS NULL);
DELETE FROM genesystematicnameslotannotation WHERE singlegene_curie IS NULL;

DELETE FROM nameslotannotation_aud USING null_ids nameslotannotation_aud.id = null_ids.id;
DELETE FROM nameslotannotation USING null_ids nameslotannotation.id = null_ids.id;
DELETE FROM slotannotation_informationcontententity_aud USING null_ids slotannotation_informationcontententity_aud.id = null_ids.id;
DELETE FROM slotannotation_informationcontententity USING null_ids slotannotation_informationcontententity.id = null_ids.id;
DELETE FROM slotannotation_aud USING null_ids slotannotation_aud.id = null_ids.id;
DELETE FROM slotannotation USING null_ids slotannotation.id = null_ids.id;
DELETE FROM null_ids

---

INSERT INTO null_ids (id) SELECT id FROM allelefullnameslotannotation WHERE singleallele_curie IS NULL;
DELETE FROM allelefullnameslotannotation_aud WHERE id IN (SELECT id FROM allelefullnameslotannotation WHERE singleallele_curie IS NULL);
DELETE FROM allelefullnameslotannotation WHERE singleallele_curie IS NULL;

DELETE FROM nameslotannotation_aud USING null_ids nameslotannotation_aud.id = null_ids.id;
DELETE FROM nameslotannotation USING null_ids nameslotannotation.id = null_ids.id;
DELETE FROM slotannotation_informationcontententity_aud USING null_ids slotannotation_informationcontententity_aud.id = null_ids.id;
DELETE FROM slotannotation_informationcontententity USING null_ids slotannotation_informationcontententity.id = null_ids.id;
DELETE FROM slotannotation_aud USING null_ids slotannotation_aud.id = null_ids.id;
DELETE FROM slotannotation USING null_ids slotannotation.id = null_ids.id;
DELETE FROM null_ids

---

INSERT INTO null_ids (id) SELECT id FROM allelesymbolslotannotation WHERE singleallele_curie IS NULL;
DELETE FROM allelesymbolslotannotation_aud WHERE id IN (SELECT id FROM allelesymbolslotannotation WHERE singleallele_curie IS NULL);
DELETE FROM allelesymbolslotannotation WHERE singleallele_curie IS NULL;

DELETE FROM nameslotannotation_aud USING null_ids nameslotannotation_aud.id = null_ids.id;
DELETE FROM nameslotannotation USING null_ids nameslotannotation.id = null_ids.id;
DELETE FROM slotannotation_informationcontententity_aud USING null_ids slotannotation_informationcontententity_aud.id = null_ids.id;
DELETE FROM slotannotation_informationcontententity USING null_ids slotannotation_informationcontententity.id = null_ids.id;
DELETE FROM slotannotation_aud USING null_ids slotannotation_aud.id = null_ids.id;
DELETE FROM slotannotation USING null_ids slotannotation.id = null_ids.id;
DELETE FROM null_ids

---

INSERT INTO null_ids (id) SELECT id FROM allelesynonymslotannotation WHERE singleallele_curie IS NULL;
DELETE FROM allelesynonymslotannotation_aud WHERE id IN (SELECT id FROM allelesynonymslotannotation WHERE singleallele_curie IS NULL);
DELETE FROM allelesynonymslotannotation WHERE singleallele_curie IS NULL;

DELETE FROM nameslotannotation_aud USING null_ids nameslotannotation_aud.id = null_ids.id;
DELETE FROM nameslotannotation USING null_ids nameslotannotation.id = null_ids.id;
DELETE FROM slotannotation_informationcontententity_aud USING null_ids slotannotation_informationcontententity_aud.id = null_ids.id;
DELETE FROM slotannotation_informationcontententity USING null_ids slotannotation_informationcontententity.id = null_ids.id;
DELETE FROM slotannotation_aud USING null_ids slotannotation_aud.id = null_ids.id;
DELETE FROM slotannotation USING null_ids slotannotation.id = null_ids.id;
DELETE FROM null_ids

---

INSERT INTO null_ids (id) SELECT id FROM allelesecondaryidslotannotation WHERE singleallele_curie IS NULL;
DELETE FROM allelesecondaryidslotannotation_aud WHERE id IN (SELECT id FROM allelesecondaryidslotannotation WHERE singleallele_curie IS NULL);
DELETE FROM allelesecondaryidslotannotation WHERE singleallele_curie IS NULL;

DELETE FROM nameslotannotation_aud USING null_ids nameslotannotation_aud.id = null_ids.id;
DELETE FROM nameslotannotation USING null_ids nameslotannotation.id = null_ids.id;
DELETE FROM slotannotation_informationcontententity_aud USING null_ids slotannotation_informationcontententity_aud.id = null_ids.id;
DELETE FROM slotannotation_informationcontententity USING null_ids slotannotation_informationcontententity.id = null_ids.id;
DELETE FROM slotannotation_aud USING null_ids slotannotation_aud.id = null_ids.id;
DELETE FROM slotannotation USING null_ids slotannotation.id = null_ids.id;
DELETE FROM null_ids

---

INSERT INTO null_ids (id) SELECT id FROM alleleinheritancemodeslotannotation WHERE singleallele_curie IS NULL;
DELETE FROM alleleinheritancemodeslotannotation_aud WHERE id IN (SELECT id FROM alleleinheritancemodeslotannotation WHERE singleallele_curie IS NULL);
DELETE FROM alleleinheritancemodeslotannotation WHERE singleallele_curie IS NULL;

DELETE FROM nameslotannotation_aud USING null_ids nameslotannotation_aud.id = null_ids.id;
DELETE FROM nameslotannotation USING null_ids nameslotannotation.id = null_ids.id;
DELETE FROM slotannotation_informationcontententity_aud USING null_ids slotannotation_informationcontententity_aud.id = null_ids.id;
DELETE FROM slotannotation_informationcontententity USING null_ids slotannotation_informationcontententity.id = null_ids.id;
DELETE FROM slotannotation_aud USING null_ids slotannotation_aud.id = null_ids.id;
DELETE FROM slotannotation USING null_ids slotannotation.id = null_ids.id;
DELETE FROM null_ids

---

INSERT INTO null_ids (id) SELECT id FROM allelemutationtypeslotannotation WHERE singleallele_curie IS NULL;
DELETE FROM allelemutationtypeslotannotation_soterm_aud WHERE allelemutationtypeslotannotation_id IN (SELECT id FROM allelemutationtypeslotannotation WHERE singleallele_curie IS NULL);
DELETE FROM allelemutationtypeslotannotation_soterm WHERE allelemutationtypeslotannotation_id IN (SELECT id FROM allelemutationtypeslotannotation WHERE singleallele_curie IS NULL);
DELETE FROM allelemutationtypeslotannotation_aud WHERE id IN (SELECT id FROM allelemutationtypeslotannotation WHERE singleallele_curie IS NULL);
DELETE FROM allelemutationtypeslotannotation WHERE singleallele_curie IS NULL;

DELETE FROM nameslotannotation_aud USING null_ids nameslotannotation_aud.id = null_ids.id;
DELETE FROM nameslotannotation USING null_ids nameslotannotation.id = null_ids.id;
DELETE FROM slotannotation_informationcontententity_aud USING null_ids slotannotation_informationcontententity_aud.id = null_ids.id;
DELETE FROM slotannotation_informationcontententity USING null_ids slotannotation_informationcontententity.id = null_ids.id;
DELETE FROM slotannotation_aud USING null_ids slotannotation_aud.id = null_ids.id;
DELETE FROM slotannotation USING null_ids slotannotation.id = null_ids.id;
DELETE FROM null_ids

---

DROP TABLE null_ids;
