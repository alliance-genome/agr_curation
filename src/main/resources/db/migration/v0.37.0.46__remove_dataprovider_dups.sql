DELETE FROM crossreference cr USING crossreference_ids_to_delete cd WHERE cr.id = cd.id;

DROP TABLE dataprovider_ids_to_keep;
DROP TABLE crossreference_ids_to_delete;
DROP TABLE dataprovider_ids_to_delete;
