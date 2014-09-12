package is414.lab3.html;

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

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.w3c.tidy.Tidy;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;


public class JTidyHTMLIndexer implements DocumentHandler {

  static final File INDEX_DIR = new File("indexes/indexHTML");
  
  public org.apache.lucene.document.Document
    getDocument(InputStream is) throws DocumentHandlerException {

    Tidy tidy = new Tidy();
    tidy.setQuiet(true);
    tidy.setShowWarnings(false);
    org.w3c.dom.Document root = tidy.parseDOM(is, null);
    Element rawDoc = root.getDocumentElement();

    org.apache.lucene.document.Document doc =
      new org.apache.lucene.document.Document();

    String title = getTitle(rawDoc);
    String body = getBody(rawDoc);
	 
    if ((title != null) && (!title.equals(""))) {
	 
	 	doc.add(new TextField("title", title,Field.Store.NO));
    }
    if ((body != null) && (!body.equals(""))) {
		doc.add(new TextField("body", body,Field.Store.NO));

    }

    return doc;
  }

  /**
   * Gets the title text of the HTML document.
   *
   * @rawDoc the DOM Element to extract title Node from
   * @return the title text
   */
  protected String getTitle(Element rawDoc) {
    if (rawDoc == null) {
      return null;
    }

    String title = "";

    NodeList children = rawDoc.getElementsByTagName("title");
    if (children.getLength() > 0) {
      Element titleElement = ((Element) children.item(0));
      Text text = (Text) titleElement.getFirstChild();
      if (text != null) {
        title = text.getData();
      }
    }
    return title;
  }

  /**
   * Gets the body text of the HTML document.
   *
   * @rawDoc the DOM Element to extract body Node from
   * @return the body text
   */
  protected String getBody(Element rawDoc) {
    if (rawDoc == null) {
      return null;
    }

    String body = "";
    NodeList children = rawDoc.getElementsByTagName("body");
    if (children.getLength() > 0) {
      body = getText(children.item(0));
    }
    return body;
  }


  
  /**
   * Extracts text from the DOM node.
   *
   * @param node a DOM node
   * @return the text value of the node
   */
  protected String getText(Node node) {
    NodeList children = node.getChildNodes();
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < children.getLength(); i++) {
      Node child = children.item(i);
      switch (child.getNodeType()) {
        case Node.ELEMENT_NODE:
          sb.append(getText(child));
          sb.append(" ");
          break;
        case Node.TEXT_NODE:
          sb.append(((Text) child).getData());
          break;
      }
    }
    return sb.toString();
  }

  public static void main(String args[]) throws Exception {
    JTidyHTMLIndexer handler = new JTidyHTMLIndexer();
	 
	 File f = new File(args[0]);
	 
	 org.apache.lucene.document.Document doc = handler.getDocument(
      new FileInputStream(f));

	// Adding the HTML file to indexHTML
	 
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
