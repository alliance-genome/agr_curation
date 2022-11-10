-- Need to drop index to allow hibernate to recreate it with new definition (as hibernate doesn't replace constraints by itself)
-- This change does require the application to be restarted after initial start-up (so hibernate would create the new constraint)!
ALTER TABLE "allelediseaseannotation"
	DROP CONSTRAINT "fkerk9wpvk1ka4pkm0t1dqyyeyl";