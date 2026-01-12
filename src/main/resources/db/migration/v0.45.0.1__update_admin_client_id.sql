ALTER TABLE person ADD COLUMN IF NOT EXISTS authid VARCHAR(255);
ALTER TABLE person ADD COLUMN IF NOT EXISTS authemail VARCHAR(255);

UPDATE person SET authemail = oktaemail WHERE oktaemail IS NOT NULL;

ALTER TABLE person DROP CONSTRAINT IF EXISTS person_authid_uk;
ALTER TABLE person ADD CONSTRAINT person_authid_uk UNIQUE (authid);

ALTER TABLE person DROP CONSTRAINT IF EXISTS person_authemail_uk;
ALTER TABLE person ADD CONSTRAINT person_authemail_uk UNIQUE (authemail);

update person set authId = '6umhkcuinvjdlmtp5841ites7g' where authemail = 'admin@alliancegenome.org';

