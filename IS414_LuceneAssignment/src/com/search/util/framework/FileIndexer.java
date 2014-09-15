package com.search.util.framework;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.SimpleAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.util.Version;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileInputStream;
import java.net.URL;
import java.util.Properties;
import java.util.Date;

/**
 * A File Indexer capable of recursively indexing a directory tree.
 */
public class FileIndexer {
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
			} else {
				System.out.println("Indexing " + file);
				try {
					Document doc = fileHandler.getDocument(file);
					if (doc != null) {
						doc.add(new Field("path", file.getPath(),
								Field.Store.YES, Field.Index.NOT_ANALYZED));
						writer.addDocument(doc);

					} else {
						System.err.println("Cannot handle "
								+ file.getAbsolutePath() + "; skipping");
					}
				} catch (IOException e) {
					System.err.println("Cannot index " + file.getAbsolutePath()
							+ "; skipping (" + e.getMessage() + ")");
				}
			}
		}
	}

	public static void execute(String docLoc, String indexLoc) throws Exception {

		URL resource = FileIndexer.class.getResource("handler.properties");
		File file = new File(resource.toURI());
		FileInputStream input = new FileInputStream(file);

		Properties props = new Properties();
		props.load(input);

		File INDEX_DIR = new File(indexLoc);

		// Create a new Index Writer Config object
		IndexWriterConfig writerConfig = new IndexWriterConfig(
				Version.LUCENE_35, new SimpleAnalyzer(Version.LUCENE_35));
		// Create a new Directory object which allows access to the lucene index
		// (read/write)
		Directory dir = FSDirectory.open(INDEX_DIR);
		// Create a new Index Writer instance
		IndexWriter writer = new IndexWriter(dir, writerConfig);
		System.out.println("Indexing to directory '" + INDEX_DIR + "'...");

		FileIndexer indexer = new FileIndexer(props);

		long start = new Date().getTime();

		indexer.index(writer, new File(docLoc));
		writer.close();
		long end = new Date().getTime();

		System.out.println();
		IndexReader reader = IndexReader.open(dir);
		System.out.println("Documents indexed: " + reader.numDocs());
		System.out.println("Total time: " + (end - start) + " ms");
		reader.close();
	}

	private static void usage() {
		System.err.println("USAGE: java " + FileIndexer.class.getName()
				+ " /path/to/properties /path/to/file/or/directory"
				+ " /path/to/index");
	}
	

}