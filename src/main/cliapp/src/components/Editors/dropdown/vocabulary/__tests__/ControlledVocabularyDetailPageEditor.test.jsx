import { fireEvent, render, screen } from '@testing-library/react';
import { ControlledVocabularyDetailPageEditor } from '../ControlledVocabularyDetailPageEditor';
import { vocabularyTermsFor } from '../../../../../service/__mocks__/useControlledVocabularyService';

vi.mock('../../../../../service/useControlledVocabularyService');

const [ALPHA, BETA] = vocabularyTermsFor('agm_subtype');

const renderEditor = (props = {}) =>
	render(
		<ControlledVocabularyDetailPageEditor
			value={ALPHA}
			name="subtype"
			label="Sub Type"
			vocabularyLabel="agm_subtype"
			onChange={vi.fn()}
			{...props}
		/>
	);

describe('<ControlledVocabularyDetailPageEditor />', () => {
	// A stored term is a separate object from the loaded option and can carry an older label.
	it('shows a stored term matched to its option by id', () => {
		const { container } = renderEditor({ value: { id: ALPHA.id, name: 'stale label' } });

		expect(container.querySelector('.p-dropdown-label')).toHaveTextContent(ALPHA.name);
	});

	it('reports the whole chosen term', () => {
		const onChange = vi.fn();
		const { container } = renderEditor({ onChange });

		fireEvent.click(container.querySelector('.p-dropdown'));
		fireEvent.click(screen.getByText(BETA.name, { selector: '.p-dropdown-item, .p-dropdown-item *' }));

		expect(onChange).toHaveBeenCalledWith(BETA);
	});

	it('reports null when cleared', () => {
		const onChange = vi.fn();
		const { container } = renderEditor({ onChange });

		fireEvent.pointerUp(container.querySelector('.p-dropdown-clear-icon'));

		expect(onChange).toHaveBeenCalledWith(null);
	});

	it('shows an error given as an object', () => {
		renderEditor({ errorMessages: { subtype: { severity: 'error', message: 'is bad' } } });

		expect(screen.getByText('is bad')).toBeInTheDocument();
	});

	it('marks the heading of a required field', () => {
		renderEditor({ required: true });

		expect(screen.getByRole('heading', { name: 'Sub Type' }).textContent).toMatch(/^\*/);
	});
});
