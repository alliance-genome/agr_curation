import React from 'react';
import { render, screen } from '@testing-library/react';
import { SubResourcesProvider, useSubResource } from '../SubResourcesContext';

const Consumer = ({ name }) => {
	const subResource = useSubResource(name);
	return <div>{subResource.label}</div>;
};

describe('SubResourcesContext', () => {
	it('Hands a section the sub-resource the page provided under that name', () => {
		render(
			<SubResourcesProvider value={{ crossReferences: { label: 'the cross references' } }}>
				<Consumer name="crossReferences" />
			</SubResourcesProvider>
		);

		expect(screen.getByText('the cross references')).toBeInTheDocument();
	});

	it('Keeps sub-resources apart by name', () => {
		render(
			<SubResourcesProvider value={{ crossReferences: { label: 'xrefs' }, secondaryIds: { label: 'ids' } }}>
				<Consumer name="secondaryIds" />
			</SubResourcesProvider>
		);

		expect(screen.getByText('ids')).toBeInTheDocument();
	});

	// Rendering a section the page forgot to provide for is a wiring mistake, and it should say so
	// rather than fail later on a missing property.
	it('Says what is missing when the page provided no such sub-resource', () => {
		vi.spyOn(console, 'error').mockImplementation(() => {});

		expect(() =>
			render(
				<SubResourcesProvider value={{ secondaryIds: {} }}>
					<Consumer name="crossReferences" />
				</SubResourcesProvider>
			)
		).toThrow(/No "crossReferences" sub-resource in context/);

		console.error.mockRestore();
	});

	it('Says the same when there is no provider at all', () => {
		vi.spyOn(console, 'error').mockImplementation(() => {});

		expect(() => render(<Consumer name="crossReferences" />)).toThrow(/SubResourcesProvider/);

		console.error.mockRestore();
	});
});
