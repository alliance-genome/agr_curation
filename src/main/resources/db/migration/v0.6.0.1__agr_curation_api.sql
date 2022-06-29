ALTER TABLE reference
	ADD COLUMN IF NOT EXISTS displayxref varchar (255),
	ADD COLUMN IF NOT EXISTS title varchar (500);

ALTER TABLE reference
	ADD CONSTRAINT reference_displayxref_uk UNIQUE (displayxref);

ALTER TABLE reference_aud
	ADD COLUMN IF NOT EXISTS displayxref varchar (255),
	ADD COLUMN IF NOT EXISTS title varchar (500);
	
UPDATE reference SET displayxref = curie;

CREATE TABLE IF NOT EXISTS reference_crossreference (
	reference_curie varchar (255),
	crossreferences_curie varchar (255)
	);
	
CREATE TABLE IF NOT EXISTS reference_crossreference_aud (
	rev integer,
	reference_curie varchar (255),
	crossreferences_curie varchar (255),
	revtype smallint
	);
	