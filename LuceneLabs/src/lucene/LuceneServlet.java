package lucene;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.lucene.search.ScoreDoc;

public class LuceneServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
		doGet(req,resp);
	}
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String query = req.getParameter("query");
		String fileNameModifier = req.getParameter("fileNameModifier");
		String fileName = req.getParameter("fileName");
		
		Enumeration<String> arr = req.getParameterNames();
		/*while(arr.hasMoreElements()){
			System.out.println(arr.nextElement());
		}*/
		
		if(query==null){
			query = "content";
		}
		
		resp.setContentType("text/plain");
		PrintWriter writer = resp.getWriter();
		
		SearchMultiIndexes smi = new SearchMultiIndexes();
		try {
			ServletContext context = req.getServletContext();
			
			HashMap<String,Object> results = smi.main(new String[]{"-queries",query});
			int hitsCount = (Integer) results.get("hits");
			ScoreDoc[] docArray = (ScoreDoc[]) results.get("docs");
			ArrayList<String> fileNames = (ArrayList<String>)results.get("filenames");
			writer.println(hitsCount + " total matching documents");
			for (int i = 0; i<docArray.length; i++){
				ScoreDoc doc = docArray[i];
				writer.println((i+1)+". "+"name="+fileNames.get(i)+" doc="+doc.doc+" score="+doc.score);
			}
			//deal with the results returned
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
