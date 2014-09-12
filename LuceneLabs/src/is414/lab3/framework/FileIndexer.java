package is414.lab3.framework;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.util.Version;
import org.apache.lucene.index.*;

import java.io.File;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.Properties;
import java.util.Date;

/**
 * A File Indexer capable of recursively indexing a directory tree.
 */
public class FileIndexer
{
  protected FileHandler fileHandler;

  public FileIndexer(Properties props) throws IOException {
    fileHandler = new ExtensionFileHandler(props);
  }

  public void index(IndexWriter writer, File file)
    throws FileHandlerException {

    if (file.canRead()) {
      if (file.isDirectory()) {
        String[] files = file.list();
        if (files != null) {
          for (int i = 0; i < files.length; i++) {
            index(writer, new File(file, files[i]));
          }
        }
      }
      else {
        System.out.println("Indexing " + file);
        try {
          Document doc = fileHandler.getDocument(file);
          if (doc != null) {
	       	doc.add(new StringField("path", file.getPath(), Field.Store.YES));			 
            writer.addDocument(doc);
				

          }
          else {
            System.err.println("Cannot handle "
              + file.getAbsolutePath() + "; skipping");
          }
        }
        catch (IOException e) {
          System.err.println("Cannot index "
            + file.getAbsolutePath() + "; skipping ("
            + e.getMessage() + ")");
        }
      }
    }
  }

  public static void main(String[] args) throws Exception {
    if (args.length < 3) {
      usage();
      System.exit(0);
    }

    Properties props = new Properties();
    props.load(new FileInputStream(args[0]));
	File INDEX_DIR = new File(args[2]);
	
    // Create a new Index Writer Config object 
    IndexWriterConfig writerConfig = new IndexWriterConfig(Version.LUCENE_48,new SimpleAnalyzer(Version.LUCENE_48));
    // Create a new Directory object which allows access to the lucene index (read/write)      
    Directory dir = FSDirectory.open(INDEX_DIR);
	IndexReader reader = DirectoryReader.open(dir);
    // Create a new Index Writer instance
    IndexWriter writer = new IndexWriter(dir, writerConfig);	
	System.out.println("Indexing to directory '" +INDEX_DIR+ "'...");
	
    FileIndexer indexer = new FileIndexer(props);
	
    long start = new Date().getTime();
	
    indexer.index(writer, new File(args[1]));
    writer.close();
    long end = new Date().getTime();

    System.out.println();
    
    System.out.println("Documents indexed: " + reader.numDocs());
    System.out.println("Total time: " + (end - start) + " ms");
    reader.close();
  }

  private static void usage() {
    System.err.println("USAGE: java "
      + FileIndexer.class.getName()
      + " /path/to/properties /path/to/file/or/directory"
      + " /path/to/index");
  }
}
