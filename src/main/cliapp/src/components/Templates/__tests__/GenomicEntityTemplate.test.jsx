import { fireEvent, render } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { GenomicEntityTemplate } from '../genomicEntity/GenomicEntityTemplate';
import '../../../tools/jest/setupTests';

describe('GenomicEntityTemplate', () => {
	it('should render genomicEntity text and id when genomicEntity has geneSymbol', () => {
		const genomicEntity = {
			geneSymbol: {
				displayText: 'Gene Symbol',
			},
			curie: 'CURIE',
		};

		const result = render(<GenomicEntityTemplate genomicEntity={genomicEntity} />);

		const content = result.getByText('Gene Symbol (CURIE)');
		expect(content).toBeInTheDocument();
	});

	it('should render genomicEntity text and id when genomicEntity has alleleSymbol', () => {
		const genomicEntity = {
			alleleSymbol: {
				displayText: 'Allele Symbol',
			},
			curie: 'CURIE',
		};

		const result = render(<GenomicEntityTemplate genomicEntity={genomicEntity} />);

		const content = result.getByText('Allele Symbol (CURIE)');
		expect(content).toBeInTheDocument();
	});

	it('should render genomicEntity text and id when genomicEntity has geneFullName', () => {
		const genomicEntity = {
			geneFullName: {
				displayText: 'Gene Full Name',
			},
			curie: 'CURIE',
		};

		const result = render(<GenomicEntityTemplate genomicEntity={genomicEntity} />);

		const content = result.getByText('Gene Full Name (CURIE)');
		expect(content).toBeInTheDocument();
	});

	it('should render genomicEntity text and id when genomicEntity has alleleFullName', () => {
		const genomicEntity = {
			alleleFullName: {
				displayText: 'Allele Full Name',
			},
			curie: 'CURIE',
		};

		const result = render(<GenomicEntityTemplate genomicEntity={genomicEntity} />);

		const content = result.getByText('Allele Full Name (CURIE)');
		expect(content).toBeInTheDocument();
	});
	it('should render genomicEntity name and id when genomicEntity has name', () => {
		const genomicEntity = {
			name: 'genomicEntity Name',
			primaryExternalId: 'ID',
		};

		const result = render(<GenomicEntityTemplate genomicEntity={genomicEntity} />);

		const content = result.getByText('genomicEntity Name (ID)');
		expect(content).toBeInTheDocument();
	});

	it('should render genomicEntity id in a div when genomicEntity has no displayable text', () => {
		const genomicEntity = {
			curie: 'CURIE',
		};

		const result = render(<GenomicEntityTemplate genomicEntity={genomicEntity} />);

		const divContent = result.getByText('CURIE');
		expect(divContent).toBeInTheDocument();
	});

	it('should render null when genomicEntity is null', () => {
		const genomicEntity = null;

		const { container } = render(<GenomicEntityTemplate genomicEntity={genomicEntity} />);

		expect(container.firstChild).toBeNull();
	});

	it('should render <sup> tags in the HTML', async () => {
		const genomicEntity = {
			geneSymbol: {
				displayText: 'Gene <sup>Symbol</sup>',
			},
			primaryExternalId: 'ID',
		};

		const { container } = render(<GenomicEntityTemplate genomicEntity={genomicEntity} />);

		let superScript = container.querySelector('sup');
		expect(superScript).toBeInTheDocument();
	});

	it('should link to the detail page when detailPage is provided', () => {
		const genomicEntity = {
			alleleSymbol: {
				displayText: 'Allele Symbol',
			},
			primaryExternalId: 'ZFIN:ZDB-ALT-120806-2732',
		};

		const result = render(
			<MemoryRouter>
				<GenomicEntityTemplate genomicEntity={genomicEntity} detailPage="allele" />
			</MemoryRouter>
		);

		const link = result.getByText('Allele Symbol (ZFIN:ZDB-ALT-120806-2732)').closest('a');
		expect(link).toHaveAttribute('href', '/allele/ZFIN:ZDB-ALT-120806-2732');
		expect(link).toHaveAttribute('target', '_blank');
	});

	it('should link to the detail page when genomicEntity has no displayable text', () => {
		const genomicEntity = {
			curie: 'CURIE',
		};

		const result = render(
			<MemoryRouter>
				<GenomicEntityTemplate genomicEntity={genomicEntity} detailPage="allele" />
			</MemoryRouter>
		);

		expect(result.getByText('CURIE').closest('a')).toHaveAttribute('href', '/allele/CURIE');
	});

	it('should not link when detailPage is not provided', () => {
		const genomicEntity = {
			alleleSymbol: {
				displayText: 'Allele Symbol',
			},
			curie: 'CURIE',
		};

		const { container } = render(<GenomicEntityTemplate genomicEntity={genomicEntity} />);

		expect(container.querySelector('a')).toBeNull();
	});
});
