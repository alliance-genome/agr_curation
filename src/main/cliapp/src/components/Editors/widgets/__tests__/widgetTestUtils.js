import { fireEvent, screen } from '@testing-library/react';

/**
 * Helpers for the widget unit tests.
 *
 * Widgets take a plain { value, onChange } contract, so these tests need no
 * editorOptions harness — that is the point of the layer. Compare with
 * components/Editors/__tests__/editorTestUtils, which supplies the table
 * strategy the entity adapters resolve their rows and errors through.
 */

/** Open a PrimeReact Dropdown or MultiSelect and click an option by its visible label. */
export const pickOption = (container, optionLabel, { multi = false } = {}) => {
	fireEvent.click(container.querySelector(multi ? '.p-multiselect' : '.p-dropdown'));
	const selector = multi ? '.p-multiselect-item, .p-multiselect-item *' : '.p-dropdown-item, .p-dropdown-item *';
	fireEvent.click(screen.getByText(optionLabel, { selector }));
};

/** Type into whichever text control the widget rendered. */
export const typeInto = (container, value) =>
	fireEvent.change(container.querySelector('input, textarea'), { target: { value } });
