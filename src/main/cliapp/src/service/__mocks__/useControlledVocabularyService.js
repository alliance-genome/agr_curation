/**
 * Manual mock for editors backed by a controlled vocabulary.
 *
 * The real hook returns a flat array of a vocabulary's terms. Term names carry the
 * vocabulary label, so a column wired to the wrong vocabulary offers the wrong names. Both real
 * hooks return `undefined` until their query resolves; this mock has no loading state.
 */
const termsByLabel = new Map();
let nextId = 900001;

/**
 * The terms this mock returns for a vocabulary, the same array on every call.
 *
 * @param {string} vocabularyLabel
 * @returns {{id: number, name: string}[]} two terms, named `<label>_alpha` and `<label>_beta`
 */
export const vocabularyTermsFor = (vocabularyLabel) => {
	if (!termsByLabel.has(vocabularyLabel)) {
		termsByLabel.set(vocabularyLabel, [
			{ id: nextId++, name: `${vocabularyLabel}_alpha` },
			{ id: nextId++, name: `${vocabularyLabel}_beta` },
		]);
	}
	return termsByLabel.get(vocabularyLabel);
};

export const useControlledVocabularyService = (vocabularyLabel) => vocabularyTermsFor(vocabularyLabel);
