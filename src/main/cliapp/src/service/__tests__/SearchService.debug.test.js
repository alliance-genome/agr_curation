import { SearchService } from '../SearchService';

const searchDebugFlag = async (siteSettings) => {
	if (siteSettings === undefined) localStorage.removeItem('siteSettings');
	else localStorage.setItem('siteSettings', JSON.stringify(siteSettings));
	const searchService = new SearchService();
	const post = vi.fn(() => Promise.resolve({ data: { results: [] } }));
	searchService.api = { post };

	await searchService.search('gene', 10, 0, [], {});

	return post.mock.calls[0][1].debug;
};

describe('SearchService.search debug flag', () => {
	afterEach(() => localStorage.removeItem('siteSettings'));

	it.each([
		[{ debug: true }, 'true'],
		[{ debug: 'true' }, 'true'],
		[{ debug: false }, 'false'],
		[{ debug: 'false' }, 'false'],
		[undefined, 'false'],
	])('site settings %j send debug %s', async (siteSettings, expected) => {
		expect(await searchDebugFlag(siteSettings)).toBe(expected);
	});
});
