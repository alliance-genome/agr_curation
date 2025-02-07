--alter TABLE agmagmassociation ADD COLUMN agmAgmAssociationObject_id bigint;
--alter TABLE agmagmassociation drop agmAssociationObject_id;

CREATE INDEX AgmAgmAssociation_agmAssociationSubject_index ON public.agmagmassociation USING btree (agmassociationsubject_id);
CREATE INDEX AgmAgmAssociation_AgmAgmAssociationObject_index ON public.agmagmassociation USING btree (agmAgmAssociationObject_id);

drop index agmagmassociation_agmassocobject_in;
drop index agmagmassociation_agmassocsubject_in;

ALTER TABLE agmagmassociation DROP CONSTRAINT agmstrassociation_agmassocsubject_fk;
ALTER TABLE agmagmassociation DROP CONSTRAINT agmagmassociation_agmassociationobject_fk;
ALTER TABLE agmagmassociation DROP CONSTRAINT agmstrassociation_createdby_fk;
ALTER TABLE agmagmassociation DROP CONSTRAINT agmstrassociation_relation_fk;
ALTER TABLE agmagmassociation DROP CONSTRAINT agmstrassociation_updatedby_fk;

ALTER TABLE agmagmassociation ADD CONSTRAINT agmagmassociation_agmagmassociationobject_id_fk FOREIGN KEY (agmAgmAssociationObject_id) REFERENCES affectedgenomicmodel(id);
ALTER TABLE agmagmassociation ADD CONSTRAINT agmagmassociation_agmassociationsubject_id_fk FOREIGN KEY (agmassociationsubject_id) REFERENCES affectedgenomicmodel(id);

ALTER TABLE ONLY public.agmagmassociation ADD CONSTRAINT agmagmassociation_relation_fk FOREIGN KEY (relation_id) REFERENCES public.vocabularyterm(id);
ALTER TABLE ONLY public.agmagmassociation ADD CONSTRAINT agmagmassociation_updatedby_fk FOREIGN KEY (updatedby_id) REFERENCES public.person(id);
ALTER TABLE ONLY public.agmagmassociation ADD CONSTRAINT agmagmassociation_createdby_fk FOREIGN KEY (createdby_id) REFERENCES public.person(id);
