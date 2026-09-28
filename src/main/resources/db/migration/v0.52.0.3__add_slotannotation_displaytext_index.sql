-- SCRUM-6290: Speed up SGD Expression Atlas cross-reference loading.
-- GeneService.addExpressionAtlasXref looks up SGD genes by geneSymbol.displayText, which
-- generates an exact-match predicate (displaytext = ?) against the ~18M row slotannotation
-- table. The only displaytext indexes are on upper(displaytext) (functional/trigram), which
-- cannot serve an exact case-sensitive match, so every lookup did a parallel seq scan
-- (~935ms/lookup, run twice per record for the count+fetch). This btree serves the exact
-- match and turns the seq scan into an index scan.
CREATE INDEX IF NOT EXISTS slotannotation_displaytext_index
	ON public.slotannotation USING btree (displaytext);
