import { act, fireEvent, screen, waitFor } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';
import {
	clickDeleteInRow,
	getCellByHeader,
	pickSuggestionInColumn,
	selectInColumn,
	typeInColumn,
} from '../../../containers/allelesPage/__tests__/alleleFormTestUtils';
import { vocabularyTermsFor } from '../../../service/__mocks__/useControlledVocabularyService';
import { termSetTermsFor } from '../../../service/__mocks__/useVocabularyTermSetService';
import { processErrors } from '../../../utils/processErrors';

const EVIDENCE = { id: 1, curie: 'AGRKB:101' };
const NEW_EVIDENCE = { id: 3, curie: 'AGRKB:103' };

vi.mock('../../../service/SearchService', () => ({
	SearchService: class {
		search = vi.fn(() => Promise.resolve({ results: [NEW_EVIDENCE], totalResults: 1 }));
		find = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
	},
}));
vi.mock('../../../service/useControlledVocabularyService');
vi.mock('../../../service/useVocabularyTermSetService');

const { GeneForm } = await import('../../../containers/genesPage/GeneForm');
const { useGeneReducer } = await import('../../../containers/genesPage/useGeneReducer');
const { useGeneSubResources } = await import('../../../containers/genesPage/useGeneSubResources');
const { AlleleForm } = await import('../../../containers/allelesPage/AlleleForm');
const { useAlleleReducer } = await import('../../../containers/allelesPage/useAlleleReducer');
const { useAlleleSubResources } = await import('../../../containers/allelesPage/useAlleleSubResources');
const { AGMForm } = await import('../../../containers/affectedGenomicModelPage/AGMForm');
const { useAGMReducer } = await import('../../../containers/affectedGenomicModelPage/useAGMReducer');
const { SubResourcesProvider } = await import('../../SubResourcesContext');

const name = (displayText, nameTypeSet) => ({
	id: 11,
	displayText,
	formatText: `${displayText} format`,
	synonymScope: vocabularyTermsFor('synonym_scope')[0],
	nameType: termSetTermsFor(nameTypeSet)[0],
	synonymUrl: 'https://example.org',
	internal: false,
	evidence: [EVIDENCE],
});

/**
 * Renders an entity form against its real reducer and returns readers for the entity the page saves and for the
 * reducer's dispatch. In detail mode the form is loaded with `entity`; in create mode it starts as the create page
 * does.
 */
const renderEntityForm = ({ useReducer, stateKey, Form, Provider, entity, mode = 'detail' }) => {
	const handle = {};
	const Harness = () => {
		const { state, dispatch } = useReducer(mode);
		handle.state = state;
		handle.dispatch = dispatch;
		return (
			<Provider>
				<Form state={state} dispatch={dispatch} isVisible={() => true} mode={mode} />
			</Provider>
		);
	};
	const result = renderWithClient(<Harness />);
	if (mode === 'detail') act(() => handle.dispatch({ type: 'SET', value: entity }));
	return {
		...result,
		getEntity: () => handle.state[stateKey],
		dispatch: (action) => act(() => handle.dispatch(action)),
	};
};

const GeneSubResources = ({ children }) => {
	const { subResources } = useGeneSubResources();
	return <SubResourcesProvider value={subResources}>{children}</SubResourcesProvider>;
};

const AlleleSubResources = ({ children }) => {
	const { subResources } = useAlleleSubResources();
	return <SubResourcesProvider value={subResources}>{children}</SubResourcesProvider>;
};

const NoProvider = ({ children }) => children;

const ENTITIES = {
	gene: {
		Form: GeneForm,
		stateKey: 'gene',
		Provider: GeneSubResources,
		useReducer: (mode) => {
			const { geneState, geneDispatch } = useGeneReducer(mode);
			return { state: geneState, dispatch: geneDispatch };
		},
	},
	allele: {
		Form: AlleleForm,
		stateKey: 'allele',
		Provider: AlleleSubResources,
		useReducer: (mode) => {
			const { alleleState, alleleDispatch } = useAlleleReducer(mode);
			return { state: alleleState, dispatch: alleleDispatch };
		},
	},
	AGM: {
		Form: AGMForm,
		stateKey: 'agm',
		Provider: NoProvider,
		useReducer: () => {
			const { agmState, agmDispatch } = useAGMReducer();
			return { state: agmState, dispatch: agmDispatch };
		},
	},
};

/**
 * Every name section on the gene, allele and AGM detail pages. Each holds one name object, shown as a single-row
 * table, edited in place and saved with the entity.
 */
const SECTIONS = [
	{ entity: 'gene', title: 'Name', field: 'geneFullName', nameTypeSet: 'full_name_type', deletable: true },
	{ entity: 'gene', title: 'Symbol', field: 'geneSymbol', nameTypeSet: 'symbol_name_type', deletable: false },
	{
		entity: 'gene',
		title: 'Systematic Name',
		field: 'geneSystematicName',
		nameTypeSet: 'systematic_name_type',
		deletable: true,
	},
	{
		entity: 'allele',
		title: 'Name',
		field: 'alleleFullName',
		nameTypeSet: 'full_name_type',
		deletable: true,
		termPickThrows: true,
	},
	{ entity: 'allele', title: 'Symbol', field: 'alleleSymbol', nameTypeSet: 'symbol_name_type', deletable: false },
	{
		entity: 'AGM',
		title: 'Name',
		field: 'agmFullName',
		nameTypeSet: 'full_name_type',
		deletable: true,
		termPickThrows: true,
	},
];

// Required sections prefix their title with a marker.
const titleText = (heading) => heading.textContent.trim().replace(/^\*/, '');

/** The section headed `title`: its heading, Add button and table. */
const section = (title) => {
	const heading = screen.getAllByRole('heading', { level: 2 }).find((candidate) => titleText(candidate) === title);
	if (!heading) throw new Error(`No section headed "${title}"`);
	return heading.closest('.col-12');
};

const getButton = (container, label) => {
	const button = [...container.querySelectorAll('button')].find((candidate) => candidate.textContent.trim() === label);
	if (!button) throw new Error(`No button labelled "${label}"`);
	return button;
};

const dropdownLabel = (container, header) =>
	getCellByHeader(container, header).querySelector('.p-dropdown-label').textContent.trim();

const textIn = (container, header) => getCellByHeader(container, header).querySelector('input').value;

const columnHeaders = (container) =>
	[...container.querySelectorAll('thead tr:first-child th')].map((header) => header.textContent.trim());

/** Picks an evidence suggestion and waits for its panel to finish closing, which it does after the pick. */
const pickEvidence = async (container) => {
	await pickSuggestionInColumn(container, 'Evidence', 'AGRKB', /AGRKB:103/);
	await waitFor(() => expect(document.querySelector('.p-autocomplete-panel')).toBeNull());
};

describe.each(SECTIONS)(
	'$entity $title section',
	({ entity, title, field, nameTypeSet, deletable, termPickThrows }) => {
		const config = ENTITIES[entity];
		const renderSection = () => {
			const loaded = { id: 1, curie: 'AGRKB:100000000000001', [field]: name('loaded', nameTypeSet) };
			const result = renderEntityForm({ ...config, entity: loaded });
			return { ...result, saved: () => result.getEntity()[field] };
		};

		it('shows the loaded name', () => {
			renderSection();

			expect(textIn(section(title), 'Display Text')).toBe('loaded');
			expect(textIn(section(title), 'Format Text')).toBe('loaded format');
			expect(textIn(section(title), 'Synonym URL')).toBe('https://example.org');
			expect(dropdownLabel(section(title), 'Synonym Scope')).toBe('synonym_scope_alpha');
			expect(dropdownLabel(section(title), 'Name Type')).toBe(`${nameTypeSet}_alpha`);
			expect(dropdownLabel(section(title), 'Internal')).toBe('false');
			expect(getCellByHeader(section(title), 'Evidence').textContent).toContain(EVIDENCE.curie);
		});

		it.each([
			['Display Text', 'displayText'],
			['Format Text', 'formatText'],
			['Synonym URL', 'synonymUrl'],
		])('saves typed %s', (header, property) => {
			const { saved } = renderSection();

			typeInColumn(section(title), header, 'typed');

			expect(saved()[property]).toBe('typed');
		});

		// The allele and AGM name sections pass a picked term to PrimeReact's editorCallback, which writes its name
		// into the term already on the row. That term is frozen reducer state, so the write throws and the pick is
		// lost.
		const pickTerm = termPickThrows ? it.skip : it;

		pickTerm('replaces the synonym scope with the whole picked term', () => {
			const { saved } = renderSection();

			selectInColumn(section(title), 'Synonym Scope', 'synonym_scope_beta');

			expect(saved().synonymScope).toEqual(vocabularyTermsFor('synonym_scope')[1]);
		});

		pickTerm('offers the name types of its term set and saves the whole picked term', () => {
			const { saved } = renderSection();

			selectInColumn(section(title), 'Name Type', `${nameTypeSet}_beta`);

			expect(saved().nameType).toEqual(termSetTermsFor(nameTypeSet)[1]);
		});

		it('saves internal as a boolean', () => {
			const { saved } = renderSection();

			selectInColumn(section(title), 'Internal', 'true');

			expect(saved().internal).toBe(true);
		});

		it('saves a picked evidence alongside the existing one', async () => {
			const { saved } = renderSection();

			await pickEvidence(section(title));

			expect(saved().evidence).toEqual([EVIDENCE, NEW_EVIDENCE]);
		});

		it("shows a rejected save's error on the cell that caused it", () => {
			const { getEntity, dispatch } = renderSection();

			processErrors(
				{ supplementalData: { errorMap: { [field]: { displayText: 'Required field is empty' } } } },
				dispatch,
				getEntity()
			);

			expect(getCellByHeader(section(title), 'Display Text').textContent).toContain('Required field is empty');
			expect(getCellByHeader(section(title), 'Format Text').textContent).not.toContain('Required field is empty');
		});

		if (deletable) {
			it('removes the name on delete, then adds an empty one', () => {
				const { saved } = renderSection();

				clickDeleteInRow(section(title));

				expect(saved()).toBeNull();

				fireEvent.click(getButton(section(title), title === 'Name' ? 'Add Full Name' : `Add ${title}`));

				expect(saved()).toMatchObject({ displayText: '', formatText: '', nameType: null, internal: false });
				expect(textIn(section(title), 'Display Text')).toBe('');
			});
		} else {
			it('offers no delete and no second symbol', () => {
				renderSection();

				expect(columnHeaders(section(title))).not.toContain('Actions');
				expect(section(title).querySelector('tbody button')).toBeNull();
				expect(getButton(section(title), `Add ${title}`)).toBeDisabled();
			});
		}
	}
);

describe.each([
	{ entity: 'gene', field: 'geneSymbol' },
	{ entity: 'allele', field: 'alleleSymbol' },
])('$entity create form Symbol section', ({ entity, field }) => {
	it('starts with an empty symbol, marked required, with no button adding another', () => {
		const { getEntity } = renderEntityForm({ ...ENTITIES[entity], mode: 'create' });

		const heading = screen.getAllByRole('heading', { level: 2 }).find((candidate) => titleText(candidate) === 'Symbol');
		expect(heading.textContent.trim()).toMatch(/^\*/);
		expect(textIn(section('Symbol'), 'Display Text')).toBe('');
		expect(getEntity()[field]).toMatchObject({ displayText: '', formatText: '' });
		expect([...section('Symbol').querySelectorAll('button')].map((button) => button.textContent.trim())).not.toContain(
			'Add Symbol'
		);
	});
});
