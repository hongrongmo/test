<!doctype html>
<%@ page language="java" session="false" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<style type="text/css">


#databasesbody {
	padding: 5px 0 0 7px;
	overflow: scroll;
	height: auto;
	margin-bottom: 20px;
}

#databasestitle {
	width: 100%;
	position: fixed;
	left: 0;
	top: 0;
	background-color: #148C75;
	line-height: 30px;
	color: #fff;
	font-size: 16px;
	font-weight: bold;
	padding-left: 7px;
}

#databaseTipsLinkContent p {
	margin: 0;
	padding: 0;
	margin-bottom: 7px;
}

#databaseTipsLinkContent p.databasetitle {
	margin-bottom: 5px;
}

#databaseTipsLinkContent p.databasename {
	font-weight: bold;
	color: #0156AA;
	margin-bottom: 0;
}
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
.ui-state-default .ui-icon { background-image: url(/static/css/custom-theme/images/ui-icons_888888_256x240.png); }
.ui-dialog .ui-resizable-se {
width: 14px;
height: 14px;
right: -1px;
bottom: -1px;
}
</style>
</head>
<body>
    <c:set var="dbid" value="${param.dbid}"/>
	<c:if test="${fn:contains(dbid,'all') or fn:contains(dbid,'cbn')}">
		<p class="databasename">CBNB</p>
		<p class="databasedesc">Chemical Business NewsBase (CBNB) is the
			leading provider of global chemical business news and information.
			Search for facts, figures, views, and comments on the chemical
			industry worldwide, from 1985 to the present. Sources include
			hundreds of core trade journals, newspapers and company newsletters,
			plus books, market research reports, annual and interim company
			reports, press releases and other "grey literature" sources.</p>
	</c:if>

	<c:if test="${fn:contains(dbid,'all') or fn:contains(dbid,'chm')}">
		<p class="databasename">Chimica</p>
		<p class="databasedesc">Chimica provides access to hundreds of the
			most influential international journals focused on chemistry and
			chemical engineering, with emphasis on applied and analytical
			chemistry, extending to physical chemistry, health and safety,
			organic and inorganic chemistry, and materials science. Coverage in
			Chimica starts in 1970.</p>
	</c:if>

	<c:if test="${fn:contains(dbid,'all') or fn:contains(dbid,'cpx') or fn:contains(dbid,'zbf')}">
		<p class="databasename">Compendex</p>
		<p class="databasedesc">Compendex is the most comprehensive
			bibliographic database of scientific and technical engineering
			research available, covering all engineering disciplines. It includes
			millions of bibliographic citations and abstracts from thousands of
			engineering journals and conference proceedings. When combined with
			the Engineering Index Backfile (1884-1969), Compendex covers well
			over 120 years of core engineering literature.</p>
	</c:if>

	<c:if test="${fn:contains(dbid,'all') or fn:contains(dbid,'elt')}">
		<p class="databasename">EnCompassLIT</p>
		<p class="databasedesc">EnCompassLIT&trade; is a bibliographic
			database specifically devoted to covering technical literature
			published worldwide on the downstream petroleum, petrochemical,
			natural gas, energy, and allied industries. Upstream coverage focuses
			solely on oil field chemicals. The content is indexed with a
			controlled vocabulary, the EnCompass Thesaurus, which facilitates
			precise search and discovery.</p>
	</c:if>

	<c:if test="${fn:contains(dbid,'all') or fn:contains(dbid,'ept')}">
		<p class="databasename">EnCompassPAT</p>
		<p class="databasedesc">EnCompassPAT&trade; is the most
			authoritative patent database uniquely devoted to covering worldwide
			patents on the downstream petroleum, petrochemical, natural gas,
			energy, and allied industries. The content is indexed with a
			controlled vocabulary, the EnCompass Thesaurus, which facilitates
			precise search and discovery.</p>
	</c:if>

    <c:if test="${fn:contains(dbid,'all') or fn:contains(dbid,'eup')}">
        <p class="databasename">EP Patents</p>
        <p class="databasedesc">The EP Patents database includes European
            patents grants and applications. EP Patents offers sophisticated
            search and retrieval tools across patents registered with European
            Patent Offices.</p>
    </c:if>

    <c:if test="${fn:contains(dbid,'all') or fn:contains(dbid,'geo')}">
        <p class="databasename">GEOBASE</p>
        <p class="databasedesc">GEOBASE&#174 is a multidisciplinary
            database of indexed research literature on the earth sciences,
            including geology, human and physical geography, environmental
            sciences, oceanography, geomechanics, alternative energy sources,
            pollution, waste management and nature conservation. Covering
            thousands of peer-reviewed journals, trade publications, book series
            and conference proceedings, GEOBASE has the most international
            coverage of any database in the field.</p>
    </c:if>

    <c:if test="${fn:contains(dbid,'all') or fn:contains(dbid,'grf')}">
        <p class="databasename">GeoRef</p>
        <p class="databasedesc">The GeoRef database, established by the
            American Geological Institute in 1966, provides access to the
            geoscience literature of the world. GeoRef is the most comprehensive
            database in the geosciences and continues to grow by more than 90,000
            references a year. The database contains over 2.9 million references
            to geoscience journal articles, books, maps, conference papers,
            reports and theses.</p>
    </c:if>

    <c:if test="${fn:contains(dbid,'all') or fn:contains(dbid,'ins') or fn:contains(dbid,'ibs')}">
        <p class="databasename">Inspec</p>
        <p class="databasedesc">Inspec includes bibliographic citations
            and indexed abstracts from publications in the fields of physics,
            electrical and electronic engineering, communications, computer
            science, control engineering, information technology, manufacturing
            and mechanical engineering, operations research, material science,
            oceanography, engineering mathematics, nuclear engineering,
            environmental science, geophysics, nanotechnology, biomedical
            technology and biophysics.</p>
    </c:if>

	<c:if test="${fn:contains(dbid,'all') or fn:contains(dbid,'nti')}">
		<p class="databasename">NTIS</p>
		<p class="databasedesc">NTIS (The National Technical Information
			Service) is the premier database for accessing unclassified reports
			from influential U.S. and international government agencies. The
			database contains access to millions of critical citations from
			government departments such as NASA, the U.S. Department of Energy
			and the U.S. Department of Defense.</p>
	</c:if>

	<c:if test="${fn:contains(dbid,'all') or fn:contains(dbid,'pch')}">
		<p class="databasename">PaperChem</p>
		<p class="databasedesc">PaperChem is a database of indexed
			bibliographic citations and abstracts from journals, conference
			proceedings, and technical reports focused on pulp and paper
			technology. Coverage is from 1967 to present, and covers such topics
			as the chemistry of cellulose, corrugated and particle board; films,
			foils and laminates; forestry and pulpwood, lignin and extractives,
			non-wovens, and much more.</p>
	</c:if>

    <c:if test="${fn:contains(dbid,'all') or fn:contains(dbid,'upa')}">
        <p class="databasename">US Patents</p>
        <p class="databasedesc">US Patents provides access to a full-text
            patent database, which currently contains over six million patents
            recorded at the U.S. Patent and Trademark Office (USPTO). Records
            cover the period from 1790 to the most recent weekly issue date.
            Patents from 1790-1975 can be searched and retrieved by patent number
            or current US classification code only.</p>
    </c:if>

<%--
	<c:if test="${fn:contains(dbid,'all') or fn:contains(dbid,'pag')}">
		<p class="databasename">Referex</p>
		<p class="databasedesc">Referex Engineering consists of six
			collections of professionally focused eBooks, bringing together key
			sources of engineering reference material. The database is fully
			searchable and delivers full-text content from the following
			collections: Chemical, Petrochemical and Process Engineering; Civil
			and Environmental Engineering; Computing; Electronics and Electrical
			Engineering; Mechanical Engineering and Materials; Networking and
			Security.</p>
	</c:if>
 --%>

</body>
</html>