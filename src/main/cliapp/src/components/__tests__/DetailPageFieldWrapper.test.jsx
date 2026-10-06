import React from 'react';
import { render, screen } from '@testing-library/react';
import '../../tools/jest/setupTests';
import { DetailPageFieldWrapper } from '../DetailPageFieldWrapper';

const renderWrapper = (props = {}) =>
	render(
		<DetailPageFieldWrapper
			fieldName="Taxon"
			formField={<input aria-label="taxon" />}
			labelColumnSize="col-3"
			widgetColumnSize="col-4"
			fieldDetailsColumnSize="col-5"
			{...props}
		/>
	);

describe('DetailPageFieldWrapper', () => {
	it('Shows no pending edits indicator by default', () => {
		const { container } = renderWrapper();

		expect(screen.queryByText('Pending Edits!')).not.toBeInTheDocument();
		expect(container.firstChild).not.toHaveClass('border-left-3');
	});

	it('Highlights the field and says it has pending edits when pending', () => {
		const { container } = renderWrapper({ isPending: true });

		expect(screen.getByText('Pending Edits!')).toBeInTheDocument();
		expect(container.firstChild).toHaveClass('border-left-3');
	});
});
