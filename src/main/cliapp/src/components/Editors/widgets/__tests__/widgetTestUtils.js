import { fireEvent } from '@testing-library/react';

/**
 * Helpers for the widget unit tests.
 *
 * Widgets take a plain { value, onChange } contract, so these tests need no
 * editorOptions harness — that is the point of the layer. Compare with
 * components/Editors/__tests__/editorTestUtils, which supplies the table
 * strategy the entity adapters resolve their rows and errors through.
 */

/** Type into whichever text control the widget rendered. */
export const typeInto = (container, value) =>
	fireEvent.change(container.querySelector('input, textarea'), { target: { value } });
