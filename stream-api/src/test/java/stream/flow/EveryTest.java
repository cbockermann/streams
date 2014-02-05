package stream.flow;

import junit.framework.Assert;

import org.junit.Test;

import stream.Data;
import stream.ProcessContext;
import stream.data.DataFactory;
import stream.data.ProcessContextMock;
import stream.data.SetValue;

public class EveryTest {

	@Test
	public void test() throws Exception {
		Every e = new Every();
		ProcessContext c = new ProcessContextMock();
		e.setN(100l);
		
		
		SetValue sv = new SetValue();
		sv.setScope(new String[] {"data"});
		sv.setKey("test");
		sv.setValue("test");
		e.add(sv);
		e.init(c);
		
		int count = 0;
		
		for(int i=0; i<1000;i++){
			Data d = DataFactory.create();
			d = e.process(d);
			if(d.containsKey("test"))
				count++;
		}
		Assert.assertEquals(10, count);
		
		
	}
	

}
