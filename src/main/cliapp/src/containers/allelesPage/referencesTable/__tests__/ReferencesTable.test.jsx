import { act, waitFor } from '@testing-library/react';
import { renderWithClient } from '../../../../tools/jest/utils';
import { clickDeleteInRow, getCellByHeader, renderSubResourceForm } from '../../__tests__/alleleFormTestUtils';
import { ReferencesForm } from '../ReferencesForm';
import { ReferencesForm as TransgenicToolReferencesForm } from '../../../transgenicToolsPage/detail/ReferencesForm';
import { useTransgenicToolReducer } from '../../../transgenicToolsPage/detail/useTransgenicToolReducer';

vi.mock('../../../../service/SearchService', () => ({
	SearchService: class {
		search = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
		find = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
	},
}));

const ERROR = { severity: 'error', message: 'this reference is bad' };
const FIRST_ERROR = { severity: 'error', message: 'first reference is bad' };

const reference = (id) => ({
	id,
	curie: `AGRKB:10100000000000${id}`,
	shortCitation: `Doe ${id}`,
	crossReferences: [{ referencedCurie: `PMID:${id}` }],
});

/** Renders the transgenic tool page's references section against its reducer, loaded with `references`. */
const renderTransgenicToolSection = (references) => {
	const handle = {};
	const Harness = () => {
		const { transgenicToolState, transgenicToolDispatch } = useTransgenicToolReducer();
		handle.state = transgenicToolState;
		handle.dispatch = transgenicToolDispatch;
		return <TransgenicToolReferencesForm state={transgenicToolState} dispatch={transgenicToolDispatch} />;
	};
	const result = renderWithClient(<Harness />);
	act(() => handle.dispatch({ type: 'SET', value: { id: 1, references } }));
	return {
		...result,
		getRows: () => handle.state.transgenicTool.references,
		setErrors: (errorMessages) =>
			act(() => handle.dispatch({ type: 'UPDATE_TABLE_ERROR_MESSAGES', entityType: 'references', errorMessages })),
	};
};

/**
 * The references table, on the allele pages (over the references sub-resource) and on the transgenic tool and
 * cassette pages (over the page reducer). Its rows are shown, not edited; a row's error shows under its curie.
 */
const SECTIONS = [
	{
		label: 'allele',
		render: (references) => {
			const result = renderSubResourceForm(ReferencesForm, 'references', references);
			return { ...result, setErrors: result.setErrorMessages };
		},
	},
	{ label: 'transgenic tool', render: renderTransgenicToolSection },
];

const bodyRows = (container) =>
	[...container.querySelectorAll('tbody tr')].filter((row) => row.querySelectorAll('td').length > 1);

describe.each(SECTIONS)('the $label references table', (section) => {
	const twoReferences = () => [reference(1), reference(2)];

	it('shows a reference error under the curie of the reference whose key it is stored under', () => {
		const { container, getRows, setErrors } = section.render(twoReferences());
		const secondRow = bodyRows(container).findIndex((row) => row.textContent.includes(getRows()[1].curie));

		setErrors({ [getRows()[1].dataKey]: { curie: ERROR } });

		expect(getCellByHeader(container, 'Curie', secondRow).textContent).toContain('this reference is bad');
		expect(getCellByHeader(container, 'Curie', 1 - secondRow).textContent).not.toContain('this reference is bad');
	});

	it("keeps a rejected save's error on its reference after the reference above it is deleted", async () => {
		const { container, getRows, setErrors } = section.render(twoReferences());
		const shownFirst = getRows().find((row) => bodyRows(container)[0].textContent.includes(row.curie));
		const remaining = getRows().find((row) => row !== shownFirst);
		setErrors({ [shownFirst.dataKey]: { curie: FIRST_ERROR }, [remaining.dataKey]: { curie: ERROR } });

		clickDeleteInRow(container, 0);
		await waitFor(() => expect(bodyRows(container)).toHaveLength(1));

		const remainingCell = getCellByHeader(container, 'Curie');
		expect(remainingCell.textContent).toContain(remaining.curie);
		expect(remainingCell.textContent).toContain('this reference is bad');
		expect(container.textContent).not.toContain('first reference is bad');
	});
});
