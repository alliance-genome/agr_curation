CREATE SEQUENCE public.association_seq            START WITH 200000000 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.bulkload_seq               START WITH 200000000 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.bulkloadfile_seq           START WITH 200000000 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.bulkloadfileexception_seq  START WITH 200000000 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.bulkloadfilehistory_seq    START WITH 200000000 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.bulkloadgroup_seq          START WITH 200000000 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.conditionrelation_seq      START WITH 200000000 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.crossreference_seq         START WITH 200000000 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.curationreport_seq         START WITH 200000000 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.curationreportgroup_seq    START WITH 200000000 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.curationreporthistory_seq  START WITH 200000000 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.dataprovider_seq           START WITH 200000000 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.experimentalcondition_seq  START WITH 200000000 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.genetogeneorthology_seq    START WITH 200000000 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.note_seq                   START WITH 200000000 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.organization_seq           START WITH 200000000 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.person_seq                 START WITH 200000000 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.personsetting_seq          START WITH 200000000 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.reagent_seq                START WITH 200000000 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.resourcedescriptor_seq     START WITH 200000000 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.resourcedescriptorpage_seq START WITH 200000000 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.revinfo_seq                START WITH 200000000 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.slotannotation_seq         START WITH 200000000 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.synonym_seq                START WITH 200000000 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.vocabulary_seq             START WITH 200000000 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.vocabularyterm_seq         START WITH 200000000 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;
CREATE SEQUENCE public.vocabularytermset_seq      START WITH 200000000 INCREMENT BY 50 NO MINVALUE NO MAXVALUE CACHE 1;

DROP SEQUENCE hibernate_sequence;

ALTER TABLE ONLY public.annotation                               ADD CONSTRAINT uk_5yecel87ntgvx62kmhgmlxw1y UNIQUE (modinternalid);
ALTER TABLE ONLY public.vocabulary                               ADD CONSTRAINT uk_avxkpwi6f4x6fhb7pvsi86pog UNIQUE (vocabularylabel);
ALTER TABLE ONLY public.reagent                                  ADD CONSTRAINT uk_eakmwvrtkqtnv0j6p5sacjith UNIQUE (modentityid);
ALTER TABLE ONLY public.reagent                                  ADD CONSTRAINT uk_f9mv5mtgjw7yhjj8c4985i649 UNIQUE (modinternalid);
ALTER TABLE ONLY public.vocabularytermset                        ADD CONSTRAINT uk_kovuuc5lh1hwyccs4iesimxst UNIQUE (vocabularylabel);
ALTER TABLE ONLY public.annotation                               ADD CONSTRAINT uk_q3qsfxfry01magx331btt1dg3 UNIQUE (modentityid);

CREATE INDEX allelegeneassociation_object_curie_index            ON public.allelegeneassociation USING btree (object_curie);
CREATE INDEX singlereferenceassociation_singlereference_index    ON public.singlereferenceassociation USING btree (singlereference_curie);
CREATE INDEX vocabulary_vocabularylabel_index                    ON public.vocabulary USING btree (vocabularylabel);
CREATE INDEX vocabularytermset_vocabularylabel_index             ON public.vocabularytermset USING btree (vocabularylabel);

ALTER TABLE ONLY public.allelegenomicentityassociation           ADD CONSTRAINT fk1qks8xk2i7ml0qnhgx8q6ieex FOREIGN KEY (id) REFERENCES public.evidenceassociation(id);
ALTER TABLE ONLY public.constructgenomicentityassociation        ADD CONSTRAINT fkgrhw9gxslaub14x4b0mc7v9mk FOREIGN KEY (id) REFERENCES public.evidenceassociation(id);
ALTER TABLE ONLY public.annotation_note                          ADD CONSTRAINT fks4im5g992bpgi6wa1rp9y8vil FOREIGN KEY (annotation_id) REFERENCES public.annotation(id);
