// "relation: gene symbol" for a targeted gene of the search results' preview, the gene identifier
// when the gene has no symbol.
export const targetedGeneText = (targetedGene) => {
	const gene = targetedGene?.geneSymbol || targetedGene?.geneIdentifier || '';
	return targetedGene?.relation ? `${targetedGene.relation}: ${gene}` : gene;
};

// The search results carry only the first few targeted genes; how many more there are.
export const moreTargetedGenesText = (targetedGeneCount, shown) => {
	const more = (targetedGeneCount || 0) - (shown || 0);
	return more > 0 ? `and ${more} more` : null;
};

// Restricts a gene association find to the associations of one STR.
export const targetedGenesFindParams = (sequenceTargetingReagentId) => ({
	'sequenceTargetingReagentAssociationSubject.id': sequenceTargetingReagentId,
});
