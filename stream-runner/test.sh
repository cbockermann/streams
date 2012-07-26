#!/bin/sh

mvn -Dexec.args="test.xml" -Dexec.mainClass="stream.run" exec:java
