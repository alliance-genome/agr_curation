-- Revert temporary changes to association table columns

ALTER TABLE constructgenomicentityassociation RENAME subjectconstruct_id TO subject_id;
ALTER TABLE constructgenomicentityassociation_aud RENAME subjectconstruct_id TO subject_id;

ALTER TABLE constructgenomicentityassociation RENAME objectgenomicentity_curie TO object_curie;
ALTER TABLE constructgenomicentityassociation_aud RENAME objectgenomicentity_curie TO object_curie;

ALTER TABLE allelegeneassociation RENAME objectgene_curie TO object_curie;
ALTER TABLE allelegeneassociation_aud RENAME objectgene_curie TO object_curie;

-- Random cleanup

ALTER INDEX gene_taxon_index RENAME TO gene_genetype_index;

ALTER TABLE organization DROP COLUMN uniqueid;
ALTER TABLE organization_aud DROP COLUMN uniqueid;