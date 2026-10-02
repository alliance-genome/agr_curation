import React from 'react';
import { render, screen } from '@testing-library/react';
import '../../tools/jest/setupTests';
import { LoadingOverlay } from '../LoadingOverlay';

describe('LoadingOverlay', () => {
	it('Renders nothing when not loading', () => {
		const { container } = render(<LoadingOverlay isLoading={false} />);

		expect(container.firstChild).toBeNull();
	});

	it('Says a save is in progress by default', () => {
		render(<LoadingOverlay isLoading />);

		expect(screen.getByRole('heading', { name: 'Saving in progress...' })).toBeInTheDocument();
	});

	it('Shows the message it is given', () => {
		render(<LoadingOverlay isLoading message="Loading allele to duplicate..." />);

		expect(screen.getByRole('heading', { name: 'Loading allele to duplicate...' })).toBeInTheDocument();
		expect(screen.queryByText('Saving in progress...')).not.toBeInTheDocument();
	});
});
