import { useCallback, useMemo } from 'react';
import { TransgenicToolService } from '../../../service/TransgenicToolService';
import { useSubResourceList } from '../../../hooks/useSubResourceList';
import { haveSameToolAssociations, prepareToolAssociations, stripToolAssociation } from './utils';

const OBJECT_FIELD = 'transgenicToolTransgenicToolAssociationObject';

/**
 * The associations a transgenic tool is the subject of, read and written through their own sub-resource
 * (/transgenic-tool/{id}/transgenic-tool-associations) rather than with the tool (SCRUM-6655).
 *
 * @param {number} [transgenicToolId] the tool to read on load
 */
export const useTransgenicToolAssociations = (transgenicToolId) => {
	const transgenicToolService = useMemo(() => new TransgenicToolService(), []);
	const load = useCallback(
		(toolId) => transgenicToolService.getTransgenicToolAssociations(toolId),
		[transgenicToolService]
	);
	const replace = useCallback(
		(toolId, associations) => transgenicToolService.replaceTransgenicToolAssociations(toolId, associations),
		[transgenicToolService]
	);

	const { rows, errorMessages, setErrorMessages, save, isLoading, loadError, ...subResource } = useSubResourceList({
		ownerId: transgenicToolId,
		label: 'tool associations',
		errorField: 'transgenicToolTransgenicToolAssociations',
		load,
		replace,
		prepare: prepareToolAssociations,
		strip: stripToolAssociation,
		isSame: haveSameToolAssociations,
	});

	/**
	 * Saves the rows as `useSubResourceList` does. A tool typed rather than picked from the autosuggest has no
	 * id; while any row holds one, the save is refused without calling the API and those rows are marked.
	 *
	 * @returns {Promise<{isSuccess: boolean, message?: string}>}
	 */
	const saveAssociations = useCallback(async () => {
		if (!isLoading && !loadError) {
			const typedRows = rows.filter((row) => row[OBJECT_FIELD] && !row[OBJECT_FIELD].id);
			if (typedRows.length > 0) {
				const marked = structuredClone(errorMessages);
				typedRows.forEach((row) => {
					marked[row.dataKey] = {
						...marked[row.dataKey],
						[OBJECT_FIELD]: { severity: 'error', message: 'Select the tool from the autosuggest' },
					};
				});
				setErrorMessages(marked);
				return { isSuccess: false, message: 'Select each tool from the autosuggest' };
			}
		}
		return save();
	}, [rows, errorMessages, setErrorMessages, isLoading, loadError, save]);

	return { rows, errorMessages, setErrorMessages, save: saveAssociations, isLoading, loadError, ...subResource };
};
