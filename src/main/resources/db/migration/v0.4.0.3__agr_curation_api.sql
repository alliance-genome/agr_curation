-- Need to drop index to allow hibernate to recreate it with new definition (as hibernate doesn't replace constraints by itself)
-- This change does require the application to be restarted after initial start-up (so hibernate would create the new constraint)!
ALTER TABLE "allelediseaseannotation"
	DROP CONSTRAINT "fk3unb0kaxocbodllqe35hu4w0c";
	
ALTER TABLE "agmdiseaseannotation"
	DROP CONSTRAINT "fkp1rktcpoyvnr2f756ncdb8k24";
	
ALTER TABLE "genediseaseannotation"
	DROP CONSTRAINT "fk3j5deigrhrwln0srh51vtw3m8";