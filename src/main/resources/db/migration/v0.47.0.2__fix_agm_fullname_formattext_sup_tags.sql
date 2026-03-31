-- Fix formattext containing HTML <sup>/<​/sup> tags across all slot annotation types.
-- MGI and ZFIN submitted formattext identical to displaytext (with HTML superscript markup).
-- formattext should use plain angle brackets: Alx4<lst> not Alx4<sup>lst</sup>
-- Affects ~206,015 records: AgmFullName (123,548), AlleleSynonym (76,805),
-- ConstructSymbol (5,631), GeneSynonym (26), AlleleSymbol (4), GeneSymbol (1)

UPDATE slotannotation
SET formattext = REPLACE(REPLACE(formattext, '<sup>', '<'), '</sup>', '>')
WHERE formattext LIKE '%<sup>%';
