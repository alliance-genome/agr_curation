import { useCallback, useMemo } from 'react';
import { CassetteService } from '../../../service/CassetteService';
import { useSubResourceList } from '../../../hooks/useSubResourceList';
import { markRowsWithTypedObject, prepareComponentRows, stripComponentRow } from '../components/utils';
import { haveSameComponentRows } from './utils';

/**
 * One of a cassette's component lists on the cassette detail page (SCRUM-6747), read and written through its own
 * sub-resource (/cassette/{id}/{list}) rather than with the cassette, as the Cassettes table's pop-up does.
 *
 * @param {number} [cassetteId] the cassette to read on load
 * @param {object} config the list, from componentConfigs
 */
export const useCassetteComponentList = (cassetteId, config) => {
	const cassetteService = useMemo(() => new CassetteService(), []);
	const load = useCallback((id) => cassetteService.getComponentList(id, config.list), [cassetteService, config.list]);
	const replace = useCallback(
		(id, rows) => cassetteService.replaceComponentList(id, config.list, rows),
		[cassetteService, config.list]
	);

	const { rows, errorMessages, setErrorMessages, save, isLoading, loadError, ...subResource } = useSubResourceList({
		ownerId: cassetteId,
		label: config.title,
		errorField: config.field,
		load,
		replace,
		prepare: prepareComponentRows,
		strip: stripComponentRow,
		isSame: haveSameComponentRows,
	});

	/**
	 * Saves the rows as `useSubResourceList` does. An entity typed rather than picked from the autosuggest has no id;
	 * while any row holds one, the save is refused without calling the API and those rows are marked.
	 *
	 * @returns {Promise<{isSuccess: boolean, message?: string}>}
	 */
	const saveComponents = useCallback(async () => {
		if (!isLoading && !loadError && config.objectField) {
			const marked = markRowsWithTypedObject(rows, errorMessages, config.objectField);
			if (marked) {
				setErrorMessages(marked);
				return { isSuccess: false, message: 'Select each entity from the autosuggest' };
			}
		}
		return save();
	}, [rows, errorMessages, setErrorMessages, isLoading, loadError, save, config.objectField]);

	return { rows, errorMessages, setErrorMessages, save: saveComponents, isLoading, loadError, ...subResource };
};
