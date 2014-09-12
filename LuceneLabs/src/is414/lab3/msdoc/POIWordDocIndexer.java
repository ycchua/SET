package is414.lab3.msdoc;

import is414.lab3.framework.DocumentHandler;
import is414.lab3.framework.DocumentHandlerException;

import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import org.apache.poi.POITextExtractor;
import org.apache.poi.extractor.ExtractorFactory;  

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.PrintWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

public class POIWordDocIndexer implements DocumentHandler {

  static final File INDEX_DIR = new File("indexMSWord");
  
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
   POIWordDocIndexer handler = new POIWordDocIndexer();
    Document doc = handler.getDocument(
      new FileInputStream(new File(args[0])));
  
  	// Adding the HTML file to indexMSWord
	 File f = new File(args[0]);

	 Date start = new Date();	 
	 try {
      // IndexWriter Properties are now set using the IndexWriter config objects
      // All other IndexWriter constructors are deprecated
      
      // Create a new Index Writer Config object 
      IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LUCENE_48,new StandardAnalyzer(Version.LUCENE_48));
      // Create a new Directory object which allows access to the lucene index (read/write)      
      Directory dir = FSDirectory.open(INDEX_DIR);
      // Create a new Index Writer instance
      IndexWriter writer = new IndexWriter(dir, writerConfig);
      System.out.println("Indexing to directory '" +INDEX_DIR+ "'...");
	  
      doc.add(new StringField("path", f.getPath(), Field.Store.YES));
      writer.addDocument(doc);  

      System.out.println("Optimizing...");
      writer.close();
    	System.out.println("Content: "+doc);
      Date end = new Date();
      System.out.println(end.getTime() - start.getTime() + " total milliseconds");

	 } catch (IOException e) {
	      System.out.println(" caught a " + e.getClass() +
	       "\n with message: " + e.getMessage());
	 }
  }
}
