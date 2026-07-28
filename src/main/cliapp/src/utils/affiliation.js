// SCRUM-2831: client-side MOD affiliation switching for "Tester" / "POTester"
// Cognito group members. The user's real affiliation (the `*Staff` cognito group)
// remains the source of truth; this module layers a purely client-side override on
// top so testers can simulate another MOD's curation UI. The override is stored in
// localStorage and consulted by the MOD-resolution chokepoints
// (getModTableState / getModFormFields / NewAnnotationForm) via getEffectiveStaffGroups().

export const AFFILIATION_OVERRIDE_KEY = 'affiliation-override';

const COGNITO_TOKEN_KEY = 'cognito-token-storage';

// Cognito groups that are allowed to switch affiliation, and the environments each
// is allowed to switch in. POTester can switch anywhere (incl. production); Tester
// is limited to the non-production A-Team environments and local dev. 'local' is the
// backend `NET` value reported by /version when running against a local API, so it
// covers local development whether or not the UI is served by the Vite dev server.
export const TESTER_GROUP = 'Tester';
export const POTESTER_GROUP = 'POTester';
const TESTER_ENVS = ['alpha', 'beta', 'local'];

// The MODs a tester can switch to. `group` matches the `*Staff` cognito group the
// rest of the app keys off of; `abbreviation` is the human-facing MOD label.
export const MOD_AFFILIATIONS = ['RGD', 'SGD', 'WB', 'FB', 'ZFIN', 'XB', 'MGI'].map((abbreviation) => ({
	abbreviation,
	group: `${abbreviation}Staff`,
}));

// Raw `cognito:groups` claim from the stored access token (e.g. ['WBStaff', 'Tester']).
export function getCognitoGroups() {
	try {
		const cognitoToken = JSON.parse(localStorage.getItem(COGNITO_TOKEN_KEY));
		return cognitoToken?.accessToken?.payload?.['cognito:groups'] || [];
	} catch (e) {
		return [];
	}
}

// The user's true MOD affiliation: the `*Staff` cognito groups (unaffected by any override).
export function getTrueStaffGroups() {
	return getCognitoGroups().filter((group) => group.includes('Staff'));
}

// The currently selected override `*Staff` group, or null when none is set.
export function getAffiliationOverride() {
	return localStorage.getItem(AFFILIATION_OVERRIDE_KEY) || null;
}

export function setAffiliationOverride(group) {
	localStorage.setItem(AFFILIATION_OVERRIDE_KEY, group);
}

export function clearAffiliationOverride() {
	localStorage.removeItem(AFFILIATION_OVERRIDE_KEY);
}

// The effective `*Staff` groups the app should use: the override (as a single-element
// array) when set, otherwise the user's true affiliation. This is the single value the
// MOD-resolution chokepoints read so an override transparently retargets the UI.
export function getEffectiveStaffGroups() {
	const override = getAffiliationOverride();
	return override ? [override] : getTrueStaffGroups();
}

// True when an override is active and actually differs from the user's true affiliation.
export function isAffiliationOverridden() {
	const override = getAffiliationOverride();
	return !!override && !getTrueStaffGroups().includes(override);
}

// Whether the current user may switch affiliation, given their cognito groups and the
// running environment. POTester -> any env; Tester -> alpha/beta or local dev; else no.
export function canSwitchAffiliation({ groups = [], env, isDev = false } = {}) {
	if (groups.includes(POTESTER_GROUP)) return true;
	if (groups.includes(TESTER_GROUP)) return isDev || TESTER_ENVS.includes(env);
	return false;
}
