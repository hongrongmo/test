<style>
	p.indent{padding-left:10px;}
	p.searchtipexample{margin: 0 0 15px 15px;}
.ui-dialog { position: absolute; width: 300px; overflow: hidden; z-index:99999;}
.ui-dialog .ui-dialog-titlebar-close { display:inline;}
.ui-state-default, .ui-widget-content .ui-state-default, .ui-widget-header .ui-state-default{border:none;}
.ui-dialog .ui-dialog-titlebar-close span{
position: absolute;
top: 50%;
margin-top: -8px;
left:50%;
margin-left:-8px;
}
.ui-dialog .ui-resizable-se {
width: 14px;
height: 14px;
right: -1px;
bottom: -1px;
}
.ui-state-default .ui-icon { background-image: url(/static/css/custom-theme/images/ui-icons_888888_256x240.png); }	
</style>
<%
    String topic = (String)request.getParameter("topic");
    if (topic == null || topic.length() == 0) {
        topic = "quick";
    }
%>
	<%-- ******************************************************* --%>
	<%-- ******************************************************* --%>
	<%-- QUICK SEARCH TIPS                                       --%>
	<%-- ******************************************************* --%>
	<%-- ******************************************************* --%>
	<% if ("quick".equals(topic)) { %>
	<p class="searchtipdesc">Use truncation (*) to search for words
		that begin with the same letters.</p>
	<p class="searchtipexample">comput* returns computer, computers,
		computerize, computerization</p>

	<p class="searchtipdesc">Truncation can also be used to replace any
		number of characters internally.</p>
	<p class="searchtipexample">sul*ate returns sulphate or sulfate</p>

	<p class="searchtipdesc">Use wildcard (?) to replace a single
		character.</p>
	<p class="searchtipexample">wom?n retrieves woman or women</p>

	<p class="searchtipdesc">Terms are automatically stemmed, except in
		the author field, unless the "Autostemming off" feature is checked.</p>
	<p class="searchtipexample">management returns manage, managed,
		manager, managers, managing, management</p>

	<p class="searchtipdesc">To search for an exact phrase or phrases
		containing stop words (and, or, not, near), enclose terms in braces or
		quotation marks.</p>
	<p class="searchtipexample">
		{Journal of Microwave Power and Electromagnetic Energy} <br /> "near
		field scanning"
	</p>

	<p class="searchtipdesc">Use NEAR or ONEAR to search for terms in
		proximity. ONEAR specifies the exact order of terms. NEAR and ONEAR
		cannot be used with truncation, wildcards, parenthesis, braces or
		quotation marks. NEAR and ONEAR can be used with stemming.</p>
	<p class="searchtipexample">
		Avalanche ONEAR/0 diodes<br /> Solar NEAR energy
	</p>

	<p class="searchtipdesc">Browse the author look-up index to select
		all variations of an author's name</p>
	<p class="searchtipexample">Smith, A. OR Smith, A.J. OR Smith, Alan
		J.</p>
	<%-- ******************************************************* --%>
	<%-- ******************************************************* --%>
	<%-- EXPERT SEARCH TIPS                                      --%>
	<%-- ******************************************************* --%>
	<%-- ******************************************************* --%>
	<% } else if ("expert".equals(topic)) { %>
	<p class="searchtipdesc">Search within a specific field using "wn"
	</p>
	<p class="searchtipexample">{test bed} wn ALL AND {atm networks} wn
		TI (window wn TI AND sapphire wn TI) OR Sakamoto, K* wn AU</p>
	<p class="searchtipdesc">Use truncation (*) to search for words
		that begin with the same letters.</p>
	<p class="searchtipexample">comput* returns computer, computers,
		computerize, computerization</p>
	<p class="searchtipdesc">Truncation can also be used to replace any
		number of characters internally.</p>
	<p class="searchtipexample">sul*ate returns sulphate or sulfate</p>
	<p class="searchtipdesc">Use wildcard (?) to replace a single
		character.</p>
	<p class="searchtipexample">wom?n retrieves woman or women</p>
	<p class="searchtipdesc">Stem search terms using $</p>
	<p class="searchtipexample">$management returns manage, managed,
		manager, managers, managing, management</p>
	<p class="searchtipdesc">To search for an exact phrase or phrases
		containing stop words (and, or, not, near), enclose terms in braces or
		quotation marks.</p>
	<p class="searchtipexample">{Journal of Microwave Power and
		Electromagnetic Energy} wn ST "near field scanning" wn CV</p>
	<p class="searchtipdesc">Use NEAR or ONEAR to search for terms in
		proximity. ONEAR specifies the exact order of terms. NEAR and ONEAR
		cannot be used with truncation, wildcards, parenthesis, braces or
		quotation marks.</p>
	<p class="searchtipexample">Avalanche ONEAR/0 diodes Solar NEAR
		energy Wind NEARr/3 power $industrial NEAR $management</p>
	<p class="searchtipdesc">Browse the author look-up index to select
		all variations of an author's name.</p>
	<p class="searchtipexample">Smith, A. OR Smith, A.J. OR Smith, Alan
		OR Smith, Alan J.</p>
	<% } else if ("ebook".equals(topic)) { %>
	<p class="searchtipdesc">
		Keyword is the default setting that searches the text of the eBook and
		retrieves sections of the eBook containing the keyword. You can review
		page snippets and book details that contain the keyword, plus you can
		read the entire page, chapter, or book, access permitting. <br /> <br />
	</p>

	<p class="searchtipdesc">
		Searching by author, ISBN, publisher, subject, or title retrieves a
		list of eBooks. You can review the details of each book. <br /> <br />
	</p>

	<p class="searchtipdesc">
		When you would just like to browse the eBooks, use the bottom half of
		the page to select the collection on the left and the subject on the
		right. Clicking a subject results in an "instant" search, and the
		Search results page displays a list of the eBooks in that subject. <br />
		<br />
	</p>
	<%-- ******************************************************* --%>
	<%-- ******************************************************* --%>
	<%-- THESAURUS SEARCH TIPS                                   --%>
	<%-- ******************************************************* --%>
	<%-- ******************************************************* --%>
	<% } else if ("thes".equals(topic)) { %>
	<p>The Thesaurus function helps you by:</p>
	<ul>
		<li>Identifying controlled vocabulary terms</li>
		<li>Finding synonyms and related terms</li>
		<li>Improving your search strategy with suggested and narrower
			terms</li>
	</ul>
	<p>Controlled vocabulary terms are used to index articles. Since
		the thesauri have evolved over time, you can use this function to
		trace the usage of controlled terms.</p>
	<br />

	<p>
		<b>Note:</b> Terms you choose from the thesaurus appear in the Search
		Box so you can include them in a query. If you switch from one
		database to another, the selections in the Search Box will be deleted.
		Some terms are common to multiple databases, but many controlled terms
		are unique to a database.
	</p>
	<br />

	<p>
		<b>Step 1: Select the type of search</b>
	</p>
	<br />
	<p class="indent">
		Click <b>Search</b> to display controlled vocabulary terms containing
		the term you are searching for, as well as broader, narrower, and
		related terms. For example, searching for <i>stress</i> will retrieve
		<i>Anelastic relaxation</i>, <i>Annealing</i>, <i>Axial
			compression</i>, <i>Bearing capacity</i>, and more.
	</p>
	<br />

	<p class="indent">
		Click <b>Exact Term</b> if you already know a controlled vocabulary
		term and want to go directly to its thesaurus entry, which contains
		broader, narrower, and related terms as well as scope notes, prior
		terms, and lead-in terms.
	</p>
	<br />

	<p class="indent">
		Click <b>Browse</b> to scan the thesaurus alphabetically.
	</p>
	<br />

	<p class="indent">All terms are linked to their thesaurus entry.</p>
	<br />

	<p class="indent">Clicking a check box moves a term to the Search
		Box, where it can become part of your database search using the
		Boolean operators AND or OR, along with Engineering Village Quick
		Search limits. Lead-in terms that have never been used as controlled
		vocabulary terms cannot be selected.</p>
	<br />

	<p>
		<b>Step 2: Add terms to your search</b>
	</p>
	<br />
	<p class="indent">To add terms from your results to your search
		query, click the checkbox of one or more terms.</p>
	<br />

	<p class="indent">To view the thesaurus entry of a term (which will
		yield additional related terms), click on the term.</p>
	<br />

	<p class="indent">
		<b>Note:</b> Terms in italics are lead-in terms that point to
		controlled vocabulary.
	</p>
	<br />

	<% } %>
