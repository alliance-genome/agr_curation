import React from 'react';
import { screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { renderWithClient } from '../../../../tools/jest/utils';

const { validate, getResourceDescriptor } = vi.hoisted(() => ({
	validate: vi.fn(),
	getResourceDescriptor: vi.fn(),
}));

vi.mock('../../../../service/ValidationService', () => ({
	ValidationService: class {
		validate = validate;
	},
}));

vi.mock('../../../../service/ResourceDescriptorService', () => ({
	ResourceDescriptorService: class {
		getResourceDescriptor = getResourceDescriptor;
	},
}));

vi.mock('../../../../service/SearchService', () => ({
	SearchService: class {
		search = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
		find = vi.fn(() => Promise.resolve({ results: [], totalResults: 0 }));
	},
}));

const { CrossReferencesEditDialog } = await import('../CrossReferencesEditDialog');

const PMID = {
	id: 9,
	prefix: 'PMID',
	resourcePages: [
		{ id: 1, name: 'default' },
		{ id: 2, name: 'gene' },
	],
};

const storedCrossReference = {
	id: 500,
	displayName: 'PMID:1',
	referencedCurie: 'PMID:1',
	resourceDescriptorPage: { id: 1, name: 'default', resourceDescriptor: { id: 9, prefix: 'PMID' } },
	internal: false,
	obsolete: false,
};

const renderDialog = (originalCrossReferences = [storedCrossReference]) => {
	const editorCallback = vi.fn();
	const setOriginalCrossReferencesData = vi.fn();
	const setErrorMessagesMainRow = vi.fn();

	const result = renderWithClient(
		<CrossReferencesEditDialog
			originalCrossReferencesData={{
				originalCrossReferences,
				isInEdit: true,
				dialog: true,
				rowIndex: 3,
				mainRowProps: { editorCallback },
			}}
			setOriginalCrossReferencesData={setOriginalCrossReferencesData}
			errorMessagesMainRow={{}}
			setErrorMessagesMainRow={setErrorMessagesMainRow}
		/>
	);

	return { ...result, editorCallback, setOriginalCrossReferencesData, setErrorMessagesMainRow };
};

beforeEach(() => {
	validate.mockReset();
	getResourceDescriptor.mockReset();
	validate.mockResolvedValue({ isSuccess: true, isError: false, data: {} });
	getResourceDescriptor.mockResolvedValue({ data: { entity: PMID } });
});

describe('CrossReferencesEditDialog', () => {
	it('Loads the descriptor in full so the page dropdown has options', async () => {
		renderDialog();

		await waitFor(() => expect(getResourceDescriptor).toHaveBeenCalledWith(9));
		expect(await screen.findByLabelText('resourceDescriptorPage')).toHaveValue('default');
	});

	it('Writes the rows back to the allele row without the table fields', async () => {
		const user = userEvent.setup();
		const { editorCallback } = renderDialog();
		await waitFor(() => expect(getResourceDescriptor).toHaveBeenCalled());

		await user.click(screen.getByRole('button', { name: /Keep Edits/ }));

		await waitFor(() => expect(editorCallback).toHaveBeenCalledTimes(1));
		const [written] = editorCallback.mock.calls[0];
		expect(written).toHaveLength(1);
		expect(written[0]).not.toHaveProperty('dataKey');
		expect(written[0]).not.toHaveProperty('resourceDescriptor');
		expect(written[0]).toMatchObject({ id: 500, referencedCurie: 'PMID:1' });
	});

	it('Warns the allele row that it holds unsaved edits', async () => {
		const user = userEvent.setup();
		const { setErrorMessagesMainRow } = renderDialog();
		await waitFor(() => expect(getResourceDescriptor).toHaveBeenCalled());

		await user.click(screen.getByRole('button', { name: /Keep Edits/ }));

		await waitFor(() => expect(setErrorMessagesMainRow).toHaveBeenCalled());
		const [stamped] = setErrorMessagesMainRow.mock.calls[0];
		expect(stamped[3].crossReferences).toEqual({ severity: 'warn', message: 'Pending Edits!' });
	});

	it('Writes nothing back when a row fails validation', async () => {
		const user = userEvent.setup();
		validate.mockResolvedValue({
			isSuccess: false,
			isError: true,
			data: { referencedCurie: 'Required field is empty' },
		});
		const { editorCallback, setOriginalCrossReferencesData } = renderDialog();
		await waitFor(() => expect(getResourceDescriptor).toHaveBeenCalled());

		await user.click(screen.getByRole('button', { name: /Keep Edits/ }));

		await waitFor(() => expect(validate).toHaveBeenCalled());
		expect(editorCallback).not.toHaveBeenCalled();
		expect(setOriginalCrossReferencesData).not.toHaveBeenCalled();
		expect(await screen.findByText('Required field is empty')).toBeInTheDocument();
	});

	it('Adds a blank row on New Cross Reference', async () => {
		const user = userEvent.setup();
		renderDialog();
		await waitFor(() => expect(getResourceDescriptor).toHaveBeenCalled());

		await user.click(screen.getByRole('button', { name: /New Cross Reference/ }));

		await waitFor(() => expect(screen.getAllByLabelText('resourceDescriptorPage')).toHaveLength(2));
		expect(screen.getAllByLabelText('resourceDescriptorPage')[1]).toHaveValue('');
	});

	it('Validates every row, including ones just added', async () => {
		const user = userEvent.setup();
		renderDialog();
		await waitFor(() => expect(getResourceDescriptor).toHaveBeenCalled());

		await user.click(screen.getByRole('button', { name: /New Cross Reference/ }));
		await user.click(screen.getByRole('button', { name: /Keep Edits/ }));

		await waitFor(() => expect(validate).toHaveBeenCalledTimes(2));
	});

	it('Closes without writing anything back on Cancel', async () => {
		const user = userEvent.setup();
		const { editorCallback, setOriginalCrossReferencesData } = renderDialog();
		await waitFor(() => expect(getResourceDescriptor).toHaveBeenCalled());

		await user.click(screen.getByRole('button', { name: /Cancel/ }));

		expect(editorCallback).not.toHaveBeenCalled();
		expect(setOriginalCrossReferencesData).toHaveBeenCalled();
	});

	it('Reads nothing when the allele has no cross references', async () => {
		renderDialog(null);

		await waitFor(() => expect(screen.getByRole('button', { name: /Keep Edits/ })).toBeInTheDocument());
		expect(getResourceDescriptor).not.toHaveBeenCalled();
	});
});
