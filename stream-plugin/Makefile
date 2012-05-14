#
#
#
VERSION=0.4-SNAPSHOT
RAPIDMINER_HOME=../../RapidMiner_Unuk
STREAMS_HOME=..

STREAM_API_HOME=$(STREAMS_HOME)/stream-api

compile-api:
	@cd $(STREAM_API_HOME) && mvn -DskipTests=true install
	@cd ../stream-plugin

plugin:
	@echo "Building streams-plugin..."
	@mvn -DskipTests=true compile package 
	@echo ""
	@echo "The plugin can be found in \n"
	@echo "\t$(PWD)/target/DataStream-Plugin-$(VERSION).jar"
	@echo ""

install:
	@echo "Installing plugin in $(RAPIDMINER_HOME)/lib/plugins"
	@cp target/DataStream-Plugin-$(VERSION).jar $(RAPIDMINER_HOME)/lib/plugins

all:	plugin

clean:
	@echo "Cleaning project directory..."
	@mvn clean >> /dev/null

clean-cache:
	@echo "Removing streams artifacts from local Maven cache..."
	@rm -rf $(HOME)/.m2/repository/org/jwall/stream*
