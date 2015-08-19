#!/usr/bin/env bash

if [ "$1" == "deployRun" ] || [ $# -eq 0 ]; then
    # package for deployment
    mvn -Dstorm.mainclass=storm.run -P deploy package

    # package for local start
    mvn -Dstorm.mainclass=storm.deploy -P standalone package
fi

if [ "$1" == "run" ] || [ "$1" == "deployRun" ]; then
    # start the deployment
    java -jar -Dnimbus.host=localhost -Dstorm.jar=target/streams-storm-0.9.22-SNAPSHOT-storm-provided.jar target/streams-storm-0.9.22-SNAPSHOT-storm-compiled.jar src/main/resources/example.xml
fi
