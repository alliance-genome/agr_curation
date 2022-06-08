import { useState, useEffect } from "react";

function getSessionValue(key, defaultValue) {
	const saved = sessionStorage.getItem(key);
	const initial = JSON.parse(saved);
	return initial || defaultValue;
}

export const useSessionStorage = (key, defaultValue) => {
	const [value, setValue] = useState(() => {
		return getSessionValue(key, defaultValue);
	});

	useEffect(() => {
		sessionStorage.setItem(key, JSON.stringify(value));
	}, [key, value]);

	return [value, setValue];
};
