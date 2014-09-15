<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Index Files</title>
</head>
<body>
	<form action="IndexingServlet" method="post">
		<table width="100%" border="0" cellspacing="1" cellpadding="5">
			<tr>
				<td><b>Location of documents: </b>
				</td>
				<td><input type="text" name="docLoc" size="50"
					value="C:/LuceneAssignment/documents">
				</td>
			</tr>
			<tr>
				<td><b>Location to store index:</b></td>
				<td><input type="text" name="indexLoc" size="50"
					value="C:/LuceneAssignment/index"><br></td>
			</tr>
			<tr>
				<td></td>
				<td><input type="submit" value="Index documents" />
				</td>
			</tr>
		</table>
	</form>
</body>
</html>