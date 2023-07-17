ALTER TABLE person ADD COLUMN apitoken character varying(255);
ALTER TABLE person ADD COLUMN oktaemail character varying(255);
ALTER TABLE person ADD COLUMN oktaid character varying(255);

ALTER TABLE person_aud ADD COLUMN apitoken character varying(255);
ALTER TABLE person_aud ADD COLUMN oktaemail character varying(255);
ALTER TABLE person_aud ADD COLUMN oktaid character varying(255);

ALTER TABLE ONLY person ADD CONSTRAINT uk_359y0d8lholv4k8x20nfqs6j3 UNIQUE (oktaid);
ALTER TABLE ONLY person ADD CONSTRAINT uk_hnv6wg06luqlb9amy4djbahan UNIQUE (oktaemail);

UPDATE person SET
apitoken = ( SELECT apitoken FROM loggedinperson WHERE id = person.id),
oktaemail = ( SELECT oktaemail FROM loggedinperson WHERE id = person.id),
oktaid = ( SELECT oktaid FROM loggedinperson WHERE id = person.id);

DROP table loggedinperson;
DROP table loggedinperson_aud;
