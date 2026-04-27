import { useEffect, useState } from 'react';

export function useSyncedState(externalValue) {
	const [value, setValue] = useState(externalValue);

	useEffect(() => {
		setValue(externalValue);
	}, [externalValue]);

	return [value, setValue];
}
