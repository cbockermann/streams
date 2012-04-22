

update-license:
	find stream-api -name '*.java' -exec perl src/main/scripts/preprend.pl {} LICENSE.preamble.txt
