-- Need to drop index to allow hibernate to recreate it with new definition (as hibernate doesn't replace constraints by itself)
-- This change does require the application to be restarted after initial start-up (so hibernate would create the new constraint)!
ALTER TABLE "genediseaseannotation"
	DROP CONSTRAINT "fk8xs26m9hfc38nmy7gvu3cec3t";
ALTER TABLE "agmdiseaseannotation"
	DROP CONSTRAINT "fklvr4o1waqclvbktjmyg6x25ls";
