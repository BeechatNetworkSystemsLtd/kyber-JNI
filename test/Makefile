

.PHONY: all clear

all: clean
	@mkdir bin
	@javac -d bin network/beechat/*.java
	@jar -cmf manifest.mf asap.jar -C ./bin .

clean:
	@rm -rf bin

run:
	java -Djava.library.path=../build/Release/ -jar asap.jar

