import { Dialog } from 'primereact/dialog';
import { GenomicLocationsTable } from './GenomicLocationsTable';

export const GenomicLocationsReadOnlyDialog = ({ genomicLocationsData, setGenomicLocationsData }) => {
	const { genomicLocations, dialog } = genomicLocationsData;

	const hideDialog = () => {
		setGenomicLocationsData((prev) => ({
			...prev,
			dialog: false,
		}));
	};

	return (
		<Dialog visible={dialog} className="w-11" modal onHide={hideDialog} closable header="Variant Genomic Locations">
			<GenomicLocationsTable genomicLocations={genomicLocations} />
		</Dialog>
	);
};
