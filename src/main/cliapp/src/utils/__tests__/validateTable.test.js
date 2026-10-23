import { validateTable } from '../utils';

const { validate } = vi.hoisted(() => ({ validate: vi.fn() }));
vi.mock('../../service/ValidationService', () => ({
	ValidationService: class {
		validate = validate;
	},
}));

describe('validateTable', () => {
	beforeEach(() => validate.mockReset());

	it("files each row's errors under its dataKey and reports that there were errors", async () => {
		const dispatch = vi.fn();
		validate
			.mockResolvedValueOnce({ isError: false })
			.mockResolvedValueOnce({ isError: true, data: { freeText: 'Required' } });

		const anyErrors = await validateTable(
			'note',
			'relatedNotesErrorMessages',
			[{ dataKey: 'k7' }, { dataKey: 'k3' }],
			dispatch
		);

		expect(anyErrors).toBe(true);
		expect(dispatch).toHaveBeenCalledWith({
			type: 'UPDATE_ERROR_MESSAGES',
			errorType: 'relatedNotesErrorMessages',
			errorMessages: { k3: { freeText: { severity: 'error', message: 'Required' } } },
		});
	});

	it('reports no errors when every row validates', async () => {
		const dispatch = vi.fn();
		validate.mockResolvedValue({ isError: false });

		const anyErrors = await validateTable('note', 'relatedNotesErrorMessages', [{ dataKey: 'k7' }], dispatch);

		expect(anyErrors).toBe(false);
		expect(dispatch).toHaveBeenCalledWith(expect.objectContaining({ errorMessages: {} }));
	});
});
