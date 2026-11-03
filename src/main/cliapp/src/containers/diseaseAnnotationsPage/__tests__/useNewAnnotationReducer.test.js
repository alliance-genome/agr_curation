import { act, renderHook } from '@testing-library/react';
import { useNewAnnotationReducer } from '../useNewAnnotationReducer';

const setUp = () => renderHook(() => useNewAnnotationReducer());

const dispatch = (result, action) => act(() => result.current.newAnnotationDispatch(action));

const notes = (result) => result.current.newAnnotationState.newAnnotation.relatedNotes;

describe('useNewAnnotationReducer', () => {
	it('gives each new note and condition relation its own key, open for editing', () => {
		const { result } = setUp();

		dispatch(result, { type: 'ADD_NEW_NOTE' });
		dispatch(result, { type: 'ADD_NEW_NOTE' });
		dispatch(result, { type: 'ADD_NEW_RELATION' });

		const state = result.current.newAnnotationState;
		const noteKeys = state.newAnnotation.relatedNotes.map((note) => note.dataKey);
		const [relationKey] = state.newAnnotation.conditionRelations.map((relation) => relation.dataKey);
		expect(new Set([...noteKeys, relationKey]).size).toBe(3);
		expect(Object.keys(state.relatedNotesEditingRows)).toEqual(noteKeys);
		expect(Object.keys(state.conditionRelationsEditingRows)).toEqual([relationKey]);
	});

	it('gives a note added after a delete a key no remaining note has', () => {
		const { result } = setUp();
		dispatch(result, { type: 'ADD_NEW_NOTE' });
		dispatch(result, { type: 'ADD_NEW_NOTE' });
		const [first] = notes(result).map((note) => note.dataKey);

		dispatch(result, { type: 'DELETE_ROW', tableType: 'relatedNotes', showType: 'showRelatedNotes', dataKey: first });
		dispatch(result, { type: 'ADD_NEW_NOTE' });

		const keys = notes(result).map((note) => note.dataKey);
		expect(keys).toHaveLength(2);
		expect(new Set(keys).size).toBe(2);
	});

	it('edits and deletes a row by its key', () => {
		const { result } = setUp();
		dispatch(result, { type: 'ADD_NEW_NOTE' });
		dispatch(result, { type: 'ADD_NEW_NOTE' });
		const [first, second] = notes(result).map((note) => note.dataKey);

		dispatch(result, {
			type: 'EDIT_ROW',
			tableType: 'relatedNotes',
			dataKey: second,
			field: 'freeText',
			value: 'edited',
		});
		dispatch(result, { type: 'DELETE_ROW', tableType: 'relatedNotes', showType: 'showRelatedNotes', dataKey: first });

		expect(notes(result)).toEqual([expect.objectContaining({ dataKey: second, freeText: 'edited' })]);
	});

	it('hides a table once its last row is deleted', () => {
		const { result } = setUp();
		dispatch(result, { type: 'ADD_NEW_RELATION' });
		const [relation] = result.current.newAnnotationState.newAnnotation.conditionRelations;

		dispatch(result, {
			type: 'DELETE_ROW',
			tableType: 'conditionRelations',
			showType: 'showConditionRelations',
			dataKey: relation.dataKey,
		});

		expect(result.current.newAnnotationState.showConditionRelations).toBe(false);
	});

	it('gives duplicated notes and condition relations fresh keys and opens them for editing', () => {
		const { result } = setUp();
		const rowData = {
			relatedNotes: [
				{ id: 1, freeText: 'a' },
				{ id: 2, freeText: 'b' },
			],
			conditionRelations: [{ id: 3, conditions: [] }],
		};

		dispatch(result, { type: 'DUPLICATE_ROW', rowData });
		dispatch(result, { type: 'SET_RELATED_NOTES_EDITING_ROWS' });
		dispatch(result, { type: 'SET_CONDITION_RELATIONS_EDITING_ROWS' });

		const state = result.current.newAnnotationState;
		const noteKeys = state.newAnnotation.relatedNotes.map((note) => note.dataKey);
		const relationKeys = state.newAnnotation.conditionRelations.map((relation) => relation.dataKey);
		expect(new Set([...noteKeys, ...relationKeys]).size).toBe(3);
		expect(noteKeys.every(Boolean)).toBe(true);
		expect(Object.keys(state.relatedNotesEditingRows)).toEqual(noteKeys);
		expect(Object.keys(state.conditionRelationsEditingRows)).toEqual(relationKeys);
		expect(state.showRelatedNotes && state.showConditionRelations).toBe(true);
		expect(rowData.relatedNotes[0]).not.toHaveProperty('dataKey');
	});
});
