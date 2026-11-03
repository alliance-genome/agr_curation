-- SCRUM-6537: placeholder was added in v0.43.0.3 as a plain nullable column, while Reagent declares
-- it "boolean default false" and not null. No load set it, so every reagent other than the MGI
-- constructs flagged by that migration holds NULL. Loads now set it when a submission sends it
-- and leave it alone otherwise; bring the column in line with the entity so NULL no longer
-- stands for false.

UPDATE reagent SET placeholder = false WHERE placeholder IS NULL;

ALTER TABLE reagent ALTER COLUMN placeholder SET DEFAULT false;
ALTER TABLE reagent ALTER COLUMN placeholder SET NOT NULL;
