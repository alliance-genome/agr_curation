DROP INDEX IF EXISTS idx_allele_id_btree;
DROP INDEX IF EXISTS cellularcomponentqualifiers_qualifiers_index;
DROP INDEX IF EXISTS idxk04don501qvjx3gsreq6i4eo;
DROP INDEX IF EXISTS idxk1mxuc2w5565d95qkk9uk2kdh;
DROP INDEX IF EXISTS idx_genetogeneorthologygenerated_confidence;
DROP INDEX IF EXISTS idx_g2gorthgen_predmethods_ortho;
DROP INDEX IF EXISTS resourcedescriptorpage_createdby_id_index;
DROP INDEX IF EXISTS resourcedescriptorpage_updatedby_id_index;
DROP INDEX IF EXISTS idxknjhcn64qms05eq8c8s2hhmxc;

ALTER TABLE ONLY resourcedescriptor DROP CONSTRAINT IF EXISTS resourcedescriptor_prefix_key;
ALTER TABLE ONLY organization DROP CONSTRAINT IF EXISTS organization_abbreviation_key;
