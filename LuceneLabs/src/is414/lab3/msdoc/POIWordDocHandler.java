package is414.lab3.msdoc;

import is414.lab3.framework.DocumentHandler;
import is414.lab3.framework.DocumentHandlerException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import org.apache.poi.POITextExtractor;
import org.apache.poi.extractor.ExtractorFactory;  

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;


public class POIWordDocHandler implements DocumentHandler {

  
  public Document getDocument(InputStream is)
    throws DocumentHandlerException {

    String bodyText = null;

    try {
      POITextExtractor extractor = ExtractorFactory.createExtractor(is);
      bodyText = extractor.getText();
    }
    catch (Exception e) {
      throw new DocumentHandlerException(
        "Cannot extract text from a Microsoft document", e);
    }

    if ((bodyText != null) && (bodyText.trim().length() > 0)) {
      Document doc = new Document();
		doc.add(new TextField("body", bodyText,Field.Store.NO));
      return doc;
    }
    return null;
  }

  public static void main(String[] args) throws Exception {
    POIWordDocHandler handler = new POIWordDocHandler();
    Document doc = handler.getDocument(
      new FileInputStream(new File(args[0])));
   	File f = new File(args[0]);
		doc.add(new StringField("path", f.getPath(), Field.Store.YES));  
  		 System.out.println(doc);
  

  
  }
}
