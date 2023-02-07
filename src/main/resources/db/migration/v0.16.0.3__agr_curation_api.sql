DROP INDEX slotannotation_informationcontententity_evidence_curie;

DROP INDEX idxpydgr8unpmiig9jnsm89f55br;

DROP INDEX idx2hm799bn7xal2pqoq6d4v8ik9;

CREATE INDEX ontologyterm_synonym_ontologyterm_curie_index ON ontologyterm_synonym USING btree (ontologyterm_curie);

CREATE INDEX crossreference_referencedcurie_index ON crossreference USING btree (referencedcurie);

CREATE INDEX resourcedescriptor_createdby_index ON resourcedescriptor USING btree (createdby_id);

CREATE INDEX resourcedescriptor_updatedby_index ON resourcedescriptor USING btree (updatedby_id);

CREATE INDEX resourcedescriptorpage_createdby_index ON resourcedescriptorpage USING btree (createdby_id);

CREATE INDEX resourcedescriptorpage_updatedby_index ON resourcedescriptorpage USING btree (updatedby_id);

CREATE INDEX organization_homepageresourcedescriptorpage_index ON organization USING btree (homepageresourcedescriptorpage_id);

CREATE INDEX vocabularyterm_vocabulary_id_index ON vocabularyterm USING btree (vocabulary_id);

CREATE INDEX vocabularytermset_name_index ON vocabularytermset USING btree (name);

CREATE INDEX vocabularytermset_createdby_id_index ON vocabularytermset USING btree (createdby_id);

CREATE INDEX vocabularytermset_updatedby_id_index ON vocabularytermset USING btree (updatedby_id);

CREATE INDEX vocabularytermset_vocabularytermsetvocabulary_id_index ON vocabularytermset USING btree (vocabularytermsetvocabulary_id);

CREATE INDEX vocabularytermset_vocabularyterm_vocabularytermsets_id_index ON vocabularytermset_vocabularyterm USING btree (vocabularytermsets_id);

CREATE INDEX vocabularytermset_vocabularyterm_memberterms_id_index ON vocabularytermset_vocabularyterm USING btree (memberterms_id);

