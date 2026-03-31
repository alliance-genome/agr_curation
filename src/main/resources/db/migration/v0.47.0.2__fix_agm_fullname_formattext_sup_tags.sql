-- Fix AGM full name formattext containing HTML <sup>/<​/sup> tags
-- MGI and ZFIN submitted formattext identical to displaytext (with HTML superscript markup).
-- formattext should use plain angle brackets: Alx4<lst> not Alx4<sup>lst</sup>
-- Affects ~96,905 MGI and ~26,643 ZFIN AGM records (123,548 total)

UPDATE slotannotation
SET formattext = REPLACE(REPLACE(formattext, '<sup>', '<'), '</sup>', '>')
WHERE slotannotationtype = 'AgmFullNameSlotAnnotation'
	AND formattext LIKE '%<sup>%';
