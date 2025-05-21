DROP TABLE ontologyterm_isa_ancestor_descendant;
DROP TABLE ontologyterm_isa_parent_children;

CREATE TABLE ontologytermclosure (
    id bigint NOT NULL,
    closuretypes jsonb,
    distance integer,
    closureobject_id bigint,
    closuresubject_id bigint,
    datecreated timestamp(6) with time zone,
    dateupdated timestamp(6) with time zone,
    dbdatecreated timestamp(6) with time zone,
    dbdateupdated timestamp(6) with time zone,
    internal boolean DEFAULT false NOT NULL,
    obsolete boolean DEFAULT false NOT NULL,
    createdby_id bigint,
    updatedby_id bigint
);

ALTER TABLE ONLY ontologytermclosure
    ADD CONSTRAINT ontologytermclosure_pkey PRIMARY KEY (id);

CREATE SEQUENCE ontologytermclosure_seq
    START WITH 1
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE INDEX ontologyclosure_closureobject_index ON ontologytermclosure USING btree (closureobject_id);
CREATE INDEX ontologyclosure_closuresubject_index ON ontologytermclosure USING btree (closuresubject_id);
CREATE INDEX ontologyclosure_createdby_index ON ontologytermclosure USING btree (createdby_id);
CREATE INDEX ontologyclosure_updatedby_index ON ontologytermclosure USING btree (updatedby_id);

ALTER TABLE ONLY ontologytermclosure
    ADD CONSTRAINT fk85i2g74lt0c1g2s6toebn317e FOREIGN KEY (createdby_id) REFERENCES person(id);
ALTER TABLE ONLY ontologytermclosure
    ADD CONSTRAINT fkcbqu78go4qwwh73un8ugptrrg FOREIGN KEY (closureobject_id) REFERENCES ontologyterm(id);
ALTER TABLE ONLY ontologytermclosure
    ADD CONSTRAINT fkfrnnkwg88xnwaalyoblqxd98j FOREIGN KEY (closuresubject_id) REFERENCES ontologyterm(id);
ALTER TABLE ONLY ontologytermclosure
    ADD CONSTRAINT fkk1ie53yfwpg8x9c83kpsbkabj FOREIGN KEY (updatedby_id) REFERENCES person(id);

