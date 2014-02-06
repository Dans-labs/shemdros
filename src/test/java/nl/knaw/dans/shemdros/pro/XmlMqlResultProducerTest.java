package nl.knaw.dans.shemdros.pro;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import nl.knaw.dans.shemdros.core.EmdrosClient;

import org.junit.Test;

public class XmlMqlResultProducerTest {
	
	@Test
	public void testProduction() throws Exception {
		File query = new File("/data/emdros/wivu/queries/bh_lq99.mql");
		OutputStream out = new FileOutputStream("target/mql-result.xml");
		XmlMqlResultProducer producer = new XmlMqlResultProducer(out);//System.err);
		producer.setIndent(2);
		EmdrosClient.instance().execute(query, producer);
		out.close();
	}
	
	@Test
	public void testName() throws Exception {
		String pre = "/data/emdros/wivu/queries/bh_lq";
		String no;
		String post = ".mql";
		for (int i = 1; i <= 22; i++)
		{
			no = "";
			if (i < 10)
			{
				no = "0";
			}
			no += i;
			try
			{
				String filename = pre + no + post;
				System.out.println(filename);
				query(filename);
			}
			catch (Exception e)
			{
				System.out.println(e.getClass().getName());
			}
		}
	}
	
	private void query(String filename) throws Exception
	{
		File query = new File(filename);
		OutputStream out = new FileOutputStream("target/mql-result.xml");
		XmlMqlResultProducer producer = new XmlMqlResultProducer(out);//System.err);
		producer.setIndent(2);
		EmdrosClient.instance().execute(query, producer);
		out.close();
	}

}
