import { fireEvent, screen } from '@testing-library/react';

/**
 * Helpers for the widget unit tests.
 *
 * Widgets take a plain { value, onChange } contract, so these tests need no
 * editorOptions harness — that is the point of the layer. Compare with
 * components/Editors/__tests__/editorTestUtils, which supplies the table
 * strategy the entity adapters resolve their rows and errors through.
 */

/** Open a PrimeReact Dropdown and click an option by its visible label. */
export const pickOption = (container, optionLabel) => {
	fireEvent.click(container.querySelector('.p-dropdown'));
	fireEvent.click(screen.getByText(optionLabel, { selector: '.p-dropdown-item, .p-dropdown-item *' }));
};

/** Type into whichever text control the widget rendered. */
export const typeInto = (container, value) =>
	fireEvent.change(container.querySelector('input, textarea'), { target: { value } });
