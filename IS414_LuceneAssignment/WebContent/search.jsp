<html>
  <head>
    <meta name="Author" content="Melvrick Goh">
    <title>Lucene Query Constructor Assignment</title>
    <script type="text/javascript" src="luceneQueryConstructor.js"></script>
    <script type="text/javascript" src="../queryValidator/luceneQueryValidator.js"></script>
    <script>
      submitForm = false; // necessary for luceneQueryConstructor not to submit the form upon query construction
      function doSubmitForm(frm, value)
      {
	        if(frm["noField-phrase-input"].value.length > 0)
	          frm["noField-phrase"].value = quote(frm["noField-phrase-input"].value)
	        else if(frm["noField-phrase"].value.length > 0)
	          frm["noField-phrase"].value = '';
	        
	        submitForm = value;
	       doMakeQuery(frm.query);
      }
    </script>
  </head>
  
  <body>
  <form action="SearchServlet" method="post">
  <table width="100%" border="0" cellspacing="1" cellpadding="5"> 
    <tr>
      <th></th>
      <td width="25%"></td>   
      <tr>
		<tr>
				<th><b>Location of index:</b></th>
				<td><input type="text" name="indexLoc" size="50"
					value="C:\LuceneAssignment\index"></td> 
      <tr>
        <th>
          <input name="noField-andModifier" value="+|0" type="hidden"><b>Find results</b>
        </th>
        <td class="bodytext">With <b>all</b> of the words</td>
        <td class="bodytext">
          <input type="text" name="noField-and" size="25">  
        </td>
      </tr>
      <tr>
        <th>
          <input name="noField-phraseModifier" value="+|+" type="hidden">
        </th>
        <td class="bodytext">With the <b>exact phrase</b></td>
        <td class="bodytext">
        <input type="text" name="noField-phrase-input" size="25">
        <input type="hidden" name="noField-phrase">
        </td>
      </tr>
      <tr>
        <th>
          <input name="noField-orModifier" value=" |+" type="hidden">
        </th>
        <td class="bodytext">With <b>at least</b> one of the words</td>
        <td class="bodytext">
          <input type="text" name="noField-or" size="25">
        </td>
      </tr>
      <tr>
        <th>
          <input name="noField-notModifier" value="-|0" type="hidden">
        </th>
        <td class="bodytext"><b>Without</b> the words</td>
        <td class="bodytext">
          <input type="text" name="noField-not" size="25">
        </td>
      </tr>
      <tr>
        <th>
          <b>File Format</b>
        </th>
        <td class="bodytext">
        <select name="fileNameModifier"><option value="And" selected>Only</option><option value="Not">Don't</option></select>
        return results of the file format</td>
        <td class="bodytext">
          <select name="fileName">
          <option value="" selected>any format
				<option value="txt">Plain Text (.txt)
				<option value="rtf">Rich Text Format (.rtf)
				<option value="doc">Microsoft Word (.doc)
				<option value="html">HyperText Markup Language (.html)
				<option value="pdf">Adobe Acrobat PDF (.pdf)
          </select>
        </td>
      </tr>
      <tr>
        <th>
          <b>Date</b>
        </th>
        <td class="bodytext">
        Return results updated in the </td>
        <td class="bodytext">
          <select name="date"><option value="" selected>anytime<option value="3">past 3 months
          <option value="6">past 6 months<option value="12">past year</select>
        </td>
      </tr>
      <tr>      
      
      <!-- SEARCH BUTTON -->
      <input type="hidden" name="query">
      <tr><td>&nbsp;</tr>
      <tr><th><p>Current Query:</th><td><pre id="curQuery" name="curQuery"></pre><pre id="curQueryValid"></pre></td><td>
      
      <input type="button" name="Update" value="Search" onClick="doSubmitForm(this.form, true);" />
      <!--<input type="button" name="Validate" value="Validate" onClick="doCheckLuceneQuery(this.form.query); getElementById('curQueryValid').innerHTML = 'Query is valid'" />      
      --></td>
      
      
            
    </table>
    </form>
  </body>
</html>