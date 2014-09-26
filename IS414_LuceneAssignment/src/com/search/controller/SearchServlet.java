package com.search.controller;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.DateTools;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.MultiReader;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermRangeQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.util.Version;

public class SearchServlet extends HttpServlet {
	public void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		
		// Retrieve of parameters from search.jsp
		String indexLoc = request.getParameter("indexLoc");
		
		String queryString = request.getParameter("query");
		String resultsModified = request.getParameter("date");
		
		//retrieve indexLoc
		indexLoc = indexLoc.replaceAll("\\\\","/");
		
		// Creates a Directory class pointing to the specified index
	    Directory specifiedIndexDirectory = FSDirectory.open(new File(indexLoc));
	    IndexReader[] readers = new IndexReader[1];
	    
	    readers[0] = IndexReader.open(specifiedIndexDirectory);
	    MultiReader reader = new MultiReader(readers);
	    //IndexReader reader = IndexReader.open(specifiedIndexDirectory); 
	    
	    // Create a Index searcher for indexes
	    IndexSearcher searcher = new IndexSearcher(reader);
	    Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_48);
	    
	 // Create a boolean query for the search
	    BooleanQuery queryBoolean = new BooleanQuery();
	    
	      // Search index by modified date for the file
	    if (resultsModified != null && resultsModified != "") {
		    Date date = new Date();
		    String dateString1 = "";
		    String dateString2 = "";
		   
		    if (resultsModified.equals("3")) {
		    	
		    	dateString1 = DateTools.timeToString(date.getTime(), DateTools.Resolution.MINUTE);
		    	
		    	date.setMonth(date.getMonth()-3);
		    	dateString2 = DateTools.timeToString(date.getTime(), DateTools.Resolution.MINUTE);
		    	
		    } else if (resultsModified.equals("6")) {
		    	
		    	dateString1 = DateTools.timeToString(date.getTime(), DateTools.Resolution.MINUTE);
		    	
		    	date.setMonth(date.getMonth()-6);
		    	dateString2 = DateTools.timeToString(date.getTime(), DateTools.Resolution.MINUTE);
		    } else if (resultsModified.equals("12")) {
		    	
		    	dateString1 = DateTools.timeToString(date.getTime(), DateTools.Resolution.MINUTE);
		    	
		    	date.setYear(date.getYear()-1);
		    	dateString2 = DateTools.timeToString(date.getTime(), DateTools.Resolution.MINUTE);
		    }
		    
		    TermRangeQuery Rangequery = new TermRangeQuery("lastModifiedDate" ,new BytesRef(dateString2), new BytesRef(dateString1), true, true);
		    queryBoolean.add(Rangequery, BooleanClause.Occur.MUST);
	    }
	     
	    try {
		    QueryParser parser = new QueryParser(Version.LUCENE_48, "body", analyzer);
		    if (queryBoolean != null && queryBoolean.toString() != "") {
		    	queryString += " " + queryBoolean.toString();	
		    }
		    
		    Query query = null;

		    if (queryString != null && !queryString.trim().equals("")) {
		    	query = parser.parse(queryString);
		    } else {
		    	query = queryBoolean;
		    }
		    
		    System.out.println("Searching for: " + queryString);
		    
		    TopDocs hits = searcher.search(query,reader.numDocs());
	
		    //Store inside list to be displayed
		    List<String> list = new LinkedList<String>();
		    HttpSession session = request.getSession();
		    session.setAttribute("list", list);
		    
		    list.add(hits.totalHits + " total matching documents");
		    
		    // Retrieves an array of ScoreDocs
		    ScoreDoc[] docArray = hits.scoreDocs;
		    
		    for (int i=0; i<docArray.length; i++) {
		    	Document currentDoc = searcher.doc(docArray[i].doc);
		    	String path = currentDoc.get("path");
		    	if (path != null) {
		            list.add((i+1) + ". " + path); //NEED SCORE??+ " score="+docArray[i].score);
		    	}
		    }
		    
		    //searcher.close();
		    response.sendRedirect("results.jsp");
	    } catch (ParseException e) {
	    	e.printStackTrace();
	    }
	}
}
