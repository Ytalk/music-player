run:
	./mvnw.cmd exec:run

compile:
	./mvnw clean install

lpermission:
	chmod +x mvnw

reset:
	./mvnw clean install