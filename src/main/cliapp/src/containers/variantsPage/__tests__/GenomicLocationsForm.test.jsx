import { render } from '@testing-library/react';
import '../../../tools/jest/setupTests';
import { GenomicLocationsForm } from '../genomicLocations/GenomicLocationsForm';

const genomicLocation = {
	id: 1,
	relation: { name: 'located_on' },
	variantGenomicLocationAssociationObject: {
		name: 'X',
		genomeAssembly: { primaryExternalId: 'mRatBN7.2' },
	},
	start: 142182614,
	end: 142182620,
	hgvs: 'NC_051353.1:g.142182614_142182620delinsATGCTA',
	consequence: { name: 'delins', curie: 'SO:1000032' },
	evidence: [],
};

describe('GenomicLocationsForm', () => {
	it('renders a row per curated genomic location', () => {
		const result = render(
			<GenomicLocationsForm state={{ variant: { curatedVariantGenomicLocations: [genomicLocation] } }} />
		);

		expect(result.getByText('Genomic Locations')).toBeInTheDocument();
		expect(result.getByText('Sequence of Reference Accession Number')).toBeInTheDocument();
		expect(result.getByText('located_on')).toBeInTheDocument();
		expect(result.getByText('mRatBN7.2')).toBeInTheDocument();
		expect(result.getByText('142182614')).toBeInTheDocument();
		expect(result.getByText('RefSeq:NC_051353.1')).toBeInTheDocument();
		expect(result.getByText('delins (SO:1000032)')).toBeInTheDocument();
	});

	it('shows an empty message when the variant has no genomic locations', () => {
		const result = render(<GenomicLocationsForm state={{ variant: {} }} />);

		expect(result.getByText('No genomic locations')).toBeInTheDocument();
	});
});
