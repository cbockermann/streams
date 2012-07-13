var globalCount = 0;
var stamp = "";

function init(ctx){
	println( "Init..." );
	stamp = ctx.get( "stamp-value" );
}


function process(data){
	var i = data.get( "@timestamp");
	data.remove( "@timestamp" );
	data.put( "stamp", "test.js processed." );
	data.put( "stamp-2", stamp);
	globalCount++;
	data.put( "count", globalCount);
	return data;
}