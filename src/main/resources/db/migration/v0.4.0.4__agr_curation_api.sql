-- Need to drop index to allow hibernate to recreate it with new definition (as hibernate doesn't replace constraints by itself)
-- This change does require the application to be restarted after initial start-up (so hibernate would create the new constraint)!
ALTER TABLE "bulkloadfilehistory"
	DROP CONSTRAINT IF EXISTS "fkk9bvfu4248kgyyrupeii7t6m0";