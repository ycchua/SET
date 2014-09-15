package com.search.util.text;

import com.search.util.framework.DocumentHandler;
import com.search.util.framework.DocumentHandlerException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

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
		doc.add(new Field("body", bodyText,Field.Store.YES, Field.Index.ANALYZED));

      return doc;
    }

    return null;
  }

  public static void main(String[] args) throws Exception {
    PlainTextHandler handler = new PlainTextHandler();
    Document doc = handler.getDocument(
      new FileInputStream(new File(args[0])));
   	File f = new File(args[0]);
		doc.add(new Field("path", f.getPath(), Field.Store.YES, Field.Index.NOT_ANALYZED));		
    System.out.println(doc);
  }
}

