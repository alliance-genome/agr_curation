CREATE INDEX bulkload_backendbulkloadtype_index ON public.bulkload USING btree (backendbulkloadtype);
CREATE INDEX bulkload_bulkloadstatus_index ON public.bulkload USING btree (bulkloadstatus);
CREATE INDEX bulkload_createdby_index ON public.bulkload USING btree (createdby_id);
CREATE INDEX bulkload_group_index ON public.bulkload USING btree (group_id);
CREATE INDEX bulkload_ontologytype_index ON public.bulkload USING btree (ontologytype);
CREATE INDEX bulkload_updatedby_index ON public.bulkload USING btree (updatedby_id);
CREATE INDEX bulkloadfile_bulkload_index ON public.bulkloadfile USING btree (bulkload_id);
CREATE INDEX bulkloadfile_bulkloadstatus_index ON public.bulkloadfile USING btree (bulkloadstatus);
CREATE INDEX bulkloadfile_createdby_index ON public.bulkloadfile USING btree (createdby_id);
CREATE INDEX bulkloadfile_updatedby_index ON public.bulkloadfile USING btree (updatedby_id);
CREATE INDEX bulkloadfileexception_bulkloadfilehistory_index ON public.bulkloadfileexception USING btree (bulkloadfilehistory_id);
CREATE INDEX bulkloadfileexception_createdby_index ON public.bulkloadfileexception USING btree (createdby_id);
CREATE INDEX bulkloadfileexception_updatedby_index ON public.bulkloadfileexception USING btree (updatedby_id);
CREATE INDEX bulkloadfilehistory_bulkloadfile_index ON public.bulkloadfilehistory USING btree (bulkloadfile_id);
CREATE INDEX bulkloadfilehistory_createdby_index ON public.bulkloadfilehistory USING btree (createdby_id);
CREATE INDEX bulkloadfilehistory_updatedby_index ON public.bulkloadfilehistory USING btree (updatedby_id);

ALTER TABLE bulkloadfilehistory
	ADD COLUMN errorrate double precision;
