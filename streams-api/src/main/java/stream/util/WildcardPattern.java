/*
 *  streams library
 *
 *  Copyright (C) 2011-2014 by Christian Bockermann, Hendrik Blom
 *
 *  streams is a library, API and runtime environment for processing high
 *  volume data streams. It is composed of three submodules "stream-api",
 *  "stream-core" and "stream-runtime".
 *
 *  The streams library (and its submodules) is free software: you can
 *  redistribute it and/or modify it under the terms of the
 *  GNU Affero General Public License as published by the Free Software
 *  Foundation, either version 3 of the License, or (at your option) any
 *  later version.
 *
 *  The stream.ai library (and its submodules) is distributed in the hope
 *  that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 *  warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see http://www.gnu.org/licenses/.
 */
package stream.util;

import java.util.ArrayList;
import java.util.Stack;

public class WildcardPattern {

	String pattern = "*";
	Boolean caseInsensitive = true;

	public WildcardPattern(String pattern) {
		this.pattern = pattern;
	}

	public WildcardPattern(String pattern, boolean caseInsensitive) {
		this.pattern = pattern;
		this.caseInsensitive = caseInsensitive;
	}

	public boolean matches(String input) {
		return matches(pattern, input, caseInsensitive);
	}

	public static boolean matches(String pattern, String input) {
		return matches(pattern, input, true);
	}

	public static boolean matches(String pattern, String input, boolean caseInsensitive) {

		String filename = input;
		String wildcardMatcher = pattern;
		if (caseInsensitive) {
			filename = input.toLowerCase();
			wildcardMatcher = pattern.toLowerCase();
		}
		if (filename == null && wildcardMatcher == null) {
			return true;
		}
		if (filename == null || wildcardMatcher == null) {
			return false;
		}

		String[] wcs = splitOnTokens(wildcardMatcher);
		boolean anyChars = false;
		int textIdx = 0;
		int wcsIdx = 0;
		Stack<int[]> backtrack = new Stack<int[]>();

		// loop around a backtrack stack, to handle complex * matching
		do {
			if (backtrack.size() > 0) {
				int[] array = backtrack.pop();
				wcsIdx = array[0];
				textIdx = array[1];
				anyChars = true;
			}

			// loop whilst tokens and text left to process
			while (wcsIdx < wcs.length) {

				if (wcs[wcsIdx].equals("?")) {
					// ? so move to next text char
					textIdx++;
					anyChars = false;

				} else if (wcs[wcsIdx].equals("*")) {
					// set any chars status
					anyChars = true;
					if (wcsIdx == wcs.length - 1) {
						textIdx = filename.length();
					}

				} else {
					// matching text token
					if (anyChars) {
						// any chars then try to locate text token
						textIdx = filename.indexOf(wcs[wcsIdx], textIdx);
						if (textIdx == -1) {
							// token not found
							break;
						}
						int repeat = filename.indexOf(wcs[wcsIdx], textIdx + 1);
						if (repeat >= 0) {
							backtrack.push(new int[] { wcsIdx, repeat });
						}
					} else {
						// matching from current position
						if (!filename.regionMatches(textIdx, wcs[wcsIdx], 0,
								wcs[wcsIdx].length())) {
							// if (!caseSensitivity.checkRegionMatches(filename,
							// textIdx, wcs[wcsIdx])) {
							// couldnt match token
							break;
						}
					}

					// matched text token, move text index to end of matched
					// token
					textIdx += wcs[wcsIdx].length();
					anyChars = false;
				}

				wcsIdx++;
			}

			// full match
			if (wcsIdx == wcs.length && textIdx == filename.length()) {
				return true;
			}

		} while (backtrack.size() > 0);

		return false;
	}

	/**
	 * Splits a string into a number of tokens.
	 *
	 * @param text
	 *            the text to split
	 * @return the tokens, never null
	 */
	static String[] splitOnTokens(String text) {
		// used by wildcardMatch
		// package level so a unit test may run on this

		if (text.indexOf('?') == -1 && text.indexOf('*') == -1) {
			return new String[] { text };
		}

		char[] array = text.toCharArray();
		ArrayList<String> list = new ArrayList<String>();
		StringBuilder buffer = new StringBuilder();
		for (int i = 0; i < array.length; i++) {
			if (array[i] == '?' || array[i] == '*') {
				if (buffer.length() != 0) {
					list.add(buffer.toString());
					buffer.setLength(0);
				}
				if (array[i] == '?') {
					list.add("?");
				} else if (list.size() == 0
						|| (i > 0 && list.get(list.size() - 1).equals("*") == false)) {
					list.add("*");
				}
			} else {
				buffer.append(array[i]);
			}
		}
		if (buffer.length() != 0) {
			list.add(buffer.toString());
		}

		return list.toArray(new String[list.size()]);
	}

}
