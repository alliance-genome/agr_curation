import { useRef } from 'react';
import { Button } from 'primereact/button';
import { Toast } from 'primereact/toast';
import { useSubResource } from '../../components/SubResourcesContext';
import { useSaveProgressContext } from '../../components/SaveProgressOverlay';
import { ALLELE_SUB_RESOURCES } from './constants';
import { capitalize } from './utils';

const titleCase = (text) => text.split(' ').map(capitalize).join(' ');

/**
 * A detail page section's own Save, which writes its list alone and reports how that went, in the page's save
 * progress overlay when the page shows one.
 *
 * @param {Object} props
 * @param {keyof typeof ALLELE_SUB_RESOURCES} props.type which list
 */
export const SubResourceSaveButton = ({ type }) => {
	const { isDirty, isSaving, isLoading, loadError, save } = useSubResource(type);
	const { label } = ALLELE_SUB_RESOURCES[type];
	const toast = useRef(null);
	const saveProgress = useSaveProgressContext();

	const saveHandler = async (event) => {
		event.preventDefault();
		saveProgress?.begin([{ key: type, label: capitalize(label) }]);
		let outcome;
		try {
			saveProgress?.start(type);
			outcome = await save();
			saveProgress?.settle(type, outcome);
		} finally {
			saveProgress?.skipRemaining();
			saveProgress?.finish();
		}

		if (outcome.isSuccess) {
			toast.current.show({ severity: 'success', summary: 'Successful', detail: `${titleCase(label)} Saved` });
		} else {
			toast.current.show([
				{ life: 7000, severity: 'error', summary: `${capitalize(label)}: `, detail: outcome.message, sticky: false },
			]);
		}
	};

	return (
		<>
			<Toast ref={toast} position="top-right" />
			<Button
				label={`Save ${titleCase(label)}`}
				icon={isSaving ? 'pi pi-spin pi-spinner' : 'pi pi-check'}
				onClick={saveHandler}
				// Saving replaces the stored list with what is on screen, so it has to wait for the read. Saving a table
				// that is still loading, or that failed to load, would submit an empty list and remove every row the
				// allele has. With nothing edited there is nothing to save, and nothing is started while another save
				// is writing.
				disabled={!isDirty || isSaving || isLoading || Boolean(loadError) || Boolean(saveProgress?.isSaving)}
				severity="success"
			/>
		</>
	);
};
