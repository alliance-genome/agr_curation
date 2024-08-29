DELETE FROM crossreference cr USING crossreference_ids_to_delete cd WHERE cr.id = cd.id;

DROP TABLE dataprovider_ids_to_keep;
DROP TABLE crossreference_ids_to_delete;
DROP TABLE dataprovider_ids_to_delete;

-- Migration to switch bulk load file and history around

ALTER TABLE bulkloadfilehistory ADD COLUMN bulkload_id bigint;
ALTER TABLE bulkloadfilehistory ADD COLUMN errormessage text;
ALTER TABLE bulkloadfilehistory ADD COLUMN bulkloadstatus character varying(255);
ALTER TABLE bulkloadfilehistory ADD CONSTRAINT bulkloadfilehistory_bulkload_fk FOREIGN KEY (bulkload_id) REFERENCES bulkload(id);

CREATE INDEX bulkloadfilehistory_bulkloadstatus_index ON bulkloadfilehistory USING btree (bulkloadstatus);
CREATE INDEX bulkloadfilehistory_bulkload_index ON bulkloadfilehistory USING btree (bulkload_id);
CREATE INDEX bulkloadfile_md5sum_index ON bulkloadfile USING btree (md5sum);

UPDATE bulkloadfilehistory bh
SET bulkload_id = bf.bulkload_id
FROM bulkloadfile bf
WHERE
   bf.id = bh.bulkloadfile_id;

UPDATE bulkloadfilehistory bh
SET errormessage = bf.errormessage
FROM bulkloadfile bf
WHERE
   bf.id = bh.bulkloadfile_id;

UPDATE bulkloadfilehistory bh
SET bulkloadstatus = bf.bulkloadstatus
FROM bulkloadfile bf
WHERE
   bf.id = bh.bulkloadfile_id;

DELETE from bulkloadfilehistory where bulkloadfile_id is null;

ALTER TABLE bulkloadfile DROP COLUMN bulkload_id;
ALTER TABLE bulkloadfile DROP COLUMN errorMessage;
ALTER TABLE bulkloadfile DROP COLUMN bulkloadStatus;

