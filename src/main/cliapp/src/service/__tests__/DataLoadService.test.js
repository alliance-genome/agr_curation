import { DataLoadService } from '../DataLoadService';

const buildService = () => {
	const service = new DataLoadService();
	service.api = { post: vi.fn(), put: vi.fn() };
	return service;
};

describe('DataLoadService species payload', () => {
	it('sends the dataProvider code on create', () => {
		const service = buildService();
		service.createLoad({ type: 'BulkManualLoad', group: 1, name: 'RGD Gene', species: 'HUMAN' });
		expect(service.api.post).toHaveBeenCalledWith(
			'/bulkmanualload',
			expect.objectContaining({ species: { displayName: 'HUMAN' } })
		);
	});

	it('sends the dataProvider code on update when species is an object', () => {
		const service = buildService();
		service.updateLoad({ type: 'BulkManualLoad', group: 1, id: 5, species: { id: 351, displayName: 'XBXT' } });
		expect(service.api.put).toHaveBeenCalledWith(
			'/bulkmanualload',
			expect.objectContaining({ species: { displayName: 'XBXT' } })
		);
	});

	it('omits species when none is selected', () => {
		const service = buildService();
		service.createLoad({ type: 'BulkFMSLoad', group: 1, name: 'FMS' });
		expect(service.api.post.mock.calls[0][1]).not.toHaveProperty('species');
	});

	it('keeps the species id when updating a non-manual load', () => {
		const service = buildService();
		const species = { id: 201, displayName: 'RGD' };
		service.updateLoad({ type: 'BulkFMSLoad', group: 1, id: 7, species });
		expect(service.api.put).toHaveBeenCalledWith('/bulkfmsload', expect.objectContaining({ species }));
	});
});
