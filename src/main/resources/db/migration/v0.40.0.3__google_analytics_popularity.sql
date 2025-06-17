
ALTER TABLE allele ADD COLUMN popularity double precision;
ALTER TABLE gene ADD COLUMN popularity double precision;
ALTER TABLE OntologyTerm ADD COLUMN popularity double precision;

CREATE TABLE bulkgoogleanalyticsload (
    id bigint NOT NULL
);

ALTER TABLE ONLY bulkgoogleanalyticsload
    ADD CONSTRAINT bulkgoogleanalyticsload_pkey PRIMARY KEY (id);

ALTER TABLE ONLY bulkgoogleanalyticsload
    ADD CONSTRAINT bulkgoogleanalyticsload_id_fk FOREIGN KEY (id) REFERENCES public.bulkscheduledload(id);

INSERT INTO bulkload (id, backendbulkloadtype, name, bulkloadstatus, group_id)
SELECT nextval('bulkload_seq'), 'GOOGLE_ANALYTICS', 'GA Load', 'STOPPED', id FROM bulkloadgroup WHERE name = 'Resource Descriptor Load';

INSERT INTO bulkscheduledload (id, cronschedule, scheduleactive)
SELECT id, '0 0 22 ? * SUN-THU', false FROM bulkload WHERE backendbulkloadtype = 'GOOGLE_ANALYTICS';

INSERT INTO bulkgoogleanalyticsload (id)
SELECT id FROM bulkload WHERE name = 'GA Load';