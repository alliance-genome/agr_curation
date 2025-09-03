ALTER TABLE anatomicalsite
	ADD COLUMN anatomicalStructureUberonTermOther boolean DEFAULT false NOT NULL,
	ADD COLUMN anatomicalSubStructureUberonTermOther boolean DEFAULT false NOT NULL;