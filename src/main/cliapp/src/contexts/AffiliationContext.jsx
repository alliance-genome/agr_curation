// SCRUM-2831: reactive wrapper around the client-side MOD affiliation override.
// The override itself lives in localStorage (see utils/affiliation.js) so the
// synchronous MOD-resolution chokepoints can read it without React; this context
// mirrors it into React state so the header banner and Profile control re-render
// the moment a tester switches or resets their affiliation.
import { createContext, useCallback, useContext, useMemo, useState } from 'react';
import {
	clearAffiliationOverride,
	getAffiliationOverride,
	getTrueStaffGroups,
	setAffiliationOverride,
} from '../utils/affiliation';

const AffiliationContext = createContext(null);

export const AffiliationProvider = ({ children }) => {
	const [override, setOverrideState] = useState(() => getAffiliationOverride());

	const setOverride = useCallback((group) => {
		setAffiliationOverride(group);
		setOverrideState(group);
	}, []);

	const reset = useCallback(() => {
		clearAffiliationOverride();
		setOverrideState(null);
	}, []);

	const value = useMemo(
		() => ({
			override,
			// Active only when an override is set and it differs from the user's true affiliation.
			isOverridden: !!override && !getTrueStaffGroups().includes(override),
			setOverride,
			reset,
		}),
		[override, setOverride, reset]
	);

	return <AffiliationContext.Provider value={value}>{children}</AffiliationContext.Provider>;
};

export const useAffiliation = () => {
	const context = useContext(AffiliationContext);
	if (!context) {
		throw new Error('useAffiliation must be used within an AffiliationProvider');
	}
	return context;
};
