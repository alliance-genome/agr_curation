ALTER TABLE variant ADD COLUMN uniqueid TEXT;

CREATE INDEX variant_uniqueid_index ON variant USING btree (uniqueid);
