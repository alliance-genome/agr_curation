ALTER TABLE phenotypeannotation ADD COLUMN crossreference_id bigint;
ALTER TABLE phenotypeannotation ADD CONSTRAINT crossreference_id_fk FOREIGN KEY (crossreference_id) REFERENCES crossreference (id);