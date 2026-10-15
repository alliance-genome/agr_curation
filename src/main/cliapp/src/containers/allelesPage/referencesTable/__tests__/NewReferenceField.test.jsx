import React, { useState } from 'react';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { SubResourcesProvider } from '../../../../components/SubResourcesContext';

const searchResult = {
	curie: 'AGRKB:101',
	short_citation: 'Doe 2020',
	cross_references: [{ curie: 'PMID:101' }],
};

// The autocomplete is replaced by buttons that report what a curator picking or typing would.
vi.mock('../../../../components/Editors/autocomplete/references/SingleReferenceDetailPageEditor', () => ({
	SingleReferenceDetailPageEditor: ({ onReferenceValueChange, errorMessages }) => (
		<>
			<button onClick={() => onReferenceValueChange({ target: { value: searchResult } })}>Pick reference</button>
			<button onClick={() => onReferenceValueChange({ target: { value: 'typed text' } })}>Type text</button>
			<span>{errorMessages?.references}</span>
		</>
	),
}));

vi.mock('../../../../components/FieldData/SingleReferenceAdditionalFieldData', () => ({
	SingleReferenceAdditionalFieldData: () => null,
}));

const { NewReferenceField } = await import('../NewReferenceField');

let heldReferences;

const Harness = ({ initialReferences = [] }) => {
	const [references, setReferences] = useState(initialReferences);
	heldReferences = references;
	return (
		<SubResourcesProvider value={{ references: { references, setReferences } }}>
			<NewReferenceField />
		</SubResourcesProvider>
	);
};

describe('<NewReferenceField />', () => {
	it('Adds the picked reference to the rows, ready for the table', async () => {
		const user = userEvent.setup();
		render(<Harness initialReferences={[{ curie: 'AGRKB:100', dataKey: 'stored' }]} />);

		await user.click(screen.getByRole('button', { name: 'Pick reference' }));
		await user.click(screen.getByRole('button', { name: 'Add Reference' }));

		expect(heldReferences.map((reference) => reference.curie)).toEqual(['AGRKB:101', 'AGRKB:100']);
		expect(heldReferences[0]).toMatchObject({ shortCitation: 'Doe 2020', crossReferencesFilter: 'PMID:101' });
		expect(heldReferences[0].dataKey).toBeTruthy();
	});

	it('Keeps the row already holding a picked reference rather than adding it again', async () => {
		const user = userEvent.setup();
		const storedRow = { curie: 'AGRKB:101', dataKey: 'stored' };
		render(<Harness initialReferences={[storedRow]} />);

		await user.click(screen.getByRole('button', { name: 'Pick reference' }));
		await user.click(screen.getByRole('button', { name: 'Add Reference' }));

		expect(heldReferences).toEqual([storedRow]);
	});

	it('Asks for a reference from the dropdown when none was picked', async () => {
		const user = userEvent.setup();
		render(<Harness />);

		await user.click(screen.getByRole('button', { name: 'Type text' }));
		await user.click(screen.getByRole('button', { name: 'Add Reference' }));

		expect(screen.getByText('Must select reference from dropdown')).toBeInTheDocument();
		expect(heldReferences).toEqual([]);

		await user.click(screen.getByRole('button', { name: 'Pick reference' }));
		await user.click(screen.getByRole('button', { name: 'Add Reference' }));

		expect(screen.queryByText('Must select reference from dropdown')).not.toBeInTheDocument();
		expect(heldReferences).toHaveLength(1);
	});
});
