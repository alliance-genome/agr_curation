-- Composite/covering indexes to optimize batch SQL queries for GeneSearchResultDocument assembly.
-- These replace inefficient seq scans and merge joins with index-only nested loop lookups.

-- Q12/Q11/Q13/Q9/Q10b: ontologytermclosure covering index for closure joins.
-- Enables index-only scan on (subject -> object) lookups, avoiding 9.9M row merge joins.
CREATE INDEX IF NOT EXISTS ontologytermclosure_subject_object_index
	ON public.ontologytermclosure USING btree (closuresubject_id, closureobject_id);

-- Q7: allelegeneassociation composite index for gene -> allele lookups.
-- Planner currently enters through relation_id and scans 1.8M rows; this lets it
-- enter through the gene object_id with relation filtering.
CREATE INDEX IF NOT EXISTS allelegeneassociation_object_relation_index
	ON public.allelegeneassociation USING btree (allelegeneassociationobject_id, relation_id)
	WHERE internal = false AND obsolete = false;

-- Q10a/Q10b: genetogeneorthology composite index for subject -> object lookups.
-- Enables index-only scan instead of seq scanning 4.4M rows.
CREATE INDEX IF NOT EXISTS genetogeneorthology_subject_object_index
	ON public.genetogeneorthology USING btree (subjectgene_id, objectgene_id);
