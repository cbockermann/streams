
# Using the <a target="_blank" href="https://sfb876.de/streams/apidocs/index.html?stream/Keys.html">stream.Keys</a> Class

The basic data structure used within the *streams* framework is a hashmap. Each
message in *streams* is represented by a hashmap that maps keys to values. We
refer to these keys as *attributes*, *features* or simply *keys*. The following
table shows an example of this message representation:

<div style="margin: auto; text-align: center; width: 500px;" class="figure">
	<table style="padding: 5px;">
		<tr>
			<th style="font-family: Courier; font-weight: bold; color: #666;">Key</th>
			<th style="font-family: Courier; font-weight: bold; color: #666;">Value</th>
		</tr>
		<tr>
			<td style="padding: 5px;">feature:color:read</td>
			<td style="padding: 5px; text-align: center;">231</td>
		</tr>
		<tr>
			<td style="padding: 5px;">feature:color:green</td>
			<td style="padding: 5px; text-align: center;">192</td>
		</tr>
		<tr>
			<td style="padding: 5px;">feature:color:blue</td>
			<td style="padding: 5px; text-align: center;">28</td>
		</tr>
		<tr>
			<td style="padding: 5px;">feature:width</td>
			<td style="padding: 5px; text-align: center;">4.23195</td>
		</tr>
		<tr>
			<td style="padding: 5px;">feature:height</td>
			<td style="padding: 5px; text-align: center;">5.29473</td>
		</tr>
		<tr>
			<td style="padding: 5px;">feature:pixel:0</td>
			<td style="padding: 5px; text-align: center;">129</td>
		</tr>
		<tr>
			<td style="padding: 5px;">feature:pixel:1</td>
			<td style="padding: 5px; text-align: center;">34</td>
		</tr>
		<tr>
			<td style="padding: 5px;">feature:pixel:2</td>
			<td style="padding: 5px; text-align: center;">64</td>
		</tr>
		<tr>
			<td style="padding: 5px;">feature:pixel:3</td>
			<td style="padding: 5px; text-align: center;">209</td>
		</tr>
		<tr>
			<td style="padding: 5px;">feature:pixel:4</td>
			<td style="padding: 5px; text-align: center;">172</td>
		</tr>
	</table>
</div>

Sometimes it is useful, to restrict the action of a processor function to a subset
of the features, contained in a message or item. This is exactly, what the `stream.Keys`
class is suited for.

It allows for selecting a subset of keys from a set of strings using simple wildcard patterns.
Only the wildcards `*` and `?` are used. In addition, the exclamation mark `!` can be used to
explicit *de-select* a previously selected key.

In the example of the item shown above, we can use the pattern 
<div style="text-align: center;">
	<code>feature:*,!feature:width</code>
</div>
to select all keys, which start with the string `feature:` and de-select the `feature:width`
attribute. 

The parts of the match are applied in the order as they appear in the pattern list. Thus, if
we reverse the list to `!feature:width,feature:*`, then the `feature:width` key will be include
in the final list as it is de-selected at first, but afterwards selected by the `feature:*` part.


## Using Regular Expressions

With the release of *streams* version 0.9.25, the use of regular expressions is supported by
the `stream.Keys` class. Regular expressions can be added to the pattern list and are marked
with slashes, like: `/pattern/`. They can be combined with the simple wildcard patterns, but
not within the same pattern element.

The following example selects all patterns with the `feature:color` prefix and uses a regular
expression to select the pixel-features:

<div style="margin: auto; text-align: center;">
	<code>feature:color*,/pixel:\d+/</code>
</div>

Like the simple wildcard patterns, the regular expressions can be prefixed with an exclamation
mark to de-select elements from a set.


The regular expressions match, if the pattern is somehow found at any part of the attribute
name. If matching of the full attribute name is required, the regex anchors <code>^</code>
and <code>$</code> need to be specified. As an example, the pattern `/\d+/` will match all 
attributes, which include at least one digit. To limit this to attributes which only consist
of digits in their names, the pattern `/^\d+$/` needs to be used.


## Using <code>Keys</code> in Java Processors

Using the `Keys` functionality in a custom processor is rather straight forward. The `Keys`
class is instantiated from a String and therefore can be used as any other parameter of a
processor.

The following example shows a simple processor that outputs all the keys selected by a wildcard
pattern using the `stream.Keys` class. By default, it uses the `*` pattern to match all keys.

<div style="text-align: center; margin: auto; width: 600px;">
<pre style="text-align: left; width: 600px;"><code class="java" style="font-size: 14px;">public class SelectivePrinter implements Processor {

&nbsp;&nbsp;@Parameter
&nbsp;&nbsp;Keys keys = new Keys("*");

&nbsp;&nbsp;public Data process( Date item ) {

&nbsp;&nbsp;&nbsp;&nbsp;for( String key : keys.select(item) ){
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;System.out.println( key + " => " + item.get(key) );
&nbsp;&nbsp;&nbsp;&nbsp;}

&nbsp;&nbsp;&nbsp;&nbsp;return item;
&nbsp;&nbsp;}
}
</code></pre>
</div>