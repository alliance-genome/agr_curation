import { fireEvent, render, screen } from '@testing-library/react';
import { FormField } from '../FormField';
import { TextInput } from '../../widgets/TextInput';
import { BooleanSelect } from '../../widgets/BooleanSelect';

const renderTextField = (props = {}) =>
	render(
		<FormField label="Taxon" name="taxon" value="" onChange={vi.fn()} {...props}>
			{(binding) => <TextInput {...binding} />}
		</FormField>
	);

describe('<FormField />', () => {
	it('renders the label as the heading and the widget beside it', () => {
		const { container } = render(
			<FormField label="Is Extinct" name="isExtinct" value={true} onChange={vi.fn()}>
				{(binding) => <BooleanSelect {...binding} />}
			</FormField>
		);

		expect(screen.getByRole('heading', { name: 'Is Extinct' })).toBeInTheDocument();
		expect(container.querySelector('.p-dropdown-label').textContent).toBe('true');
	});

	it('passes the value to the widget and reports changes as values', () => {
		const onChange = vi.fn();
		renderTextField({ value: 'NCBITaxon:6239', onChange });

		expect(screen.getByRole('textbox')).toHaveValue('NCBITaxon:6239');

		fireEvent.change(screen.getByRole('textbox'), { target: { value: 'NCBITaxon:7955' } });

		expect(onChange).toHaveBeenCalledWith('NCBITaxon:7955');
	});

	it('names the widget for assistive technology', () => {
		renderTextField();

		expect(screen.getByRole('textbox', { name: 'taxon' })).toBeInTheDocument();
	});

	it('renders an error given as a string', () => {
		renderTextField({ errors: { taxon: 'Required field' } });

		expect(screen.getByText('Required field')).toBeInTheDocument();
	});

	it('renders the error as text beneath the widget', () => {
		const { container } = renderTextField({ errors: { taxon: 'Required field' } });

		expect(container.querySelector('small.p-error')).toHaveTextContent('Required field');
	});

	it('renders an error given as an object', () => {
		renderTextField({ errors: { taxon: { severity: 'error', message: 'Required field' } } });

		expect(screen.getByText('Required field')).toBeInTheDocument();
	});

	it("ignores another field's error", () => {
		renderTextField({ errors: { inCollection: 'Required field' } });

		expect(screen.queryByText('Required field')).not.toBeInTheDocument();
		expect(screen.getByRole('textbox')).not.toHaveClass('p-invalid');
	});

	it('marks the widget invalid when an error applies', () => {
		renderTextField({ errors: { taxon: 'Required field' } });

		expect(screen.getByRole('textbox')).toHaveClass('p-invalid');
	});

	it('looks the error up under errorField when it differs from name', () => {
		renderTextField({ name: 'reference', errorField: 'references', errors: { references: 'Required field' } });

		expect(screen.getByText('Required field')).toBeInTheDocument();
	});

	it('marks the heading of a required field', () => {
		renderTextField({ required: true });

		expect(screen.getByRole('heading', { name: 'Taxon' }).textContent).toMatch(/^\*/);
	});

	it('marks a field with pending edits', () => {
		renderTextField({ isPending: true });

		expect(screen.getByText('Pending Edits!')).toBeInTheDocument();
	});

	it('renders supplementary content beside the widget', () => {
		renderTextField({ additionalDataField: <span>Caenorhabditis elegans</span> });

		expect(screen.getByText('Caenorhabditis elegans')).toBeInTheDocument();
	});

	it('passes a false value to the widget', () => {
		const { container } = render(
			<FormField label="Obsolete" name="obsolete" value={false} onChange={vi.fn()}>
				{(binding) => <BooleanSelect {...binding} />}
			</FormField>
		);

		expect(container.querySelector('.p-dropdown-label').textContent).toBe('false');
	});

	it('leaves out the supplementary content when asked to', () => {
		renderTextField({ additionalDataField: <span>Caenorhabditis elegans</span>, showAdditionalData: false });

		expect(screen.queryByText('Caenorhabditis elegans')).not.toBeInTheDocument();
	});

	it('renders a plain node as its widget', () => {
		render(
			<FormField label="Curie" name="curie" value="AGRKB:1" onChange={vi.fn()}>
				<span>AGRKB:1</span>
			</FormField>
		);

		expect(screen.getByText('AGRKB:1')).toBeInTheDocument();
	});
});
