import {
	AFFILIATION_OVERRIDE_KEY,
	canSwitchAffiliation,
	getEffectiveModAbbreviation,
	getEffectiveStaffGroups,
	getTrueStaffGroups,
	isAffiliationOverridden,
} from '../affiliation';

// SCRUM-2831: client-side MOD affiliation switching for Tester / POTester users.

const setCognitoGroups = (groups) => {
	localStorage.setItem(
		'cognito-token-storage',
		JSON.stringify({ accessToken: { payload: { 'cognito:groups': groups } } })
	);
};

describe('getTrueStaffGroups', () => {
	afterEach(() => localStorage.clear());

	it('returns only the *Staff groups from the cognito token', () => {
		setCognitoGroups(['WBStaff', 'Tester', 'SomethingElse']);
		expect(getTrueStaffGroups()).toEqual(['WBStaff']);
	});

	it('returns [] when there is no cognito token', () => {
		localStorage.removeItem('cognito-token-storage');
		expect(getTrueStaffGroups()).toEqual([]);
	});
});

describe('getEffectiveStaffGroups', () => {
	afterEach(() => localStorage.clear());

	it('returns the true staff groups when no override is set', () => {
		setCognitoGroups(['WBStaff', 'Tester']);
		expect(getEffectiveStaffGroups()).toEqual(['WBStaff']);
	});

	it('returns the override (as a single-element array) when one is set', () => {
		setCognitoGroups(['WBStaff', 'Tester']);
		localStorage.setItem(AFFILIATION_OVERRIDE_KEY, 'RGDStaff');
		expect(getEffectiveStaffGroups()).toEqual(['RGDStaff']);
	});
});

describe('getEffectiveModAbbreviation', () => {
	afterEach(() => localStorage.clear());

	it('maps the effective staff group to its MOD abbreviation', () => {
		setCognitoGroups(['WBStaff', 'Tester']);
		expect(getEffectiveModAbbreviation()).toEqual('WB');
	});

	it('follows an override', () => {
		setCognitoGroups(['WBStaff', 'Tester']);
		localStorage.setItem(AFFILIATION_OVERRIDE_KEY, 'RGDStaff');
		expect(getEffectiveModAbbreviation()).toEqual('RGD');
	});

	it("falls back to 'Alliance' when the user has no MOD group", () => {
		setCognitoGroups(['Tester']);
		expect(getEffectiveModAbbreviation()).toEqual('Alliance');
	});

	it("falls back to 'Alliance' when there is no cognito token", () => {
		localStorage.removeItem('cognito-token-storage');
		expect(getEffectiveModAbbreviation()).toEqual('Alliance');
	});
});

describe('isAffiliationOverridden', () => {
	afterEach(() => localStorage.clear());

	it('is false when no override is set', () => {
		setCognitoGroups(['WBStaff']);
		expect(isAffiliationOverridden()).toBe(false);
	});

	it('is false when the override matches the true affiliation', () => {
		setCognitoGroups(['WBStaff']);
		localStorage.setItem(AFFILIATION_OVERRIDE_KEY, 'WBStaff');
		expect(isAffiliationOverridden()).toBe(false);
	});

	it('is true when the override differs from the true affiliation', () => {
		setCognitoGroups(['WBStaff']);
		localStorage.setItem(AFFILIATION_OVERRIDE_KEY, 'RGDStaff');
		expect(isAffiliationOverridden()).toBe(true);
	});
});

describe('canSwitchAffiliation', () => {
	it('lets POTester switch on any environment, including production', () => {
		expect(canSwitchAffiliation({ groups: ['POTester'], env: 'production' })).toBe(true);
		expect(canSwitchAffiliation({ groups: ['POTester'], env: 'alpha' })).toBe(true);
		expect(canSwitchAffiliation({ groups: ['POTester'], env: 'beta' })).toBe(true);
	});

	it('lets Tester switch on alpha/beta/local but not production', () => {
		expect(canSwitchAffiliation({ groups: ['Tester'], env: 'alpha' })).toBe(true);
		expect(canSwitchAffiliation({ groups: ['Tester'], env: 'beta' })).toBe(true);
		expect(canSwitchAffiliation({ groups: ['Tester'], env: 'local' })).toBe(true);
		expect(canSwitchAffiliation({ groups: ['Tester'], env: 'production' })).toBe(false);
	});

	it('lets Tester switch in local dev regardless of env', () => {
		expect(canSwitchAffiliation({ groups: ['Tester'], env: 'production', isDev: true })).toBe(true);
		expect(canSwitchAffiliation({ groups: ['Tester'], env: undefined, isDev: true })).toBe(true);
	});

	it('does not let other users switch', () => {
		expect(canSwitchAffiliation({ groups: ['WBStaff'], env: 'alpha' })).toBe(false);
		expect(canSwitchAffiliation({ groups: [], env: 'alpha' })).toBe(false);
		expect(canSwitchAffiliation({})).toBe(false);
	});
});
