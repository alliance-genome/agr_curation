all:
	npm install

run:
	npm start

build:
	npm run-script build
	cp build/index.html ../resources/META-INF/resources
	rm -fr ../resources/META-INF/resources/assets
	rm -fr ../resources/META-INF/resources/static
	mv build/assets ../resources/META-INF/resources
	mv build/static ../resources/META-INF/resources
