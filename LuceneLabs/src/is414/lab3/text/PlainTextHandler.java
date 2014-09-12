package is414.lab3.text;

import is414.lab3.framework.DocumentHandler;
import is414.lab3.framework.DocumentHandlerException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.StringField;

import java.io.File;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;

public class PlainTextHandler implements DocumentHandler {

  public Document getDocument(InputStream is)
    throws DocumentHandlerException {

    String bodyText = "";

    try {
      BufferedReader br =
        new BufferedReader(new InputStreamReader(is));
      String line = null;
      while ((line = br.readLine()) != null) {
        bodyText += line;
      }
      br.close();
    }
    catch(IOException e) {
      throw new DocumentHandlerException(
        "Cannot read the text document", e);
    }

    if (!bodyText.equals("")) {
      Document doc = new Document();
		doc.add(new TextField("body", bodyText,Field.Store.NO));

      return doc;
    }

    return null;
  }

  public static void main(String[] args) throws Exception {
    PlainTextHandler handler = new PlainTextHandler();
    Document doc = handler.getDocument(
      new FileInputStream(new File(args[0])));
   	File f = new File(args[0]);
		doc.add(new StringField("path", f.getPath(), Field.Store.YES));		
    System.out.println(doc);
  }
}
