import { act, fireEvent, screen } from '@testing-library/react';
import { useAlleleReducer } from '../useAlleleReducer';
import { renderWithClient } from '../../../tools/jest/utils';

/**
 * Renders an allele subform against the real allele reducer, loaded with `allele` the way the
 * detail page loads it. The allele form tables have no row-save step, so the reducer state is
 * what the page submits; tests read it back to see what a curator's edit will save.
 *
 * @param {Function} Form - the subform component, taking `state` and `dispatch`
 * @param {object} allele - the allele to load, holding the subform's rows
 * @returns {object} the render result, plus `getState()` for the current reducer state and
 *   `dispatch(action)` to send the reducer an action from the test
 */
export const renderAlleleForm = (Form, allele) => {
	const handle = {};
	const Harness = () => {
		const { alleleState, alleleDispatch } = useAlleleReducer();
		handle.state = alleleState;
		handle.dispatch = alleleDispatch;
		return <Form state={alleleState} dispatch={alleleDispatch} />;
	};

	const result = renderWithClient(<Harness />);
	act(() => handle.dispatch({ type: 'SET', value: allele }));

	return {
		...result,
		getState: () => handle.state,
		dispatch: (action) => act(() => handle.dispatch(action)),
	};
};

// Required columns prefix their header with a marker.
const headerLabel = (header) => header.textContent.trim().replace(/^\*/, '');

/**
 * A body cell located by its column header text, ignoring a required-field marker. Column
 * position is stable where editor ordinals are not: several tables put a vocabulary dropdown
 * before the Internal one.
 *
 * @param {HTMLElement} container - the rendered table's container
 * @param {string} headerText - the column header
 * @param {number} [rowIndex=0] - the body row, counting from the top
 * @returns {HTMLElement} the cell
 */
export const getCellByHeader = (container, headerText, rowIndex = 0) => {
	const headers = [...container.querySelectorAll('thead tr:first-child th')];
	const columnIndex = headers.findIndex((header) => headerLabel(header) === headerText);
	if (columnIndex === -1) {
		throw new Error(
			`No column header "${headerText}". Saw: ${headers.map((header) => `"${headerLabel(header)}"`).join(', ')}`
		);
	}
	const row = container.querySelectorAll('tbody tr')[rowIndex];
	if (!row) throw new Error(`No body row at index ${rowIndex}`);
	const cell = row.querySelectorAll('td')[columnIndex];
	if (!cell) throw new Error(`Column "${headerText}" has no cell in row ${rowIndex}`);
	return cell;
};

/**
 * Picks a dropdown option by its label. The options panel is portalled to the document body,
 * hence the screen-level query.
 *
 * @param {HTMLElement} container - the rendered table's container
 * @param {string} headerText - the column header
 * @param {string} optionLabel - the option's visible label
 * @param {number} [rowIndex=0] - the body row, counting from the top
 */
export const selectInColumn = (container, headerText, optionLabel, rowIndex = 0) => {
	const dropdown = getCellByHeader(container, headerText, rowIndex).querySelector('.p-dropdown');
	if (!dropdown) throw new Error(`No dropdown in the "${headerText}" cell of row ${rowIndex}`);
	fireEvent.click(dropdown);
	fireEvent.click(screen.getByText(optionLabel, { selector: '.p-dropdown-item, .p-dropdown-item *' }));
};

/**
 * Clears a dropdown through its clear icon, which PrimeReact binds to pointer-up.
 *
 * @param {HTMLElement} container - the rendered table's container
 * @param {string} headerText - the column header
 * @param {number} [rowIndex=0] - the body row, counting from the top
 */
export const clearInColumn = (container, headerText, rowIndex = 0) => {
	const clearIcon = getCellByHeader(container, headerText, rowIndex).querySelector('.p-dropdown-clear-icon');
	if (!clearIcon) throw new Error(`No clear icon in the "${headerText}" cell of row ${rowIndex}`);
	fireEvent.pointerUp(clearIcon);
};

/**
 * Types into the text input or text area in a cell.
 *
 * @param {HTMLElement} container - the rendered table's container
 * @param {string} headerText - the column header
 * @param {string} value - the text to enter
 * @param {number} [rowIndex=0] - the body row, counting from the top
 */
export const typeInColumn = (container, headerText, value, rowIndex = 0) => {
	const input = getCellByHeader(container, headerText, rowIndex).querySelector('input, textarea');
	if (!input) throw new Error(`No text input in the "${headerText}" cell of row ${rowIndex}`);
	fireEvent.change(input, { target: { value } });
};

/**
 * Types into the autocomplete in a cell and clicks the suggestion matching `suggestionText`.
 * The search service must be mocked to offer it.
 *
 * @param {HTMLElement} container - the rendered table's container
 * @param {string} headerText - the column header
 * @param {string} query - the text to search for
 * @param {string|RegExp} suggestionText - matches the suggestion to pick
 * @param {number} [rowIndex=0] - the body row, counting from the top
 */
export const pickSuggestionInColumn = async (container, headerText, query, suggestionText, rowIndex = 0) => {
	typeInColumn(container, headerText, query, rowIndex);
	fireEvent.click(
		await screen.findByText(
			suggestionText,
			{ selector: '.p-autocomplete-item, .p-autocomplete-item *' },
			{ timeout: 3000 }
		)
	);
};

/**
 * Removes the first token from the multi-select autocomplete in a cell, which changes its value
 * without a search request.
 *
 * @param {HTMLElement} container - the rendered table's container
 * @param {string} headerText - the column header
 * @param {number} [rowIndex=0] - the body row, counting from the top
 */
export const removeFirstTokenInColumn = (container, headerText, rowIndex = 0) => {
	const removeIcon = getCellByHeader(container, headerText, rowIndex).querySelector('.p-autocomplete-token-icon');
	if (!removeIcon) throw new Error(`No token to remove in the "${headerText}" cell of row ${rowIndex}`);
	fireEvent.click(removeIcon);
};

/**
 * Clicks the delete button in a row's Actions cell.
 *
 * @param {HTMLElement} container - the rendered table's container
 * @param {number} [rowIndex=0] - the body row, counting from the top
 */
export const clickDeleteInRow = (container, rowIndex = 0) => {
	const button = getCellByHeader(container, 'Actions', rowIndex).querySelector('button');
	if (!button) throw new Error(`No delete button in row ${rowIndex}`);
	fireEvent.click(button);
};
