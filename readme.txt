G2- Lucene Group Assignment

- Once .war file is deployed
- Enter index file location (abs) into the index field
- If you wish to index files, go to index.jsp
- Search at search.jsp

Deployment of War File (Assuming Windows OS)
- I've provided a webapp-runner-7.0.40.1.jar file that simulates a Tomcat instance which you can choose to use for running the program
- Make sure for the .jar file and the .war app file are in the same directory
- Open the command line at the same directory
- Key in java -jar webapp-runner-7.0.40.1.jar IS414_LuceneAssignment.war to run the instance
- You should find the app running at port 8080 now

Notes on Navigating the App
- Home is set to the page dealing with indexing and index files location
- Search at http://localhost:8080/search.jsp
- You will be redirected to results.jsp upon executing the search
