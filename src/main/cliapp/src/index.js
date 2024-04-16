import 'react-app-polyfill/ie11';
import React from 'react';
import { createRoot } from 'react-dom/client';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { ReactQueryDevtools } from '@tanstack/react-query-devtools';
import App from './App';
import { HashRouter } from 'react-router-dom';
import ScrollToTop from './ScrollToTop';

const queryClient = new QueryClient();

const container = document.getElementById("root");

if (!container) throw new Error("container not found!");

const root = createRoot(container);

root.render(
	<HashRouter>
		<ScrollToTop>
			<QueryClientProvider client={queryClient}>
				<App />
				<ReactQueryDevtools />
			</QueryClientProvider>
		</ScrollToTop>
	</HashRouter>
);

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: http://bit.ly/CRA-PWA
//serviceWorker.unregister();
