package is414.lab3.pdf;

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
//import org.apache.lucene.index.FilterIndexReader; No exisitng anymore
import org.apache.lucene.index.*;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

/** Simple command-line based search demo. */
public class SearchFilesPDF {

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

  private SearchFilesPDF() {}

  /** Simple command-line based search demo. */
  public static void main(String[] args) throws Exception {
    String usage =
      "Usage: java org.apache.lucene.demo.SearchFiles [-index dir] [-field f] [-repeat n] [-queries file] [-raw] [-norms field]";
    if (args.length > 0 && ("-h".equals(args[0]) || "-help".equals(args[0]))) {
      System.out.println(usage);
      System.exit(0);
    }

    String index = "indexPDF";
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
      }
    }
    
    // Indexes are now accessed through the Directory class (IndexReader  
    // constructors no longer accept a String(path) as an argument.
    
    // Creates a Directory class pointing to the specified index
    Directory specifiedIndexDirectory = FSDirectory.open(new File(index));
	
    IndexReader reader = DirectoryReader.open(specifiedIndexDirectory);             
	AtomicReader aReader = SlowCompositeReaderWrapper.wrap(reader);             

    if (normsField != null)
      reader = new OneNormsReader(aReader, normsField);

    IndexSearcher searcher = new IndexSearcher(reader);
    Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_48);

    BufferedReader in = null;
    if (queries != null) {
      in = new BufferedReader(new FileReader(queries));
    } else {
      in = new BufferedReader(new InputStreamReader(System.in, "UTF-8"));
    }
      
	QueryParser parser = new QueryParser(Version.LUCENE_48, field, analyzer);
    
	while (true) {
      if (queries == null)                        // prompt the user
        System.out.print("Query: ");

      String line = in.readLine();

      if (line == null || line.length() == -1)
        break;

      Query query = parser.parse(line);
      System.out.println("Searching for: " + query.toString(field));

      // Search results are now encapsulated by the TopDocs object rather than
      // the Hits object which is no longer available from v2.9. 
      
      // Performs a search and returns the top 10 results
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

      final int HITS_PER_PAGE = 10;
      
      // Retrieves an array of ScoreDocs
      ScoreDoc[] docArray = hits.scoreDocs;
      
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
        
        if (hits.totalHits > end) {
          System.out.print("more (y/n) ? ");
          line = in.readLine();
          if (line.length() == 0 || line.charAt(0) == 'n')
            break;
        }
      }
    }
    reader.close();
  }
}
