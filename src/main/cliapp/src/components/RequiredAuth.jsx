import { useEffect, useState } from 'react';
import { Outlet, useNavigate } from 'react-router-dom';
import { getCurrentUser } from 'aws-amplify/auth';

const Loading = () => <div>Loading authentication...</div>;

export const RequiredAuth = () => {
	const [isAuthenticated, setIsAuthenticated] = useState(null);
	const navigate = useNavigate();

	useEffect(() => {
		const checkAuth = async () => {
			try {
				await getCurrentUser();
				setIsAuthenticated(true);
			} catch (error) {
				console.error('Auth check failed:', error);
				setIsAuthenticated(false);
				navigate('/login');
			}
		};

		checkAuth();
	}, [navigate]);

	if (isAuthenticated === null || isAuthenticated === false) {
		return <Loading />;
	}

	return <Outlet />;
};
