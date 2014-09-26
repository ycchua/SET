package com.search.util.framework;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Properties;

import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

/**
 * A FileHandler implementation that delegates responsibility to
 * appropriate DocumentHandler implementation, based on a file
 * extension.
 */
public class ExtensionFileHandler implements FileHandler {
  private Properties handlerProps;

  public ExtensionFileHandler(Properties props) {
    handlerProps = props;
  }

  public Document getDocument(File file)
    throws FileHandlerException {

    String name = file.getName();
    int dotIndex = name.indexOf(".");
    if ((dotIndex > 0) && (dotIndex < name.length())) {
      String ext = name.substring(dotIndex + 1, name.length());
      String handlerClassName = handlerProps.getProperty(ext);

      if (handlerClassName == null)
        return null;

      try {
        Class handlerClass = Class.forName(handlerClassName);
        DocumentHandler handler =
          (DocumentHandler) handlerClass.newInstance();
        Document document = handler.getDocument(new FileInputStream(file));
        //Add file format of the file a field named "filename"  
        document.add(new Field("fileName", ext,Field.Store.YES, Field.Index.NOT_ANALYZED));
        // Add the last modified date of the file a field named "lastModifiedDate".  Use 
        // a field that is indexed (i.e. searchable), but don't tokenize the field
        // into words.

        document.add(new Field("lastModifiedDate",
                DateTools.timeToString(file.lastModified(), DateTools.Resolution.MINUTE),
                Field.Store.YES, Field.Index.NOT_ANALYZED));
        // Add the path of the file as a field named "path".  Use a field that is 
        // indexed (i.e. searchable), but don't tokenize the field into words.
        document.add(new Field("path", file.getPath(), Field.Store.YES, Field.Index.NOT_ANALYZED));
        // Add the content for the document content
        
        return document;
      }
      catch (ClassNotFoundException e) {
        throw new FileHandlerException(
          "Cannot create instance of : "
          + handlerClassName, e);
      }
      catch (InstantiationException e) {
        throw new FileHandlerException(
          "Cannot create instance of : "
          + handlerClassName, e);
      }
      catch (IllegalAccessException e) {
        throw new FileHandlerException(
          "Cannot create instance of : "
          + handlerClassName, e);
      }
      catch (FileNotFoundException e) {
        throw new FileHandlerException(
          "File not found: "
          + file.getAbsolutePath(), e);
      }
      catch (DocumentHandlerException e) {
        throw new FileHandlerException(
          "Document cannot be handler: "
          + file.getAbsolutePath(), e);
      }
    }
    return null;
  }

  public static void main(String[] args) throws Exception {
    if (args.length < 2) {
      usage();
      System.exit(0);
    }

    Properties props = new Properties();
    props.load(new FileInputStream(args[0]));

    ExtensionFileHandler fileHandler =
      new ExtensionFileHandler(props);
    Document doc = fileHandler.getDocument(new File(args[1]));
    System.out.println(doc);
  }

  private static void usage() {
    System.err.println("USAGE: java "
      + ExtensionFileHandler.class.getName()
      + " /path/to/properties /path/to/document");
  }
}
