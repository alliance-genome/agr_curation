UPDATE bulkscheduledload SET cronschedule = '0 10 22 ? * SUN-THU' WHERE id = (SELECT id FROM bulkload WHERE name = 'FB GEO CrossReference');
UPDATE bulkscheduledload SET cronschedule = '0 11 22 ? * SUN-THU' WHERE id = (SELECT id FROM bulkload WHERE name = 'HUMAN GEO CrossReference');
UPDATE bulkscheduledload SET cronschedule = '0 12 22 ? * SUN-THU' WHERE id = (SELECT id FROM bulkload WHERE name = 'MGI GEO CrossReference');
UPDATE bulkscheduledload SET cronschedule = '0 13 22 ? * SUN-THU' WHERE id = (SELECT id FROM bulkload WHERE name = 'RGD GEO CrossReference');
UPDATE bulkscheduledload SET cronschedule = '0 14 22 ? * SUN-THU' WHERE id = (SELECT id FROM bulkload WHERE name = 'SGD GEO CrossReference');
UPDATE bulkscheduledload SET cronschedule = '0 15 22 ? * SUN-THU' WHERE id = (SELECT id FROM bulkload WHERE name = 'WB GEO CrossReference');
UPDATE bulkscheduledload SET cronschedule = '0 16 22 ? * SUN-THU' WHERE id = (SELECT id FROM bulkload WHERE name = 'ZFIN GEO CrossReference');