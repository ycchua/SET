package lucene;

import is414.lab3.html.JTidyHTMLIndexer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IndexServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/plain");
		
		JTidyHTMLIndexer htmlI = new JTidyHTMLIndexer();
		try {
			ServletContext sc = req.getServletContext();
			URI htmlURI = sc.getResource("/source/Sample2/HTML.html").toURI();
			File htmlFile = new File(htmlURI);
			//FileInputStream fs = new FileInputStream(sc.getResource("/source/Sample2/HTML.html").openStream());
			
			System.out.println(htmlURI);
			//htmlI.main(new String[]{htmlURI.toString()});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		PrintWriter writer = resp.getWriter();
		System.out.println();
		writer.println("Index servlet");
	}
}
