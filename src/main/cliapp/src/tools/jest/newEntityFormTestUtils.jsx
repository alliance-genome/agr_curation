import { fireEvent, screen } from '@testing-library/react';

/**
 * A text matcher for a field's label: a `<label>` or a heading, ignoring a required-field marker.
 *
 * @param {string} labelText - the label's text
 * @returns {(content: string, element: HTMLElement) => boolean}
 */
const isFieldLabel = (labelText) => (content, element) =>
	['LABEL', 'H2'].includes(element?.tagName) && element.textContent.replace('*', '').trim() === labelText;

/**
 * A New Entity dialog field located by its label, ignoring a required-field marker: the smallest element holding
 * both the label and the field's control and messages.
 *
 * @param {string} labelText - the field's label
 * @returns {HTMLElement}
 */
export const getField = (labelText) => {
	const label = screen.getByText(isFieldLabel(labelText));
	const field = label.closest('.field, .grid');
	if (!field) throw new Error(`No field around the "${labelText}" label`);
	return field;
};

/**
 * A field's text input or text area, located by its label.
 *
 * @param {string} labelText - the field's label
 * @returns {HTMLElement}
 */
const controlIn = (labelText) => {
	const control = getField(labelText).querySelector('input, textarea');
	if (!control) throw new Error(`No text control in the "${labelText}" field`);
	return control;
};

/**
 * Types into a field's text input, text area or autocomplete.
 *
 * @param {string} labelText - the field's label
 * @param {string} value - the text to enter
 */
export const typeInField = (labelText, value) => fireEvent.change(controlIn(labelText), { target: { value } });

/**
 * Types into a field's autocomplete and clicks the suggestion matching `suggestionText`. The search service must
 * be mocked to offer it.
 *
 * @param {string} labelText - the field's label
 * @param {string} query - the text to search for
 * @param {string|RegExp} suggestionText - matches the suggestion to pick
 */
export const pickSuggestionInField = async (labelText, query, suggestionText) => {
	typeInField(labelText, query);
	fireEvent.click(
		await screen.findByText(
			suggestionText,
			{ selector: '.p-autocomplete-item, .p-autocomplete-item *' },
			{ timeout: 3000 }
		)
	);
};

/**
 * Picks a field's dropdown option by its label. The options panel is portalled to the document body.
 *
 * @param {string} labelText - the field's label
 * @param {string} optionLabel - the option's visible label
 */
export const selectInField = (labelText, optionLabel) => {
	const dropdown = getField(labelText).querySelector('.p-dropdown');
	if (!dropdown) throw new Error(`No dropdown in the "${labelText}" field`);
	fireEvent.click(dropdown);
	fireEvent.click(screen.getByText(optionLabel, { selector: '.p-dropdown-item, .p-dropdown-item *' }));
};

/** Clicks the dialog's Save button. */
export const clickSave = () => fireEvent.click(screen.getByRole('button', { name: 'Save' }));

/**
 * A rejected create, shaped as the API answers a failed validation.
 *
 * @param {object} errorMessages - messages keyed by field
 * @param {string} [errorMessage] - the summary message
 * @returns {Promise<never>}
 */
export const rejectedCreate = (errorMessages, errorMessage = 'Validation failed') =>
	Promise.reject({ response: { data: { errorMessage, errorMessages } } });

/**
 * A successful create, shaped as the API answers one.
 *
 * @param {object} entity - the created entity
 * @returns {Promise<object>}
 */
export const resolvedCreate = (entity) => Promise.resolve({ data: { entity } });
