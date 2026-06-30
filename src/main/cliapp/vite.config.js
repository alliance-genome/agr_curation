import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
	plugins: [react()],
	base: './',
	build: {
		outDir: 'build',
		assetsDir: 'static',
	},
	server: {
		port: 3000,
		open: true,
		proxy: {
			'/api': {
				target: process.env.API_URL || 'http://127.0.0.1:8080',
				changeOrigin: true,
			},
		},
	},
	test: {
		environment: 'jsdom',
		globals: true,
		setupFiles: ['./src/tools/jest/setupTests.js'],
		css: false,
	},
});
