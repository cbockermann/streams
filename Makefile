

update-license:
	find stream-api -name '*.java' -exec perl src/main/scripts/prepend.pl {} LICENSE.preamble.txt \;
	find stream-core -name '*.java' -exec perl src/main/scripts/prepend.pl {} LICENSE.preamble.txt \;
	find stream-runtime -name '*.java' -exec perl src/main/scripts/prepend.pl {} LICENSE.preamble.txt \;

plugin:
	@cd stream-plugin && make plugin
