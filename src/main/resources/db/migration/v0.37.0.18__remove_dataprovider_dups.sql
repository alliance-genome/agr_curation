UPDATE annotation an
SET dataprovider_id = (
   SELECT dp1.id
   FROM dataprovider dp1, organization o1, crossreference cr1
   WHERE o1.id = dp1.sourceorganization_id
   AND dp1.crossreference_id = cr1.id
   AND o1.abbreviation = 'XB'
   AND cr1.referencedCurie = 'XB'
   ORDER BY dp1.id ASC LIMIT 1
)
FROM dataprovider dp, organization o, crossreference cr
WHERE
   an.dataprovider_id=dp.id AND
   dp.sourceorganization_id=o.id AND
   dp.crossreference_id = cr.id AND
   o.abbreviation = cr.referencedCurie AND
   o.abbreviation = 'XB';

