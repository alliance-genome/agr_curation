import { render, screen, within } from '@testing-library/react';
import { SaveProgressOverlay } from '../SaveProgressOverlay';
import { SAVE_STATUS } from '../../hooks/useSaveProgress';

const row = (label) => screen.getByText(new RegExp(`^${label}:`)).closest('li');

describe('<SaveProgressOverlay />', () => {
	it('Renders nothing while closed', () => {
		const { container } = render(<SaveProgressOverlay isOpen={false} steps={[]} />);

		expect(container).toBeEmptyDOMElement();
	});

	it('Lists each row with its status, colour and icon in a live region', () => {
		render(
			<SaveProgressOverlay
				isOpen
				steps={[
					{ key: 'allele', label: 'Allele', status: SAVE_STATUS.SAVED },
					{ key: 'alleleSynonyms', label: 'Synonyms', status: SAVE_STATUS.SAVING },
					{ key: 'references', label: 'References', status: SAVE_STATUS.QUEUED },
					{ key: 'relatedNotes', label: 'Related notes', status: SAVE_STATUS.SKIPPED },
				]}
			/>
		);

		const progress = screen.getByRole('status', { name: 'Save progress' });
		expect(within(within(progress).getByRole('list')).getAllByRole('listitem')).toHaveLength(4);
		expect(within(progress).getByRole('heading', { level: 1 })).toHaveTextContent('Saving...');
		expect(row('Allele')).toHaveTextContent('Allele: Saved');
		expect(row('Allele')).toHaveStyle({ color: 'var(--green-700)' });
		expect(row('Allele').querySelector('i')).toHaveClass('pi-check-circle');
		expect(row('Synonyms')).toHaveTextContent('Synonyms: Saving');
		expect(row('Synonyms')).toHaveStyle({ color: 'var(--yellow-700)' });
		expect(row('Synonyms').querySelector('i')).toHaveClass('pi-spinner');
		expect(row('References')).toHaveTextContent('References: Pending');
		expect(row('References')).toHaveStyle({ color: 'var(--yellow-700)' });
		expect(row('References').querySelector('i')).toHaveClass('pi-clock');
		expect(row('Related notes')).toHaveTextContent('Related notes: Not saved');
		expect(row('Related notes')).toHaveStyle({ color: 'var(--text-color-secondary)' });
		expect(row('Related notes').querySelector('i')).toHaveClass('pi-minus-circle');
	});

	it('Shows a failed row by its status alone, and that the save finished with errors', () => {
		render(
			<SaveProgressOverlay
				isOpen
				steps={[
					{ key: 'allele', label: 'Allele', status: SAVE_STATUS.SAVED },
					{ key: 'alleleSynonyms', label: 'Synonyms', status: SAVE_STATUS.FAILED },
				]}
			/>
		);

		expect(screen.getByRole('heading', { level: 1 })).toHaveTextContent('Save finished with errors');
		expect(row('Synonyms')).toHaveTextContent(/^Synonyms: Failed$/);
		expect(row('Synonyms')).toHaveStyle({ color: 'var(--red-700)' });
		expect(row('Synonyms').querySelector('i')).toHaveClass('pi-times-circle');
	});

	it('Says the save is done once every row saved', () => {
		render(<SaveProgressOverlay isOpen steps={[{ key: 'allele', label: 'Allele', status: SAVE_STATUS.SAVED }]} />);

		expect(screen.getByRole('heading', { level: 1 })).toHaveTextContent('Saved');
	});
});
