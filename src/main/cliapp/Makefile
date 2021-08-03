all:
	npm install

start:
	npm start

build:
	npm run-script build
	cp build/index.html ../webapp
	rm -fr ../webapp/assets
	rm -fr ../webapp/static
	mv build/assets ../webapp
	mv build/static ../webapp
