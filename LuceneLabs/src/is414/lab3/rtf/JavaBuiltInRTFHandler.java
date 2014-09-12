package is414.lab3.rtf;

import is414.lab3.framework.DocumentHandler;
import is414.lab3.framework.DocumentHandlerException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.document.StringField;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.rtf.RTFEditorKit;
import javax.swing.text.BadLocationException;


public class JavaBuiltInRTFHandler implements DocumentHandler {

  
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
    JavaBuiltInRTFHandler handler = new JavaBuiltInRTFHandler();
    Document doc = handler.getDocument(
      new FileInputStream(new File(args[0])));
   	File f = new File(args[0]);
		doc.add(new StringField("path", f.getPath(), Field.Store.YES));
		System.out.println(doc);
  }
}
