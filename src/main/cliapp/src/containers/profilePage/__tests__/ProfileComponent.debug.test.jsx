import { screen } from '@testing-library/react';
import { renderWithClient } from '../../../tools/jest/utils';
import { pickOption } from '../../../components/Editors/widgets/__tests__/widgetTestUtils';

const setSiteSettings = vi.fn();
let siteSettings;

vi.mock('../../../hooks/useAuth', () => ({ useAuth: () => ({ authState: {} }) }));
vi.mock('../../../service/SiteQueryHooks', () => ({
	QUERY_KEYS: { USER_INFO: 'userInfo' },
	useUserInfo: () => ({ data: { firstName: 'Ada', lastName: 'Curator', settings: [] } }),
	useApiVersion: () => ({ data: { env: 'test' } }),
	useRegenApiToken: () => vi.fn(),
}));
vi.mock('../../../contexts/AffiliationContext', () => ({
	useAffiliation: () => ({ override: null, setOverride: vi.fn(), reset: vi.fn() }),
}));
vi.mock('../../../service/useGetUserSettings', () => ({
	useGetUserSettings: (key, defaults) =>
		key === 'siteSettings'
			? { settings: siteSettings, mutate: setSiteSettings }
			: { settings: defaults, mutate: vi.fn() },
}));

const { ProfileComponent } = await import('../ProfileComponent');

// The Site Settings row: the "Debug:" text and its select
const debugSetting = () => screen.findByText(/^Debug:/);

describe('<ProfileComponent /> Debug setting', () => {
	beforeEach(() => {
		setSiteSettings.mockReset();
	});

	it.each([
		['the boolean true', true, 'true'],
		["a saved 'true' string", 'true', 'true'],
		['the boolean false', false, 'false'],
		["a saved 'false' string", 'false', 'false'],
	])('shows %s as %s', async (description, debug, shown) => {
		siteSettings = { debug };
		renderWithClient(<ProfileComponent />);

		expect((await debugSetting()).querySelector('.p-dropdown-label')).toHaveTextContent(shown);
	});

	it('saves the Debug value chosen as a boolean', async () => {
		siteSettings = { debug: 'false' };
		renderWithClient(<ProfileComponent />);

		pickOption(await debugSetting(), 'true');

		expect(setSiteSettings).toHaveBeenCalledWith({ debug: true });
	});
});
