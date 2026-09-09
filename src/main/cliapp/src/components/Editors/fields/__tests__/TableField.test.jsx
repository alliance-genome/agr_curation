import { render, fireEvent } from '@testing-library/react';
import { TableField } from '../TableField';
import { TableEditorProvider } from '../TableEditorContext';
import { TextInput } from '../../widgets/TextInput';

const makeEditorOptions = (rowData, rowIndex = 0) => ({ rowData, rowIndex, editorCallback: vi.fn() });

/** Minimal strategy: index-addressed, stores through editorCallback. */
const indexStrategy = (errors = {}, extra = {}) => ({
	errorLayout: 'overlay',
	keyOf: (editorOptions) => editorOptions.rowIndex,
	errorsAt: (rowKey, field) => [errors?.[rowKey]?.[field]],
	readRow: (editorOptions) => editorOptions.rowData,
	write: ({ editorOptions, value }) => editorOptions.editorCallback(value),
	...extra,
});

const renderField = (props, strategy) =>
	render(
		<TableEditorProvider strategy={strategy}>
			<TableField {...props} />
		</TableEditorProvider>
	);

describe('<TableField />', () => {
	describe('value resolution', () => {
		it('reads the field off the row by default', () => {
			const { container } = renderField(
				{
					editorOptions: makeEditorOptions({ name: 'a value' }),
					field: 'name',
					children: (binding) => <TextInput {...binding} />,
				},
				indexStrategy()
			);

			expect(container.querySelector('input')).toHaveValue('a value');
		});

		it('uses a supplied read transform', () => {
			const { container } = renderField(
				{
					editorOptions: makeEditorOptions({ nested: { label: 'derived' } }),
					field: 'nested',
					read: (row) => row.nested?.label,
					children: (binding) => <TextInput {...binding} />,
				},
				indexStrategy()
			);

			expect(container.querySelector('input')).toHaveValue('derived');
		});
	});

	describe('writes', () => {
		it('routes through the strategy write when one is supplied, leaving editorCallback alone', () => {
			const editorOptions = makeEditorOptions({ name: '' }, 3);
			const write = vi.fn();
			const { container } = renderField(
				{ editorOptions, field: 'name', children: (binding) => <TextInput {...binding} /> },
				indexStrategy({}, { write })
			);

			fireEvent.change(container.querySelector('input'), { target: { value: 'typed' } });

			expect(write).toHaveBeenCalledWith({ editorOptions, rowKey: 3, field: 'name', value: 'typed' });
			expect(editorOptions.editorCallback).not.toHaveBeenCalled();
		});

		it('applies a supplied write transform before storing', () => {
			const editorOptions = makeEditorOptions({ handle: '' });
			const { container } = renderField(
				{
					editorOptions,
					field: 'conditionRelations',
					read: () => '',
					write: (value) => [{ handle: value }],
					children: (binding) => <TextInput {...binding} />,
				},
				indexStrategy()
			);

			fireEvent.change(container.querySelector('input'), { target: { value: 'CR:1' } });

			expect(editorOptions.editorCallback).toHaveBeenCalledWith([{ handle: 'CR:1' }]);
		});
	});

	describe('errors', () => {
		const ERROR = { severity: 'error', message: 'Not a valid entry' };

		it('renders the error for its row and field', () => {
			const { getByText } = renderField(
				{
					editorOptions: makeEditorOptions({ name: 'x' }, 2),
					field: 'name',
					children: (binding) => <TextInput {...binding} />,
				},
				indexStrategy({ 2: { name: ERROR } })
			);

			expect(getByText('Not a valid entry')).toBeInTheDocument();
		});

		it('ignores an error belonging to another row', () => {
			const { queryByText } = renderField(
				{
					editorOptions: makeEditorOptions({ name: 'x' }, 2),
					field: 'name',
					children: (binding) => <TextInput {...binding} />,
				},
				indexStrategy({ 5: { name: ERROR } })
			);

			expect(queryByText('Not a valid entry')).toBeNull();
		});

		it('looks errors up under errorField when it differs from the written field', () => {
			const { getByText } = renderField(
				{
					editorOptions: makeEditorOptions({ conditionRelations: [] }),
					field: 'conditionRelations',
					errorField: 'conditionRelationHandle',
					children: (binding) => <TextInput {...binding} />,
				},
				indexStrategy({ 0: { conditionRelationHandle: ERROR } })
			);

			expect(getByText('Not a valid entry')).toBeInTheDocument();
		});

		it('marks the widget invalid when an error is present', () => {
			const { container } = renderField(
				{
					editorOptions: makeEditorOptions({ name: 'x' }),
					field: 'name',
					children: (binding) => <TextInput {...binding} />,
				},
				indexStrategy({ 0: { name: ERROR } })
			);

			expect(container.querySelector('input')).toHaveClass('p-invalid');
		});

		// Strategies may expose more than one error channel; TableField renders all of them.

		it('renders no message when the row has errors on other fields only', () => {
			const { container } = renderField(
				{
					editorOptions: makeEditorOptions({ name: 'x' }),
					field: 'name',
					children: (binding) => <TextInput {...binding} />,
				},
				indexStrategy({ 0: { somethingElse: ERROR } })
			);

			expect(container.querySelector('.p-inline-message')).toBeNull();
		});
	});

	describe('composition', () => {
		it('accepts a plain node, for cells that only need an error slot', () => {
			const { getByText } = renderField(
				{
					editorOptions: makeEditorOptions({ uniqueId: 'AGRKB:1' }),
					field: 'uniqueId',
					children: <span>AGRKB:1</span>,
				},
				indexStrategy({ 0: { uniqueId: { severity: 'error', message: 'boom' } } })
			);

			expect(getByText('AGRKB:1')).toBeInTheDocument();
			expect(getByText('boom')).toBeInTheDocument();
		});

		it('prefers an explicit strategy prop over the one from context', () => {
			const explicitWrite = vi.fn();
			const editorOptions = makeEditorOptions({ name: '' });
			const { container } = render(
				<TableEditorProvider strategy={indexStrategy()}>
					<TableField editorOptions={editorOptions} field="name" strategy={indexStrategy({}, { write: explicitWrite })}>
						{(binding) => <TextInput {...binding} />}
					</TableField>
				</TableEditorProvider>
			);

			fireEvent.change(container.querySelector('input'), { target: { value: 'typed' } });

			expect(explicitWrite).toHaveBeenCalled();
			expect(editorOptions.editorCallback).not.toHaveBeenCalled();
		});

		// A self-supplied strategy, with no provider anywhere above.
		it('works from a strategy prop alone, with no provider', () => {
			const write = vi.fn();
			const editorOptions = makeEditorOptions({ internal: false });
			const { container, getByText } = render(
				<TableField
					editorOptions={editorOptions}
					field="internal"
					strategy={{
						errorLayout: 'inline',
						keyOf: () => 'caller-supplied-key',
						readRow: (options) => options.rowData,
						write,
						errorsAt: (rowKey, field) =>
							rowKey === 'caller-supplied-key' && field === 'internal'
								? [{ severity: 'error', message: 'from the caller' }]
								: [],
					}}
				>
					{(binding) => <TextInput {...binding} />}
				</TableField>
			);

			expect(getByText('from the caller')).toBeInTheDocument();

			fireEvent.change(container.querySelector('input'), { target: { value: 'typed' } });

			expect(write).toHaveBeenCalledWith(
				expect.objectContaining({ rowKey: 'caller-supplied-key', field: 'internal', value: 'typed' })
			);
		});

		it('throws a named error when rendered with no strategy at all', () => {
			expect(() =>
				render(
					<TableField editorOptions={makeEditorOptions({ name: '' })} field="name">
						{(binding) => <TextInput {...binding} />}
					</TableField>
				)
			).toThrow(/TableField "name".*TableEditorProvider/);
		});
	});
});
