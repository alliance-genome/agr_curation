-- SCRUM-6535: foreign keys from chromosomeaccession's audit columns to person.
--
-- v0.53.0.2 created chromosomeaccession with createdby_id and updatedby_id but no foreign keys on
-- them, so Hibernate's schema update adds its own. Alpha, beta and production all lack them, and no
-- row on any of them references a missing person, so both apply cleanly.

ALTER TABLE chromosomeaccession
	ADD CONSTRAINT chromosomeaccession_createdby_id_fk FOREIGN KEY (createdby_id) REFERENCES person (id);
ALTER TABLE chromosomeaccession
	ADD CONSTRAINT chromosomeaccession_updatedby_id_fk FOREIGN KEY (updatedby_id) REFERENCES person (id);
