-- Need to drop index to allow hibernate to recreate it with new definition (as hibernate doesn't replace constraints by itself)
-- This change does require the application to be restarted after initial start-up (so hibernate would create the new constraint)!
ALTER TABLE "bulkloadfileexception"
	DROP CONSTRAINT IF EXISTS "fkgt2k1ohdyuodwu71mofkyplhy";