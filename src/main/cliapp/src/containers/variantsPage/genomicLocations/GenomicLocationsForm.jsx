import { FormTableWrapper } from '../../../components/FormTableWrapper';
import { GenomicLocationsTable } from './GenomicLocationsTable';

export const GenomicLocationsForm = ({ state }) => {
	return (
		<FormTableWrapper
			table={
				<GenomicLocationsTable
					genomicLocations={state.variant?.curatedVariantGenomicLocations ?? []}
					emptyMessage="No genomic locations"
				/>
			}
			tableName="Genomic Locations"
			showTable={true}
		/>
	);
};
