export const conditionsSort = (conditions) => {
    return conditions.toSorted((a, b) => a.conditionSummary > b.conditionSummary ? 1 : -1);
}

export const crossReferencesSort = (crossReferences) =>{
    return crossReferences.sort((a, b) => a.displayName > b.displayName ? 1 : a.resourceDescriptorPage.name === b.resourceDescriptorPage.name ? 1 : -1 );
} 