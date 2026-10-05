import React from 'react';
import { render, screen } from '@testing-library/react';
import '../../tools/jest/setupTests';
import { FormTableWrapper } from '../FormTableWrapper';

const renderWrapper = (props = {}) =>
	render(<FormTableWrapper tableName="Cross References" table={<table />} showTable button={<button />} {...props} />);

describe('FormTableWrapper', () => {
	it('Shows no pending edits indicator by default', () => {
		const { container } = renderWrapper();

		expect(screen.queryByText('Pending Edits!')).not.toBeInTheDocument();
		expect(container.firstChild).not.toHaveClass('bg-yellow-50');
	});

	it('Highlights the section and says it has pending edits when pending', () => {
		const { container } = renderWrapper({ isPending: true });

		expect(screen.getByText('Pending Edits!')).toBeInTheDocument();
		expect(container.firstChild).toHaveClass('bg-yellow-50');
	});
});
