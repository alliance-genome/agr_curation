CREATE INDEX organization_createdby_index ON public.organization USING btree (createdby_id);
CREATE INDEX organization_updatedby_index ON public.organization USING btree (updatedby_id);
CREATE INDEX person_alliancemember_index ON public.person USING btree (alliancemember_id);
CREATE INDEX vocabularyterm_createdby_index ON public.vocabularyterm USING btree (createdby_id);
CREATE INDEX vocabularyterm_updatedby_index ON public.vocabularyterm USING btree (updatedby_id);
