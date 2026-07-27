import { DiseaseAnnotationsTable } from './DiseaseAnnotationsTable';
import { useSpeciesTaxa } from '../../service/useSpeciesTaxa';

export function DiseaseAnnotationsPage() {
	// Load the Species table once so the autocomplete species narrowing (Subject,
	// genetic-modifier, asserted fields) can map the curator's MOD to its taxa.
	useSpeciesTaxa();
	return <DiseaseAnnotationsTable />;
}
