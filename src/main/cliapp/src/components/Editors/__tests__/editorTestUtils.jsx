import { render } from '@testing-library/react';
import { TableEditorProvider } from '../fields/TableEditorContext';
import { usePrimeRowEditStrategy } from '../fields/strategies/usePrimeRowEditStrategy';

export const makeEditorOptions = (rowData, rowIndex = 0) => ({
	rowData,
	rowIndex,
	editorCallback: vi.fn(),
});

export const emptyErrorMessagesRef = { current: {} };

/**
 * Renders a table editor under the strategy a main table supplies, which is where
 * it gets its row addressing and its error maps.
 *
 * @param {React.ReactNode} editor - the editor under test
 * @param {object} [errorMessages] - server errors, keyed `{[rowIndex]: {[field]: error}}`
 * @param {object} [uiErrorMessages] - client-side errors in the same shape
 * @returns {object} the testing-library render result
 */
export const renderInTable = (editor, { errorMessages = {}, uiErrorMessages } = {}) => {
	// The inner component is required, not incidental: react-hooks/rules-of-hooks
	// rejects a use* call in a plain function, even one that holds no hooks.
	const Harness = () => (
		<TableEditorProvider strategy={usePrimeRowEditStrategy({ errorMessages, uiErrorMessages })}>
			{editor}
		</TableEditorProvider>
	);
	return render(<Harness />);
};
