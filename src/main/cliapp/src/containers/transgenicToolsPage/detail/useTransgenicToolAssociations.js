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
	 * A tool typed rather than picked from the autosuggest has no id. While any row holds one, those rows are marked
	 * and a refusal is returned; otherwise null. The create page checks this before it creates the tool, so a tool
	 * is not created whose associations could only fail.
	 *
	 * @returns {{isSuccess: false, message: string}|null}
	 */
	const refuseTypedTools = useCallback(() => {
		if (isLoading || loadError) return null;
		const typedRows = rows.filter((row) => row[OBJECT_FIELD] && !row[OBJECT_FIELD].id);
		if (typedRows.length === 0) return null;
		const marked = structuredClone(errorMessages);
		typedRows.forEach((row) => {
			marked[row.dataKey] = {
				...marked[row.dataKey],
				[OBJECT_FIELD]: { severity: 'error', message: 'Select the tool from the autosuggest' },
			};
		});
		setErrorMessages(marked);
		return { isSuccess: false, message: 'Select each tool from the autosuggest' };
	}, [rows, errorMessages, setErrorMessages, isLoading, loadError]);

	/**
	 * Saves the rows as `useSubResourceList` does, unless {@link refuseTypedTools} refuses them, in which case the
	 * API is not called.
	 *
	 * @param {number} [targetToolId] the tool to write to; the create page passes the tool it has just created
	 * @returns {Promise<{isSuccess: boolean, message?: string}>}
	 */
	const saveAssociations = useCallback(
		async (targetToolId) => refuseTypedTools() ?? save(targetToolId),
		[refuseTypedTools, save]
	);

	return {
		rows,
		errorMessages,
		setErrorMessages,
		save: saveAssociations,
		refuseTypedTools,
		isLoading,
		loadError,
		...subResource,
	};
};
