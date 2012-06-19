#!/bin/sh

mvn -Dexec.args="server.xml" -Dexec.mainClass="stream.run" exec:java
