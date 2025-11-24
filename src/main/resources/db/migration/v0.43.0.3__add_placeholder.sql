ALTER TABLE reagent
	ADD COLUMN IF NOT EXISTS placeholder boolean;

UPDATE reagent
SET placeholder = true
FROM construct c, organization org
WHERE reagent.id = c.id
  AND reagent.dataprovider_id = org.id
  AND org.abbreviation = 'MGI';

