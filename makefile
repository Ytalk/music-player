run:
	java -jar target/Apolo.jar


lcompile:
	./mvnw clean install

wcompile:
	mvnw.cmd clean install


lpermission:
	chmod +x mvnw


wreset:
	mvnw.cmd clean install

lreset:
	./mvnw clean install