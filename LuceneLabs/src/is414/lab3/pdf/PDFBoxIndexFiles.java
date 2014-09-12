package is414.lab3.pdf;

import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Date;

public class PDFBoxIndexFiles {

    public static void main(String[] args) throws Exception {
		PDFBoxPDFHandler handler = new PDFBoxPDFHandler();
		 
		File f = new File(args[0]);
		File INDEX_DIR = new File(args[1]);
		
		org.apache.lucene.document.Document doc = handler.getDocument(new FileInputStream(f));
		  
		// Adding the PDF file to INDEX_DIR		 
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
	
