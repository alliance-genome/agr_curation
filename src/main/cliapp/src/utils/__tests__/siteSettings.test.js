import { isDebugOn } from '../siteSettings';

describe('isDebugOn', () => {
	it.each([
		[{ debug: true }, true],
		[{ debug: 'true' }, true],
		[{ debug: false }, false],
		[{ debug: 'false' }, false],
		[{}, false],
		[null, false],
	])('reads %j as %s', (siteSettings, expected) => {
		expect(isDebugOn(siteSettings)).toBe(expected);
	});
});
