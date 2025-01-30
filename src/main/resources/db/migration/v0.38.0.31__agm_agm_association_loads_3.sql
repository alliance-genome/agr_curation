--alter TABLE agmagmassociation ADD COLUMN agmAgmAssociationObject_id bigint;
--alter TABLE agmagmassociation drop agmAssociationObject_id;

CREATE INDEX agmagmassociation_agmassociationsubject_index ON public.agmagmassociation USING btree (agmassociationsubject_id);
CREATE INDEX agmagmassociation_agmagmassocicationobject_index ON public.agmagmassociation USING btree (agmAgmAssociationObject_id);

drop index agmagmassociation_agmassocobject_in;
drop index agmagmassociation_agmassocsubject_in;

ALTER TABLE agmagmassociation ADD CONSTRAINT agmagmassociation_agmagmassociationobject_id_fk FOREIGN KEY (agmAgmAssociationObject_id) REFERENCES affectedgenomicmodel(id);
ALTER TABLE agmagmassociation ADD CONSTRAINT agmagmassociation_agmassociationsubject_id_fk FOREIGN KEY (agmAgmAssociationObject_id) REFERENCES affectedgenomicmodel(id);
