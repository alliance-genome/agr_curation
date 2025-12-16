-- Set allelegeneassociation records to internal=true where associated gene is internal=true
-- This affects 5,669 records across FB (4,745), WB (738), and MGI (186)

UPDATE allelegeneassociation aga
SET internal = true
FROM biologicalentity gene_be
WHERE gene_be.id = aga.allelegeneassociationobject_id
  AND gene_be.internal = true
  AND aga.internal = false;
