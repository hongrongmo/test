package org.ei.stripes.action.search;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.stripes.action.DefaultHandler;
import net.sourceforge.stripes.action.DontValidate;
import net.sourceforge.stripes.action.HandlesEvent;
import net.sourceforge.stripes.action.Resolution;
import net.sourceforge.stripes.action.StreamingResolution;
import net.sourceforge.stripes.action.UrlBinding;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.domain.Citation;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.DocID;
import org.ei.domain.DocumentBuilder;
import org.ei.domain.DocumentBuilderException;
import org.ei.domain.EIDoc;
import org.ei.domain.InvalidArgumentException;
import org.ei.domain.Keys;
import org.ei.domain.MultiDatabaseDocBuilder;
import org.ei.exception.InfrastructureException;
import org.ei.parser.base.BaseParser;
import org.ei.parser.base.BooleanQuery;
import org.ei.query.base.HitHighlightFinisher;
import org.ei.query.base.HitHighlighter;
import org.ei.stripes.action.EVActionBean;

@UrlBinding("/search/results/preview.url")
public class SearchResultPreviewAction extends EVActionBean {

    private final static Logger log4j = Logger.getLogger(SearchResultPreviewAction.class);

    private String docId= null;
    private String query = null;
    private BooleanQuery queryTree = null;
    private HitHighlighter hlight = null;
    private static final int MAX_SNIP_LENGTH = 170;

	@SuppressWarnings("unchecked")
	@DefaultHandler
    @DontValidate
    public Resolution getPreviewText() throws InvalidArgumentException, InfrastructureException {
        if (GenericValidator.isBlankOrNull(getDocId())) {
            throw new InvalidArgumentException("'term' must be present in request!");
        }
        List<EIDoc> eiDocs= null;

        List<DocID> docIds = new ArrayList<DocID>();
        DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
        DocID did = new DocID(0, getDocId(), databaseConfig.getDatabase(getDocId().substring(0, 3)));
        docIds.add(did);
        DocumentBuilder builder = new MultiDatabaseDocBuilder();
        BaseParser parser = new BaseParser();

        if(!GenericValidator.isBlankOrNull(query)){
        	queryTree = (BooleanQuery)parser.parse(query);
        	hlight = new HitHighlighter(queryTree);
        }

        try {
			eiDocs = (List<EIDoc>) builder.buildPage(docIds,Citation.CITATION_PREVIEW);
		} catch (DocumentBuilderException e) {
			return new StreamingResolution("application/json", "{\"previewtext\":\"error\"}");
		}


        if(eiDocs == null){
        	return new StreamingResolution("application/json", "{\"previewtext\":\"error\"}");
        }

        if(StringUtils.isBlank(getAbstractText(eiDocs))){
        	return new StreamingResolution("application/json", "{\"previewtext\":\"error\"}");
        }


        String data = getAbstractText(eiDocs);


        return new StreamingResolution("application/json", "{\"previewtext\":\""+ data+"\"}");
    }


	/**
	 * Get the partial abstract and highlight it. Also, return the number of hits left in the string after
	 * the snippet
	 * @return
	 * @throws InvalidArgumentException
	 * @throws InfrastructureException
	 */
	@SuppressWarnings("unchecked")
	@HandlesEvent("partial")
    @DontValidate
    public Resolution getPartialPreviewText() throws InvalidArgumentException, InfrastructureException {
        if (GenericValidator.isBlankOrNull(getDocId())) {
            throw new InvalidArgumentException("'term' must be present in request!");
        }
        List<EIDoc> eiDocs= null;

        List<DocID> docIds = new ArrayList<DocID>();
        DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
        DocID did = new DocID(0, getDocId(), databaseConfig.getDatabase(getDocId().substring(0, 3)));
        docIds.add(did);
        DocumentBuilder builder = new MultiDatabaseDocBuilder();
        BaseParser parser = new BaseParser();

        if(!GenericValidator.isBlankOrNull(query)){
        	queryTree = (BooleanQuery)parser.parse(query);
        	hlight = new HitHighlighter(queryTree);
        }

        try {
			eiDocs = (List<EIDoc>) builder.buildPage(docIds,Citation.CITATION_PREVIEW);
		} catch (DocumentBuilderException e) {
			return new StreamingResolution("application/json", "{\"previewtext\":\"error\",\"countLeft\":0}");
		}


        if(eiDocs == null){
        	return new StreamingResolution("application/json", "{\"previewtext\":\"error\",\"countLeft\":0}");
        }

        if(StringUtils.isBlank(getAbstractText(eiDocs))){
        	return new StreamingResolution("application/json", "{\"previewtext\":\"error\",\"countLeft\":0}");
        }

        return new StreamingResolution("application/json", getPartialAbstractWithAllJSONText(eiDocs));
    }
	/**
	 * Get the abstract and highlight it
	 * @param eiDocs
	 * @return
	 */
	private String getAbstractText(List<EIDoc> eiDocs) {
		EIDoc doc = eiDocs.get(0);
		if(hlight != null){
			doc = (EIDoc) hlight.highlight(eiDocs.get(0));
			return HitHighlightFinisher.addMarkup( doc.getElementDataMap().get(Keys.ABSTRACT).getElementData()[0].replaceAll("\"", "&quot;"));
		}
		return doc.getElementDataMap().get(Keys.ABSTRACT).getElementData()[0].replaceAll("\"", "&quot;");

	}

	/**
	 * get the JSON text for partial snippet of the abstract with the "..." at the end and the count of matches left in it.
	 * @param eiDocs
	 * @return
	 */
	private String getPartialAbstractJSONText(List<EIDoc> eiDocs) {

		EIDoc doc = eiDocs.get(0);
		String data = "{\"previewtext\":\"";
		String tmp = "";
		String all = "";
		if(hlight != null){
			doc = (EIDoc) hlight.highlight(eiDocs.get(0));
			all = HitHighlightFinisher.addMarkup( doc.getElementDataMap().get(Keys.ABSTRACT).getElementData()[0].replaceAll("\"", "&quot;"));
			tmp = createSnippet(all);
			String left = "";
			if(tmp.length() < all.length()){
				// then there was a snippet and we don't just have the same string
				left = all.substring(tmp.length()-1);
			}

			data += tmp + "\", \"countLeft\":"+ (left.split("</span>").length - 1)+"}";
		}else{
			data +=  doc.getElementDataMap().get(Keys.ABSTRACT).getElementData()[0].replaceAll("\"", "&quot;") + "\"}";;
		}


		return data;
	}

	/**
	 * get the JSON text for partial snippet of the abstract with the "..." at the end and the count of matches left in it.
	 * @param eiDocs
	 * @return
	 */
	private String getPartialAbstractWithAllJSONText(List<EIDoc> eiDocs) {

		EIDoc doc = eiDocs.get(0);
		String data = "{\"previewtext\":\"";
		String tmp = "";
		String all = "";
		if(hlight != null){
			doc = (EIDoc) hlight.highlight(eiDocs.get(0));
			all = HitHighlightFinisher.addMarkup( doc.getElementDataMap().get(Keys.ABSTRACT).getElementData()[0].replaceAll("\"", "&quot;"));
			tmp = createSnippet(all);
			String left = "";
			if(GenericValidator.isBlankOrNull(tmp)){
				tmp = all;
			}else if(tmp.length() < all.length()){
				// then there was a snippet and we don't just have the same string
				left = all.substring(tmp.length());
			}

			data += tmp + "\", \"countLeft\":"+ (left.split("</span>").length - 1);
			data += ",\"theRest\":" + "\"" + left + "\"" + "}";
		}else{
			data +=  doc.getElementDataMap().get(Keys.ABSTRACT).getElementData()[0].replaceAll("\"", "&quot;") + "\"}";;
		}


		return data;
	}

	/**
	 * create the snippet MAX_SNIP_LENGTH (170 chars) * 3.
	 * @param all
	 * @return
	 */
	private String createSnippet(String all){
		String snip = "";

		if(all.length() > MAX_SNIP_LENGTH * 3){
			int nextSpace = all.indexOf(" ", MAX_SNIP_LENGTH * 3);
			int nextHit = all.indexOf("<span c",MAX_SNIP_LENGTH * 3);

			//so we don't get the space that is in <span class="hit"
			if (nextHit > 0 && nextHit < nextSpace)	{
				snip = all.substring(0, nextHit);
			}else if(nextSpace > 0){
				snip = all.substring(0, nextSpace);
				if(snip.endsWith("<span")){
					snip = all.substring(0, nextSpace - 5);
				}
			}else{
				return all;
			}

		}

		return snip;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}
    public String getQuery() {
		return query;
	}



	public void setQuery(String query) {
		this.query = query;
	}





}
