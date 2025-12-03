-- Create join table for many-to-many relationship between AnatomicalSite and GOTerm for cellular component ribbon terms
CREATE TABLE anatomicalsite_cellularcomponentribbonterms (
	anatomicalsite_id BIGINT,
	cellularcomponentribbonterms_id BIGINT
);

-- Add foreign key constraints
ALTER TABLE anatomicalsite_cellularcomponentribbonterms
	ADD CONSTRAINT cellularcomponentribbonterms_anatomicalsite_fk
		FOREIGN KEY (anatomicalsite_id) REFERENCES anatomicalsite(id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

ALTER TABLE anatomicalsite_cellularcomponentribbonterms
	ADD CONSTRAINT cellularcomponentribbonterms_goterm_fk
		FOREIGN KEY (cellularcomponentribbonterms_id) REFERENCES ontologyterm(id) MATCH SIMPLE ON UPDATE NO ACTION ON DELETE NO ACTION;

-- Create indexes
CREATE INDEX cellularcomponentribbonterms_anatomicalsite_index ON anatomicalsite_cellularcomponentribbonterms USING btree (anatomicalsite_id);
CREATE INDEX cellularcomponentribbonterms_ribbonterms_index ON anatomicalsite_cellularcomponentribbonterms USING btree (cellularcomponentribbonterms_id);

ALTER TABLE anatomicalsite_cellularcomponentribbonterms
	ADD CONSTRAINT anatomicalsite_cellularcomponentribbonterms_unique
	UNIQUE (anatomicalsite_id, cellularcomponentribbonterms_id);

-- Drop the old foreign key constraint
ALTER TABLE anatomicalsite DROP CONSTRAINT IF EXISTS fkcuqc7qacirmg4wqcwuou8abjn;

-- Drop the old column
ALTER TABLE anatomicalsite DROP COLUMN IF EXISTS cellularcomponentribbonterm_id;
