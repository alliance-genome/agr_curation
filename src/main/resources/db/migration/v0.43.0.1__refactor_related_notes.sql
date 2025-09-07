CREATE TABLE biologicalentity_note (
	submittedobject_id bigint,
	relatednotes_id bigint
	);
	
ALTER TABLE biologicalentity_note ADD CONSTRAINT biologicalentity_note_relatednotes_id_uk UNIQUE (relatednotes_id);
ALTER TABLE biologicalentity_note ADD CONSTRAINT biologicalentity_note_submittedobject_id_fk FOREIGN KEY (submittedobject_id) REFERENCES biologicalentity (id);
ALTER TABLE biologicalentity_note ADD CONSTRAINT biologicalentity_note_relatednotes_id_fk FOREIGN KEY (relatednotes_id) REFERENCES note (id);

INSERT INTO biologicalentity_note(submittedobject_id, relatednotes_id) SELECT allele_id, relatednotes_id FROM allele_note;
INSERT INTO biologicalentity_note(submittedobject_id, relatednotes_id) SELECT gene_id, relatednotes_id FROM gene_note;
INSERT INTO biologicalentity_note(submittedobject_id, relatednotes_id) SELECT variant_id, relatednotes_id FROM variant_note;

CREATE INDEX idx2jsfrdpiv07we916tdrv3mpkq ON biologicalentity_note USING btree (submittedobject_id);
CREATE INDEX idx6i4hjvhqrlly2gvu85dyjpq1g ON biologicalentity_note USING btree (relatednotes_id);

DROP TABLE allele_note;
DROP TABLE gene_note;
DROP TABLE variant_note;

CREATE TABLE reagent_note (
	submittedobject_id bigint,
	relatednotes_id bigint
	);
	
ALTER TABLE reagent_note ADD CONSTRAINT reagent_note_relatednotes_id_uk UNIQUE (relatednotes_id);
ALTER TABLE reagent_note ADD CONSTRAINT reagent_note_submittedobject_id_fk FOREIGN KEY (submittedobject_id) REFERENCES reagent (id);
ALTER TABLE reagent_note ADD CONSTRAINT reagent_note_relatednotes_id_fk FOREIGN KEY (relatednotes_id) REFERENCES note (id);

CREATE INDEX idxr3n77tcrjubcvaor107a7ad92 ON reagent_note USING btree (submittedobject_id);
CREATE INDEX idxags0p43afm5906murhxo9g3wh ON reagent_note USING btree (relatednotes_id);