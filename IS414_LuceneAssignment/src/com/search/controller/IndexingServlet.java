package com.search.controller;

import com.search.util.framework.FileIndexer;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IndexingServlet extends HttpServlet {
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		
		String docLoc = request.getParameter("docLoc");
		String indexLoc = request.getParameter("indexLoc");
		docLoc = docLoc.replaceAll("\\\\","/");
		indexLoc = indexLoc.replaceAll("\\\\","/");
		
		try {
			FileIndexer.execute(docLoc, indexLoc);
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.sendRedirect("search.jsp");
	}
	
	
}
