package lucene;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/** Simple command-line based search demo. */
public class SearchMultiIndexes {

  private static AtomicReader[] readers;
  
  /** Use the norms from one field for all fields.  Norms are read into memory,
   * using a byte of memory per document per searched field.  This can cause
   * search of large collections with a large number of fields to run out of
   * memory.  If all of the fields contain only a single token, then the norms
   * are all identical, then single norm vector may be shared. */
  private static class OneNormsReader extends FilterAtomicReader {
    private String field;

    public OneNormsReader(AtomicReader in, String field) {
      super(in);
      this.field = field;
    }

    /*public byte[] norms(String field) throws IOException {
      return in.norms(this.field);
    }*/
  }

  public SearchMultiIndexes() {}

  /** Simple command-line based search demo. */
  public static HashMap<String,Object> main(String[] args) throws Exception {
    HashMap<String,Object> results = new HashMap<String,Object>();
	ArrayList<String> fileNameResults = new ArrayList<String>();
	String usage =
      "Usage: java org.apache.lucene.demo.SearchFiles [-index dir] [-field f] [-repeat n] [-queries file] [-raw] [-norms field]";
    if (args.length > 0 && ("-h".equals(args[0]) || "-help".equals(args[0]))) {
      System.out.println(usage);
      System.exit(0);
    }

    String index = "indexRTF";
	String index2 = "indexHTML";
    String field = "body";
    String queries = null;
    int repeat = 0;
    boolean raw = false;
    String normsField = null;
    
    for (int i = 0; i < args.length; i++) {
      if ("-index".equals(args[i])) {
        index = args[i+1];
        i++;
      } else if ("-field".equals(args[i])) {
        field = args[i+1];
        i++;
      } else if ("-queries".equals(args[i])) {
        queries = args[i+1];
        i++;
      } else if ("-repeat".equals(args[i])) {
        repeat = Integer.parseInt(args[i+1]);
        i++;
      } else if ("-raw".equals(args[i])) {
        raw = true;
      } else if ("-norms".equals(args[i])) {
        normsField = args[i+1];
        i++;
      } else if ("-fileType".equals(args[i])){
    	  normsField = args[i+1];
          i++;
      }
    }
	
	//Open the 2 index directories to search
	Directory specifiedIndexDirectoryIndex = FSDirectory.open(new File("C:\\is414\\build\\index"));
	Directory specifiedIndexDirectoryHTML = FSDirectory.open(new File("C:\\is414\\build\\indexHTML"));
	Directory specifiedIndexDirectoryMSWord = FSDirectory.open(new File("C:\\is414\\build\\indexMSWord"));
	Directory specifiedIndexDirectoryPDF = FSDirectory.open(new File("C:\\is414\\build\\indexPDF"));
	Directory specifiedIndexDirectoryRTF = FSDirectory.open(new File("C:\\is414\\build\\indexRTF"));
	Directory specifiedIndexDirectoryTV = FSDirectory.open(new File("C:\\is414\\build\\indexTV"));

	IndexReader reader1 = DirectoryReader.open(specifiedIndexDirectoryIndex);             
	AtomicReader aReader1 = SlowCompositeReaderWrapper.wrap(reader1);        
	
	IndexReader reader2 = DirectoryReader.open(specifiedIndexDirectoryHTML);             
	AtomicReader aReader2 = SlowCompositeReaderWrapper.wrap(reader2);           
	
	IndexReader reader3 = DirectoryReader.open(specifiedIndexDirectoryMSWord);             
	AtomicReader aReader3 = SlowCompositeReaderWrapper.wrap(reader3);           
	
	IndexReader reader4 = DirectoryReader.open(specifiedIndexDirectoryPDF);             
	AtomicReader aReader4 = SlowCompositeReaderWrapper.wrap(reader4);           
	
	IndexReader reader5 = DirectoryReader.open(specifiedIndexDirectoryRTF);             
	AtomicReader aReader5 = SlowCompositeReaderWrapper.wrap(reader5);           
	
	IndexReader reader6 = DirectoryReader.open(specifiedIndexDirectoryTV);             
	AtomicReader aReader6 = SlowCompositeReaderWrapper.wrap(reader6);        
	
	readers = new AtomicReader[6];
    readers[0] = aReader1;
	readers[1] = aReader2;
	readers[2] = aReader3;
	readers[3] = aReader4;
	readers[4] = aReader5;
	readers[5] = aReader6;

    if (normsField != null){
      readers[0] = new OneNormsReader(readers[0], normsField);
	  readers[1] = new OneNormsReader(readers[1], normsField);
	  readers[2] = new OneNormsReader(readers[2], normsField);
	  readers[3] = new OneNormsReader(readers[3], normsField);
	  readers[4] = new OneNormsReader(readers[4], normsField);
	  readers[5] = new OneNormsReader(readers[5], normsField);
	}
    
	//Declare multi index searches class, MultiSearch
    MultiReader reader = new MultiReader(readers);
	IndexSearcher searcher = new IndexSearcher(reader); 	 
		 
	Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_48);

    /*BufferedReader in = null;
    if (queries != null) {
      in = new BufferedReader(new FileReader(queries));
    } else {
      in = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
    }*/
      QueryParser parser = new QueryParser(Version.LUCENE_48, field, analyzer);

      String line = queries;

      Query query = parser.parse(line);
      System.out.println("Searching for: " + query.toString(field));

	  // Search for top 30 hits with the Query query
      TopDocs hits = searcher.search(query,30);
      
      if (repeat > 0) {                           // repeat & time as benchmark
        Date start = new Date();
        for (int i = 0; i < repeat; i++) {
          hits = searcher.search(query,30);
        }
        Date end = new Date();
        System.out.println("Time: "+(end.getTime()-start.getTime())+"ms");
      }

      System.out.println(hits.totalHits + " total matching documents");
      results.put("hits", hits.totalHits);

	  final int HITS_PER_PAGE = 10;
	  
      // Retrieves an array of ScoreDocs
      ScoreDoc[] docArray = hits.scoreDocs;
      results.put("docs", docArray);
      
      for (int start = 0; start < hits.totalHits; start += HITS_PER_PAGE) {
        int end = Math.min(hits.totalHits, start + HITS_PER_PAGE);
        for (int i = start; i < end; i++) {

          if (raw) {                              // output raw format
            System.out.println("doc="+docArray[i].doc+" score="+docArray[i].score);
            continue;
          }
          
          Document currentDoc = searcher.doc(docArray[i].doc);
          String path = currentDoc.get("path");
          if (path != null) {
            System.out.println((i+1) + ". " + path +" score="+docArray[i].score);
            
            fileNameResults.add(path);
            
            String title = currentDoc.get("title");
            if (title != null) {
              System.out.println("   Title: " + currentDoc.get("title") +" score="+docArray[i].score);
              
            }
          } else {
            System.out.println((i+1) + ". " + "No path for this document");
          }
        }

        if (queries != null)                      // non-interactive
          break;
        
        /*if (hits.totalHits > end) {
          System.out.print("more (y/n) ? ");
          line = in.readLine();
          if (line.length() == 0 || line.charAt(0) == 'n')
            break;
        }*/
      }
    results.put("filenames", fileNameResults);
    reader.close();
	return results;
  }
}
