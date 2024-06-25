CREATE TABLE sequencetargetingreagentgeneassociation (
    id bigint NOT NULL,
    relation_id bigint,
    sequencetargetingreagentassociationsubject_id bigint,
    sequencetargetingreagentgeneassociationobject_id bigint
);


ALTER TABLE ONLY sequencetargetingreagentgeneassociation ADD CONSTRAINT sequencetargetingreagentgeneassociation_pkey PRIMARY KEY (id);

CREATE INDEX sequencetargetingreagentassociation_subject_index 
	ON sequencetargetingreagentgeneassociation USING btree (sequencetargetingreagentassociationsubject_id);

CREATE INDEX sequencetargetingreagentgeneassociation_relation_index 
	ON sequencetargetingreagentgeneassociation USING btree (relation_id);

CREATE INDEX sequencetargetingreagentgeneassociation_sequencetargetingreagentgeneassociationobject_index 
	ON sequencetargetingreagentgeneassociation USING btree (sequencetargetingreagentgeneassociationobject_id);

ALTER TABLE ONLY sequencetargetingreagentgeneassociation
    ADD CONSTRAINT fk4oywb4vxq460mph6qqfucrklc FOREIGN KEY (sequencetargetingreagentassociationsubject_id) REFERENCES sequencetargetingreagent(id);

ALTER TABLE ONLY sequencetargetingreagentgeneassociation
    ADD CONSTRAINT fk7h8rchplqiu7rji3t751d16fb FOREIGN KEY (sequencetargetingreagentgeneassociationobject_id) REFERENCES gene(id);

ALTER TABLE ONLY sequencetargetingreagentgeneassociation
    ADD CONSTRAINT fkc3s4amylq8kw9109g0608v516 FOREIGN KEY (id) REFERENCES evidenceassociation(id);

ALTER TABLE ONLY sequencetargetingreagentgeneassociation
    ADD CONSTRAINT fkf34iyy9myqed615lvm21exrp FOREIGN KEY (relation_id) REFERENCES vocabularyterm(id);