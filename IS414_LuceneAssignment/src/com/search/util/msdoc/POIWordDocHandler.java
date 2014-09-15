package com.search.util.msdoc;

import com.search.util.framework.DocumentHandler;
import com.search.util.framework.DocumentHandlerException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

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
		doc.add(new Field("body", bodyText,Field.Store.YES, Field.Index.ANALYZED));
      return doc;
    }
    return null;
  }

  public static void main(String[] args) throws Exception {
//    POIWordDocHandler handler = new POIWordDocHandler();
//    Document doc = handler.getDocument(
//      new FileInputStream(new File(args[0])));
//   	File f = new File(args[0]);
//		doc.add(new Field("path", f.getPath(), Field.Store.YES, Field.Index.NOT_ANALYZED));  
//  		 System.out.println(doc);
  

  
  }
}
