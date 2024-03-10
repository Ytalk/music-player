run:
	./mvnw exec:java

compile:
	./mvnw compile

lpermission:
	chmod +x mvnw

reset:
	./mvnw clean install