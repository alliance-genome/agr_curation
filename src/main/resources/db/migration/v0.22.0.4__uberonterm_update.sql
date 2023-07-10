INSERT INTO anatomicalterm SELECT * FROM uberonterm;

INSERT INTO anatomicalterm_aud SELECT * FROM uberonterm_aud;

ALTER TABLE uberonterm
	DROP CONSTRAINT uberonterm_curie_fk;

ALTER TABLE uberonterm
    ADD CONSTRAINT uberonterm_curie_fk FOREIGN KEY (curie) REFERENCES anatomicalterm(curie);

ALTER TABLE uberonterm_aud
	DROP CONSTRAINT uberonterm_aud_curie_rev_fk;

ALTER TABLE uberonterm_aud
    ADD CONSTRAINT uberonterm_aud_curie_rev_fk FOREIGN KEY (curie, rev) REFERENCES anatomicalterm_aud(curie, rev);
