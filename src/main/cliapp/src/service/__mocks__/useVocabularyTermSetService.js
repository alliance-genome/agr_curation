/**
 * Manual mock for editors backed by a vocabulary term set. The real hook returns a flat array
 * of terms, or `undefined` until its query resolves; this mock has no loading state. Term names
 * carry the term set label, so a column wired to the wrong term set offers the wrong names.
 */
const termsByLabel = new Map();
let nextId = 900011;

/**
 * The terms this mock returns for a term set, the same array on every call.
 *
 * @param {string} vocabularyLabel
 * @returns {{id: number, name: string}[]} two terms, named `<label>_alpha` and `<label>_beta`
 */
export const termSetTermsFor = (vocabularyLabel) => {
	if (!termsByLabel.has(vocabularyLabel)) {
		termsByLabel.set(vocabularyLabel, [
			{ id: nextId++, name: `${vocabularyLabel}_alpha` },
			{ id: nextId++, name: `${vocabularyLabel}_beta` },
		]);
	}
	return termsByLabel.get(vocabularyLabel);
};

export const useVocabularyTermSetService = (vocabularyLabel) => termSetTermsFor(vocabularyLabel);
