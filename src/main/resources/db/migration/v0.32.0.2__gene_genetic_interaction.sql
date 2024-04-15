-- Create gene molecular interaction tables and indexes

CREATE TABLE genegeneticinteraction (
	id bigint PRIMARY KEY,
	interactorageneticperturbation_id bigint,
	interactorbgeneticperturbation_id bigint
);

ALTER TABLE genegeneticinteraction ADD CONSTRAINT genegeneticinteraction_id_fk FOREIGN KEY (id) REFERENCES geneinteraction (id);
ALTER TABLE genegeneticinteraction ADD CONSTRAINT genegeneticinteraction_interactorageneticperturbation_id_fk FOREIGN KEY (interactorageneticperturbation_id) REFERENCES allele (id);
ALTER TABLE genegeneticinteraction ADD CONSTRAINT genegeneticinteraction_interactorbgeneticperturbation_id_fk FOREIGN KEY (interactorbgeneticperturbation_id) REFERENCES allele (id);
CREATE INDEX genegeneticinteraction_interactorageneticperturbarion_index ON genegeneticinteraction USING btree (interactorageneticperturbation_id);
CREATE INDEX genegeneticinteraction_interactorbgeneticperturbarion_index ON genegeneticinteraction USING btree (interactorbgeneticperturbation_id);

CREATE TABLE genegeneticinteraction_phenotypesortraits (
	genegeneticinteraction_id bigint,
	phenotypesortraits varchar(255)
);
ALTER TABLE genegeneticinteraction_phenotypesortraits ADD CONSTRAINT genegeneticinteraction_phenotypesortraits_genegeneticinteraction_id_fk FOREIGN KEY (genegeneticinteraction_id) REFERENCES genegeneticinteraction (id);
CREATE INDEX genegeneticinteraction_phenotypesortraits_interaction_index ON genegeneticinteraction_phenotypesortraits USING btree (genegeneticinteraction_id);


-- Create bulk loads

INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
	SELECT nextval('bulkload_seq'), 'INTERACTION_GEN', 'Genetic Interaction Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Interaction Bulk Loads';
INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
	SELECT id, '0 0 22 ? * SUN-THU', false FROM bulkload WHERE backendbulkloadtype = 'INTERACTION_GEN';
INSERT INTO bulkfmsload (id, fmsdatatype, fmsdatasubtype)
	SELECT id, 'INTERACTION-GEN', 'COMBINED' FROM bulkload WHERE name = 'Genetic Interaction Load';