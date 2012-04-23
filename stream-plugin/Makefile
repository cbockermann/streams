#
#
#
VERSION=0.3-SNAPSHOT
RAPIDMINER_HOME=/usr/local/rapidminer-5.1
STREAMS_HOME=..

STREAM_API_HOME=$(STREAMS_HOME)/stream-api

compile-api:
	cd $(STREAM_API_HOME) && mvn -DskipTests=true install
	cd ../stream-plugin

plugin:
	mvn compile package
#	mvn clean compile exec:java package

install: plugin
	cp target/DataStream-Plugin-$(VERSION).jar $(RAPIDMINER_HOME)/lib/plugins

all:	compile-api plugin

clean:
	mvn clean
