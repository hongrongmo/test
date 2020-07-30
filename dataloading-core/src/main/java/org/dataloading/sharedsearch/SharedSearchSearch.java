package org.dataloading.sharedsearch;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.stream.Collectors;
import java.net.HttpURLConnection;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;

/**
 * 
 * @author TELEBH
 * @Date: 06/17/2020
 * @Description: Pull AUIDS and AFIDS from ES via Shared Search Service API to compare IDS with 
 * doc_count discrepancy between ES and Fast
 * i.e. (7410355831 wn auid) returns 146 hit count in Fast/EV Prod
 * authorId:"7410355831" resturns 142 hit count in ES (DEV)  
 * 
 * SAMPLE RESPONSE: 
 * 
 * Response: {  "totalResultsCount" : 142,  "hits" : [ ],  "searchProduct" : "engineering_village",  "searchEntity" : "document",  "searchType" : "result",  "errors" : [ ],  "timings" : [ {    "sharedSearchAPI" : {      "queryArrival" : "2020-06-18T20:40:11.346",      "totalElapsedMilliSecs" : 7,      "blockedMilliSecs" : 6,      "blockedPercent" : 85    },    "elastic" : {      "queryTimeMilliSecs" : 1    }  } ],  "diagnostics" : [ {    "requestUri" : "http://shared-search-service-api.cert.scopussearch.net/document/v1/query/result",    "submittedQuery" : {      "query" : {        "queryString" : "authorId:7410355831 AND database:cpx",        "defaultOperator" : "AND"      },      "resultSet" : {        "skip" : 0,        "amount" : 0      }    },    "validatedQuery" : {      "query" : {        "queryString" : "authorId:7410355831 AND database:cpx",        "defaultOperator" : "AND",        "type" : "best_fields",        "tieBreaker" : 0.0,        "fieldNames" : [ "all" ]      },      "facetFilters" : [ ],      "rangeQueries" : [ ],      "resultSet" : {        "skip" : 0,        "amount" : 0      },      "returnFields" : [ "eidocid" ],      "sortBy" : [ {        "fieldName" : "relevance",        "order" : "desc"      } ],      "stemming" : false,      "highlight" : false    },    "translatedQuery" : {      "from" : 0,      "size" : 0,      "query" : {        "function_score" : {          "query" : {            "bool" : {              "must" : [ {                "query_string" : {                  "query" : "authorId:7410355831 AND database:cpx",                  "fields" : [ "abstract^8.0", "all^1.0", "controlledTerms^25.0", "title^100.0", "uncontrolledTerms^25.0" ],                  "type" : "best_fields",                  "tie_breaker" : 0.0,                  "default_operator" : "and",                  "max_determinized_states" : 10000,                  "enable_position_increments" : true,                  "fuzziness" : "AUTO",                  "fuzzy_prefix_length" : 0,                  "fuzzy_max_expansions" : 50,                  "phrase_slop" : 0,                  "escape" : false,                  "auto_generate_synonyms_phrase_query" : true,                  "fuzzy_transpositions" : true,                  "boost" : 1.0                }              } ],              "adjust_pure_negative" : true,              "boost" : 1.0            }          },          "functions" : [ {            "filter" : {              "match_all" : {                "boost" : 1.0              }            },            "weight" : 1.0,            "gauss" : {              "publicationYear" : {                "origin" : 2020,                "scale" : 7.0,                "offset" : 0.5,                "decay" : 0.5              },              "multi_value_mode" : "MIN"            }          } ],          "score_mode" : "multiply",          "boost_mode" : "multiply",          "max_boost" : 3.4028235E38,          "boost" : 1.0        }      },      "_source" : {        "includes" : [ "eidocid" ],        "excludes" : [ ]      },      "stored_fields" : [ ],      "sort" : [ {        "_score" : {          "order" : "desc"        }      } ]    }  } ]}
7410355831	142
Response: {  "totalResultsCount" : 24,  "hits" : [ ],  "searchProduct" : "engineering_village",  "searchEntity" : "document",  "searchType" : "result",  "errors" : [ ],  "timings" : [ {    "sharedSearchAPI" : {      "queryArrival" : "2020-06-18T20:40:11.592",      "totalElapsedMilliSecs" : 22,      "blockedMilliSecs" : 20,      "blockedPercent" : 90    },    "elastic" : {      "queryTimeMilliSecs" : 16    }  } ],  "diagnostics" : [ {    "requestUri" : "http://shared-search-service-api.cert.scopussearch.net/document/v1/query/result",    "submittedQuery" : {      "query" : {        "queryString" : "authorId:7006377109 AND database:cpx",        "defaultOperator" : "AND"      },      "resultSet" : {        "skip" : 0,        "amount" : 0      }    },    "validatedQuery" : {      "query" : {        "queryString" : "authorId:7006377109 AND database:cpx",        "defaultOperator" : "AND",        "type" : "best_fields",        "tieBreaker" : 0.0,        "fieldNames" : [ "all" ]      },      "facetFilters" : [ ],      "rangeQueries" : [ ],      "resultSet" : {        "skip" : 0,        "amount" : 0      },      "returnFields" : [ "eidocid" ],      "sortBy" : [ {        "fieldName" : "relevance",        "order" : "desc"      } ],      "stemming" : false,      "highlight" : false    },    "translatedQuery" : {      "from" : 0,      "size" : 0,      "query" : {        "function_score" : {          "query" : {            "bool" : {              "must" : [ {                "query_string" : {                  "query" : "authorId:7006377109 AND database:cpx",                  "fields" : [ "abstract^8.0", "all^1.0", "controlledTerms^25.0", "title^100.0", "uncontrolledTerms^25.0" ],                  "type" : "best_fields",                  "tie_breaker" : 0.0,                  "default_operator" : "and",                  "max_determinized_states" : 10000,                  "enable_position_increments" : true,                  "fuzziness" : "AUTO",                  "fuzzy_prefix_length" : 0,                  "fuzzy_max_expansions" : 50,                  "phrase_slop" : 0,                  "escape" : false,                  "auto_generate_synonyms_phrase_query" : true,                  "fuzzy_transpositions" : true,                  "boost" : 1.0                }              } ],              "adjust_pure_negative" : true,              "boost" : 1.0            }          },          "functions" : [ {            "filter" : {              "match_all" : {                "boost" : 1.0              }            },            "weight" : 1.0,            "gauss" : {              "publicationYear" : {                "origin" : 2020,                "scale" : 7.0,                "offset" : 0.5,                "decay" : 0.5              },              "multi_value_mode" : "MIN"            }          } ],          "score_mode" : "multiply",          "boost_mode" : "multiply",          "max_boost" : 3.4028235E38,          "boost" : 1.0        }      },      "_source" : {        "includes" : [ "eidocid" ],        "excludes" : [ ]      },      "stored_fields" : [ ],      "sort" : [ {        "_score" : {          "order" : "desc"        }      } ]    }  } ]}
7006377109	24

 */
public class SharedSearchSearch {
	
	String database;
	String url = "https://shared-search-service-api.cert.scopussearch.net/document/v1/query/result";
	
	public SharedSearchSearch(String sharedSearchUrl, String database)
	{
		this.url = sharedSearchUrl;
		this.database = database;
		
	}
	public void runESQuery(String value, String query, BufferedWriter bw, Logger logger) {
		
		//String encodedQuery;
		URL urlObject;
		try
		{
			//a. encode query
			//encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
			//System.out.println("query: " + query);		// only for debugging
			
			
			urlObject = new URL(url);
			HttpURLConnection httpCon = (HttpURLConnection) urlObject.openConnection();
			httpCon.setRequestMethod("POST");
			httpCon.setRequestProperty("x-els-product", "engineering_village");
			httpCon.setRequestProperty("x-els-diagnostics", "false");
			httpCon.setRequestProperty("Content-Type", "application/json");
			httpCon.setDoOutput(true);
			OutputStreamWriter writer = new OutputStreamWriter(httpCon.getOutputStream());
			writer.write(query);
			writer.close();
			int responseCode = httpCon.getResponseCode();
			
			// Only read response if connection was successful
			if(responseCode == HttpURLConnection.HTTP_OK)
			{
				BufferedReader in = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
				String line;
				StringBuilder response = new StringBuilder();
				while((line = in.readLine()) != null)
				{
					response.append(line);
					
					// Response Is to big , to speedup the process only read up to hits 
					if(line.equalsIgnoreCase("hits"))
						break;
				}
				//System.out.println("Response: " + response);   // only for debugging
				// close stream
				in.close();
				
				// process response and fetch HitCount from result
				processResponse(value, response.toString(), bw);
				
			}
			else
			{
				System.out.println("Response Code: " + responseCode);
				byte[] responseContents = httpCon.getInputStream().readAllBytes();
				System.out.println("sharedsearch response: " + Arrays.toString(responseContents));
				System.out.println("POST request did not work for ESQuery: " + query);
			}
		}
		catch(Exception e)
		{
			System.out.println("Exception runing ES Query!!!");
			System.out.println(e.getMessage());
			logger.info(query);
			e.printStackTrace();
		}
		
	}

	private void processResponse(String value, String response, BufferedWriter bw) throws ParseException, IOException {
		if(!(response.toString().isEmpty()))
		{
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(response);
			Integer hitCount = Integer.parseInt(json.get("totalResultsCount").toString());
			//System.out.println(value + "\t" + hitCount);   // only for debugging
			JSONArray hits = (JSONArray) json.get("hits");
			
			synchronized(this)
			{
				bw.write(value + "\t" + hitCount);
				bw.newLine();
				if(!hits.isEmpty())
				{
					
					Iterator<JSONObject> itr = hits.iterator();
					while(itr.hasNext())
					{
						String[] eidoc = itr.next().toString().replaceAll("[\"\\{\\}]+","").split(":");
						if(eidoc.length >1)
							
							bw.write(eidoc[1] +"\n");
						
					}
					
				}

			}
		}
		
	}

	@SuppressWarnings("unchecked")
	public String buildESQuery(String value, String searchField) {
		
		JSONObject query = new JSONObject();
		
		JSONObject queryString = new JSONObject();
		queryString.put("queryString", searchField + ":" + value + " AND database:" + database);
		queryString.put("defaultOperator", "AND");
		query.put("query",queryString);
		
		JSONArray returnFields = new JSONArray();
		returnFields.add("eidocid");
		query.put("returnFields", returnFields);
		

		JSONObject result = new JSONObject();
		result.put("skip", 0);
		result.put("amount", 0);
		query.put("resultSet", result);
		
		return query.toJSONString();
			
		}	

	
	@SuppressWarnings("unchecked")
	public String buildESQueryWithMIDReturn(String value, String searchField) {
		
		JSONObject query = new JSONObject();
		
		JSONObject queryString = new JSONObject();
		queryString.put("queryString", searchField + ":" + value + " AND database:" + database);
		queryString.put("defaultOperator", "AND");
		query.put("query",queryString);
		
		JSONArray returnFields = new JSONArray();
		returnFields.add("eidocid");
		query.put("returnFields", returnFields);
		

		JSONObject result = new JSONObject();
		result.put("skip", 0);
		result.put("amount", 1000);
		query.put("resultSet", result);
		
		return query.toJSONString();
			
		}	
}
