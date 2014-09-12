package is414.lab3.rtf;

import is414.lab3.framework.DocumentHandler;
import is414.lab3.framework.DocumentHandlerException;

import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.rtf.RTFEditorKit;
import javax.swing.text.BadLocationException;


public class JavaBuiltInRTFIndexer implements DocumentHandler {

  static final File INDEX_DIR = new File("indexRTF");
  
  public Document getDocument(InputStream is)
    throws DocumentHandlerException {

    String bodyText = null;

    DefaultStyledDocument styledDoc = new DefaultStyledDocument();
    try {
      new RTFEditorKit().read(is, styledDoc, 0);
      bodyText = styledDoc.getText(0, styledDoc.getLength());
    }
    catch (IOException e) {
      throw new DocumentHandlerException(
        "Cannot extract text from a RTF document", e);
    }
    catch (BadLocationException e) {
      throw new DocumentHandlerException(
        "Cannot extract text from a RTF document", e);
    }

    if (bodyText != null) {
      Document doc = new Document();
		doc.add(new TextField("body", bodyText,Field.Store.NO));
      return doc;
    }
    return null;
  }

  public static void main(String[] args) throws Exception {
    JavaBuiltInRTFIndexer handler = new JavaBuiltInRTFIndexer();
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
