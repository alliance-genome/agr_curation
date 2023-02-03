DROP INDEX idxcmlmyaq41oab54whjt5cglo8v;

CREATE INDEX crossreference_createdby_index ON crossreference USING btree (createdby_id);

CREATE INDEX crossreference_updatedby_index ON crossreference USING btree (updatedby_id);

CREATE INDEX crossreference_resourcedescriptorpage_index ON crossreference USING btree (resourcedescriptorpage_id);

CREATE INDEX dataprovider_createdby_index ON dataprovider USING btree (createdby_id);

CREATE INDEX dataprovider_updatedby_index ON dataprovider USING btree (updatedby_id);

CREATE INDEX dataprovider_crossreference_index ON dataprovider USING btree (crossreference_id);

CREATE INDEX dataprovider_sourceorganization_index ON dataprovider USING btree (sourceorganization_id);

CREATE INDEX ontologyterm_crossreference_ontologyterm_curie_index ON ontologyterm_crossreference USING btree (ontologyterm_curie);
