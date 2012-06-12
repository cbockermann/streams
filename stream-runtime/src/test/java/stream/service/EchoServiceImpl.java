package stream.service;

public class EchoServiceImpl implements EchoService {

	@Override
	public void reset() throws Exception {
	}

	@Override
	public String echo(String text) {

		if( text == null )
			return "...";
		
		return text.toUpperCase() + " " + text + " ... " + text.toLowerCase();
	}
}
