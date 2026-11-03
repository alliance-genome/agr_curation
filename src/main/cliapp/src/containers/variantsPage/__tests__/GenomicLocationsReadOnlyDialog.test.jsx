import { render } from '@testing-library/react';
import '../../../tools/jest/setupTests';
import { GenomicLocationsReadOnlyDialog } from '../genomicLocations/GenomicLocationsReadOnlyDialog';
import { getSequenceOfReferenceAccessionNumber } from '../genomicLocations/GenomicLocationsTable';

const genomicLocations = [
	{
		id: 1,
		relation: { name: 'located_on' },
		variantGenomicLocationAssociationObject: {
			name: 'X',
			genomeAssembly: { primaryExternalId: 'mRatBN7.2' },
		},
		variationStrand: '+',
		start: 142182614,
		end: 142182620,
		hgvs: 'NC_051353.1:g.142182614_142182620delinsATGCTA',
		referenceSequence: 'TTGCAC',
		variantSequence: 'ATGCTA',
		consequence: { name: 'delins', curie: 'SO:1000032' },
		curatedConsequence: { name: 'insertion', curie: 'SO:0000667' },
		numberAdditionalDnaBasePairs: 125,
		numberRemovedDnaBasePairs: 0,
		insertedSequence: 'GGCATA',
		deletedSequence: 'TTAGGATCA',
		paddedBase: 'A',
		dnaMutationType: { name: 'deletion', curie: 'SO:0000159' },
		geneLocalizationType: { name: 'intron', curie: 'SO:0000188' },
		evidence: [{ curie: 'AGRKB:101000000000001', crossReferences: [{ referencedCurie: 'PMID:123456' }] }],
	},
];

describe('GenomicLocationsReadOnlyDialog', () => {
	it('renders all genomic location properties when open', () => {
		const result = render(
			<GenomicLocationsReadOnlyDialog
				genomicLocationsData={{ genomicLocations, dialog: true }}
				setGenomicLocationsData={vi.fn()}
			/>
		);

		expect(result.getByText('Variant Genomic Locations')).toBeInTheDocument();
		expect(result.getByText('located_on')).toBeInTheDocument();
		expect(result.getByText('X')).toBeInTheDocument();
		expect(result.getByText('mRatBN7.2')).toBeInTheDocument();
		expect(result.getByText('+')).toBeInTheDocument();
		expect(result.getByText('142182614')).toBeInTheDocument();
		expect(result.getByText('142182620')).toBeInTheDocument();
		expect(result.getByText('NC_051353.1:g.142182614_142182620delinsATGCTA')).toBeInTheDocument();
		expect(result.getByText('RefSeq:NC_051353.1')).toBeInTheDocument();
		expect(result.getByText('TTGCAC')).toBeInTheDocument();
		expect(result.getByText('ATGCTA')).toBeInTheDocument();
		expect(result.getByText('delins (SO:1000032)')).toBeInTheDocument();
		expect(result.getByText('insertion (SO:0000667)')).toBeInTheDocument();
		expect(result.getByText('125')).toBeInTheDocument();
		expect(result.getByText('0')).toBeInTheDocument();
		expect(result.getByText('GGCATA')).toBeInTheDocument();
		expect(result.getByText('TTAGGATCA')).toBeInTheDocument();
		expect(result.getByText('A')).toBeInTheDocument();
		expect(result.getByText('deletion (SO:0000159)')).toBeInTheDocument();
		expect(result.getByText('intron (SO:0000188)')).toBeInTheDocument();
		expect(result.getByText(/PMID:123456/)).toBeInTheDocument();
	});

	it('renders nothing when closed', () => {
		const result = render(
			<GenomicLocationsReadOnlyDialog
				genomicLocationsData={{ genomicLocations, dialog: false }}
				setGenomicLocationsData={vi.fn()}
			/>
		);

		expect(result.queryByText('Variant Genomic Locations')).not.toBeInTheDocument();
	});
});

describe('getSequenceOfReferenceAccessionNumber', () => {
	it('derives the RefSeq accession from the HGVS name', () => {
		expect(getSequenceOfReferenceAccessionNumber('NC_003279.8:g.1A>T')).toBe('RefSeq:NC_003279.8');
	});

	it('returns null for missing or malformed HGVS', () => {
		expect(getSequenceOfReferenceAccessionNumber(null)).toBeNull();
		expect(getSequenceOfReferenceAccessionNumber('NC_003279.8')).toBeNull();
	});
});
