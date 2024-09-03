export const conditionsSort = (conditions) => {
    return conditions.toSorted((a, b) => a.conditionSummary > b.conditionSummary ? 1 : -1);
}