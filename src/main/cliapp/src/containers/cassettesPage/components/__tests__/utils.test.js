import { mapRowErrors, markRowsWithTypedObject, prepareComponentRows, stripComponentRow } from '../utils';

describe('component pop-up helpers', () => {
	it('gives each row its own key without changing the rows', () => {
		const rows = [{ id: 1 }, { id: 2 }];
		const prepared = prepareComponentRows(rows);
		expect(prepared[0].dataKey).toBeTruthy();
		expect(prepared[0].dataKey).not.toBe(prepared[1].dataKey);
		expect(rows[0]).not.toHaveProperty('dataKey');
	});

	it('leaves out the key and the cassette a row belongs to', () => {
		expect(
			stripComponentRow({
				dataKey: 'k',
				id: 1,
				cassetteAssociationSubject: { id: 7 },
				singleCassette: { id: 7 },
				relation: {},
			})
		).toEqual({ id: 1, relation: {} });
	});

	it('marks rows whose entity was typed rather than picked', () => {
		const rows = [
			{ dataKey: 'a', tool: { id: 12 } },
			{ dataKey: 'b', tool: { primaryExternalId: 'typed' } },
			{ dataKey: 'c', tool: null },
		];
		const marked = markRowsWithTypedObject(rows, {}, 'tool');
		expect(Object.keys(marked)).toEqual(['b']);
		expect(marked.b.tool.message).toBe('Select from the autosuggest');
		expect(markRowsWithTypedObject([rows[0]], {}, 'tool')).toBeNull();
	});

	it("maps the API's index keyed errors onto the rows", () => {
		const rows = [{ dataKey: 'a' }, { dataKey: 'b' }];
		const data = { supplementalData: { errorMap: { list: { 1: { relation: 'Required field is empty' } } } } };
		expect(mapRowErrors(data, 'list', rows)).toEqual({
			b: { relation: { severity: 'error', message: 'Required field is empty' } },
		});
		expect(mapRowErrors({}, 'list', rows)).toEqual({});
	});
});
