import { fireEvent, screen } from '@testing-library/react';

/**
 * A detail page field's row, located by its heading. The heading is the label curators see, so
 * the row is found the same way whichever editor renders inside it.
 *
 * @param {string} label - the field's heading, without its required marker
 * @returns {HTMLElement} the row holding the heading, the editor and its messages
 */
export const fieldRow = (label) => {
	const heading = screen.getByRole('heading', { name: label });
	const row = heading.closest('.grid');
	if (!row) throw new Error(`No field row around the "${label}" heading`);
	return row;
};

/**
 * The text input in a field's row.
 *
 * @param {string} label - the field's heading
 * @returns {HTMLInputElement}
 */
export const fieldInput = (label) => {
	const input = fieldRow(label).querySelector('input');
	if (!input) throw new Error(`No input in the "${label}" field`);
	return input;
};

/**
 * Types into a field's input.
 *
 * @param {string} label - the field's heading
 * @param {string} value - the text to enter
 */
export const typeInField = (label, value) => {
	fireEvent.change(fieldInput(label), { target: { value } });
};

/**
 * Types into a field's autocomplete and clicks the suggestion matching `suggestionText`. The
 * search service must be mocked to offer it.
 *
 * @param {string} label - the field's heading
 * @param {string} query - the text to search for
 * @param {string|RegExp} suggestionText - matches the suggestion to pick
 */
export const pickSuggestionInField = async (label, query, suggestionText) => {
	typeInField(label, query);
	fireEvent.click(
		await screen.findByText(
			suggestionText,
			{ selector: '.p-autocomplete-item, .p-autocomplete-item *' },
			{ timeout: 3000 }
		)
	);
};

/**
 * Opens a field's dropdown and picks an option by its label. The options panel is portalled to
 * the document body, hence the screen-level query.
 *
 * @param {string} label - the field's heading
 * @param {string} optionLabel - the option's visible label
 */
export const selectInField = (label, optionLabel) => {
	const dropdown = fieldRow(label).querySelector('.p-dropdown');
	if (!dropdown) throw new Error(`No dropdown in the "${label}" field`);
	fireEvent.click(dropdown);
	fireEvent.click(screen.getByText(optionLabel, { selector: '.p-dropdown-item, .p-dropdown-item *' }));
};
