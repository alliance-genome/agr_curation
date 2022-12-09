CREATE INDEX synonym_createdby_index ON public.synonym USING btree (createdby_id);
CREATE INDEX synonym_updatedby_index ON public.synonym USING btree (updatedby_id);
CREATE INDEX allelemutationtype_singleallele_curie_index ON public.allelemutationtypeslotannotation USING btree (singleallele_curie);
CREATE INDEX genomicentity_crossreference_genomicentity_curie_index ON public.genomicentity_crossreference USING btree (genomicentity_curie);
CREATE INDEX genomicentity_crossreference_crossreferences_curie_index ON public.genomicentity_crossreference USING btree (crossreferences_curie);