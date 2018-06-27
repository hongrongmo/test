/**
 *
 */
package org.ei.domain;

import java.net.URLEncoder;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.connectionpool.ConnectionBroker;
import org.ei.exception.InfrastructureException;
import org.ei.exception.SystemErrorCodes;
import org.ei.query.base.FastQueryWriter;
import org.ei.util.GUID;
import org.ei.util.StringUtil;
import org.ei.xmlio.XMLIOException;

/**
 * @author harovetm
 *
 */
public class RSSFeed {
    public static final Logger log4j = Logger.getLogger(RSSFeed.class);

    public static String ERROR_MESSAGE =
        "<rss version=\"2.0\">" +
        "<channel><title>RSS feed error</title><link>http://www.engineeringvillage.com</link><description></description><language>en-us</language>" +
        "  <item><title>This RSS feed is not currently available</title><description>An error has occurred.</description><link>http://www.engineeringvillage.com</link></item>" +
        "<copyright>Copyright " +  Calendar.getInstance().get(Calendar.YEAR) + " Elsevier Inc.</copyright></channel></rss>";

    private String queryid;

    private String sessionid;
    private String servername;
    private Channel channel;

    public RSSFeed(String queryid, String servername) throws InfrastructureException {
        this.channel = new Channel();
        this.queryid = queryid;
        this.servername = servername;

        buildChannel();
    }

    /**
     * @return the channel
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * @return the queryid
     */
    public String getQueryid() {
        return queryid;
    }

    /**
     * @return the channel
     */
    public Channel buildChannel() throws InfrastructureException {
        SearchControl sc = null;
        SearchResult result = null;
        try {
            sc = new FastSearchControl();
            DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
            String[] rssInfo = getQuery(this.queryid);
            if (rssInfo == null || rssInfo.length < 2) {
                log4j.error("RSS.getQuery failed!  Invalid rss info returned for query ID: " + this.queryid);
                throw new InfrastructureException(SystemErrorCodes.RSS_FEED_ERROR, "Invalid RSS info returned!");
            }
            String database = rssInfo[0];
            String query = rssInfo[1];

            // Get user credentials
            int dbmask = Integer.parseInt(database);
            List<String> carlist = buildUserCartridgeForRSS(dbmask);
            String[] credentials = null;
            if (carlist.size() <= 0) {
                credentials = new String[3];
                credentials[0] = "CPX";
                credentials[1] = "INS";
                credentials[2] = "NTI";
            } else {
                credentials = (String[]) carlist.toArray(new String[carlist.size()]);
            }

            // Create and compile new Query object
            org.ei.domain.Query queryObject = new org.ei.domain.Query(databaseConfig, credentials);
            queryObject.setDataBase(dbmask);
            queryObject.setID(new GUID().toString());
            queryObject.setSearchType(org.ei.domain.Query.TYPE_EXPERT);
            queryObject.setSortOption(new Sort("relevance", "dw"));
            queryObject.setSearchPhrase(query, "", "", "", "", "", "", "");
            queryObject.setAutoStemming("on");
            queryObject.setLastFourUpdates("1");
            queryObject.setSearchQueryWriter(new FastQueryWriter());
            queryObject.compile();

            // Get search results
            result = sc.openSearch(queryObject, null, 25, true);
            List<DocID> docIds = sc.getDocIDRange(1, 400);
            queryObject.setRecordCount(Integer.toString(result.getHitCount()));

            // Create channel XML
            this.channel.setTitle("Engineering Village RSS results for database " + DatabaseDisplayHelper.getDisplayName(dbmask).replaceAll("&", "&amp;") + " and search query of " + queryObject.getDisplayQuery());
            this.channel.setCopyright("Copyright " + Calendar.getInstance().get(Calendar.YEAR) + " Elsevier Inc. All rights reserved.");
            this.channel.setLink("http://" + servername + "/search/submit.url?searchtype=Expert&amp;database=" + dbmask + "&amp;searchWord1=" + URLEncoder.encode(query, "UTF-8") + "&amp;yearselect=lastupdate&amp;updatesNo=1");
            this.channel.setDescription(query);

            Channel.Image image = new Channel.Image();
            image.setTitle("Engineering Village Icon");
            image.setLink("http://" + servername + "/home.url");
            image.setUrl("http://" + servername + "/static/images/engineering_village_favicon.gif");

            this.channel.setImage(image);

            SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date today = new Date();
            this.channel.setPubdate(dt.format(today));

            // Iterate doc IDs to create items!
            Pagemaker pagemaker = new Pagemaker(this.sessionid, 25, docIds, Abstract.ABSTRACT_FORMAT);
            while (pagemaker.hasMorePages()) {
                List builtDocuments = pagemaker.nextPage();
                for (int i = 0; i < builtDocuments.size(); i++) {
                    EIDoc eidoc = (EIDoc) builtDocuments.get(i);
                    Channel.Item item = new Channel.Item();

                    // Get the title
                    String title = eidoc.getElementDataMap().get(Keys.TITLE).getElementData()[0];
                    if (GenericValidator.isBlankOrNull(title)) {
                        title = "No title";
                    }
                    item.setTitle(title);

                    // Build a link to the document
                    item.setLink("http://" + servername + "/blog/document.url?mid=" + eidoc.getDocID().getDocID() + "&amp;database=" + eidoc.getDatabase());

                    // Add a description
                    String description = "No abstract available...";
                    if (eidoc.getElementDataMap().get(Keys.ABSTRACT) != null) {
                        description = eidoc.getElementDataMap().get(Keys.ABSTRACT).getElementData()[0];
                        description = StringUtil.teaser(description) + "...";
                    } else if (eidoc.getElementDataMap().get(Keys.ABSTRACT2) != null) {
                        description = eidoc.getElementDataMap().get(Keys.ABSTRACT2).getElementData()[0];
                        description = StringUtil.teaser(description) + "...";
                    }
                    item.setDescription(description);

                    // Add GUID (doc id)
                    item.setGuid(eidoc.getDocID().getDocID());

                    // Add pub date
                    if (eidoc.getElementDataMap().get(Keys.PUBLICATION_DATE) != null) {
                        item.setPubdate(eidoc.getElementDataMap().get(Keys.PUBLICATION_DATE).getElementData()[0]);
                    } else if (eidoc.getElementDataMap().get(Keys.PUBLICATION_YEAR) != null) {
                        item.setPubdate(eidoc.getElementDataMap().get(Keys.PUBLICATION_YEAR).getElementData()[0]);
                    }

                    // Add it!
                    this.channel.addItem(item);
                }
            }

            // The legacy code used to set log properties.  Disabling for now but leaving the code here
            // in case we need it...
            /*
                LogEntry logentry = context.getLogEntry();
                Properties logproperties = logentry.getLogProperties();
                logproperties.put("search_id", queryObject.getID());
                logproperties.put("query_string", queryObject.getPhysicalQuery());
                logproperties.put("sort_by", queryObject.getSortOption().getSortField());
                logproperties.put("suppress_stem", queryObject.getAutoStemming());
                logproperties.put("context", "search");
                logproperties.put("action", "search");
                logproperties.put("type", "basic");
                logproperties.put("rss", "y");
                logproperties.put("db_name", dbName);
                logproperties.put("page_size", "25");
                logproperties.put("format", format);
                logproperties.put("doc_id", " ");
                logproperties.put("num_recs", totalDocCount);
                logproperties.put("doc_index", "1");
                logproperties.put("hits", totalDocCount);

             */

        } catch (Exception e) {
            throw new InfrastructureException(SystemErrorCodes.RSS_FEED_ERROR, e);
        }
        return this.channel;
    }

    /**
     * Channel inner class - channel is composed of: The <channel> element usually contains one or more <item> elements. Each <item> element defines an article
     * or "story" in the RSS feed.
     *
     * <title> - Defines the title of the channel (e.g. W3Schools Home Page) <link> - Defines the hyperlink to the channel (e.g. http://www.w3schools.com)
     * <description> - Describes the channel (e.g. Free web building tutorials) <category> (optional) child element is used to specify a category for your feed.
     * <copyright> (optional) child element notifies about copyrighted material. <language> (optional) child element is used to specify the language used to
     * write your document. <pubDate> (optional) element defines the last publication date for the content in the RSS feed. <image> (optional) child element
     * allows an image to be displayed when aggregators present a feed.
     *
     * @author harovetm
     *
     */
    public static class Channel {
        private String title;
        private String link;
        private String description;
        private int ttl = 2280;
        private String copyright;
        private String category;
        private String language = "en-US";
        private String pubdate;
        private Image image;

        private List<Channel.Item> items = new ArrayList<Channel.Item>();

        /**
         * @return the items
         */
        public List<Channel.Item> getItems() {
            return items;
        }

        public void addItem(Channel.Item item) {
            items.add(item);
        }

        /**
         * Image inner class (image for Channel) - composed of <url> - Defines the URL to the image <title> - Defines the text to display if the image could not
         * be shown <link> - Defines the hyperlink to the website that offers the channel
         *
         * @author harovetm
         *
         */
        public static class Image {
            private String url;
            private String title;
            private String link;

            /**
             * @return the url
             */
            public String getUrl() {
                return url;
            }

            /**
             * @param url
             *            the url to set
             */
            public void setUrl(String url) {
                this.url = url;
            }

            /**
             * @return the title
             */
            public String getTitle() {
                return title;
            }

            /**
             * @param title
             *            the title to set
             */
            public void setTitle(String title) {
                this.title = title;
            }

            /**
             * @return the link
             */
            public String getLink() {
                return link;
            }

            /**
             * @param link
             *            the link to set
             */
            public void setLink(String link) {
                this.link = link;
            }
        }

        /**
         * @return the title
         */
        public String getTitle() {
            return title;
        }

        /**
         * @param title
         *            the title to set
         */
        public void setTitle(String title) {
            this.title = title;
        }

        /**
         * @return the link
         */
        public String getLink() {
            return link;
        }

        /**
         * @param link
         *            the link to set
         */
        public void setLink(String link) {
            this.link = link;
        }

        /**
         * @return the description
         */
        public String getDescription() {
            return description;
        }

        /**
         * @param description
         *            the description to set
         */
        public void setDescription(String description) {
            this.description = description;
        }

        /**
         * @return the ttl
         */
        public int getTtl() {
            return ttl;
        }

        /**
         * @param ttl
         *            the ttl to set
         */
        public void setTtl(int ttl) {
            this.ttl = ttl;
        }

        /**
         * @return the copyright
         */
        public String getCopyright() {
            return copyright;
        }

        /**
         * @param copyright
         *            the copyright to set
         */
        public void setCopyright(String copyright) {
            this.copyright = copyright;
        }

        /**
         * @return the category
         */
        public String getCategory() {
            return category;
        }

        /**
         * @param category
         *            the category to set
         */
        public void setCategory(String category) {
            this.category = category;
        }

        /**
         * @return the language
         */
        public String getLanguage() {
            return language;
        }

        /**
         * @param language
         *            the language to set
         */
        public void setLanguage(String language) {
            this.language = language;
        }

        /**
         * @return the pubdate
         */
        public String getPubdate() {
            return pubdate;
        }

        /**
         * @param pubdate
         *            the pubdate to set
         */
        public void setPubdate(String pubdate) {
            this.pubdate = pubdate;
        }

        /**
         * @return the image
         */
        public Image getImage() {
            return image;
        }

        /**
         * @param image
         *            the image to set
         */
        public void setImage(Image image) {
            this.image = image;
        }

        /**
         * An RSS channel item
         * <title> Required. Defines the title of the item
         * <link>  Required. Defines the hyperlink to the item
         * <description>   Required. Describes the item
         * <author>    Optional. Specifies the e-mail address to the author of the item
         * <category>  Optional. Defines one or more categories the item belongs to
         * <comments>  Optional. Allows an item to link to comments about that item
         * <enclosure> Optional. Allows a media file to be included with the item
         * <guid>  Optional. Defines a unique identifier for the item
         * <pubDate>   Optional. Defines the last-publication date for the item
         * <source>    Optional. Specifies a third-party source for the item
         * @author harovetm
         *
         */
        public static class Item {
            private String title;
            private String link;
            private String description;

            private String guid;
            private String pubdate;

            /**
             * @return the title
             */
            public String getTitle() {
                return title;
            }
            /**
             * @param title the title to set
             */
            public void setTitle(String title) {
                this.title = title;
            }
            /**
             * @return the link
             */
            public String getLink() {
                return link;
            }
            /**
             * @param link the link to set
             */
            public void setLink(String link) {
                this.link = link;
            }
            /**
             * @return the description
             */
            public String getDescription() {
                return description;
            }
            /**
             * @param description the description to set
             */
            public void setDescription(String description) {
                this.description = description;
            }
            /**
             * @return the guid
             */
            public String getGuid() {
                return guid;
            }
            /**
             * @param guid the guid to set
             */
            public void setGuid(String guid) {
                this.guid = guid;
            }
            /**
             * @return the pubdate
             */
            public String getPubdate() {
                return pubdate;
            }
            /**
             * @param pubdate the pubdate to set
             */
            public void setPubdate(String pubdate) {
                this.pubdate = pubdate;
            }
        }

}

    /**
     * Get RSS query from database.  Query should have 2 parts:  1) DATABASE and 2) QUERY
     * @param id
     * @return
     * @throws XMLIOException
     */
    public static String[] getQuery(String id) throws XMLIOException {

        Connection con = null;
        ConnectionBroker broker = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;
        String[] rssinfo = null;
        CallableStatement proc = null;

        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SESSION_POOL);

            try {
                pstmt = con.prepareStatement("SELECT QUERY, DATABASE FROM RSS_QUERY WHERE RSSID=?");
                pstmt.setString(1, id);
                rset = pstmt.executeQuery();

                while (rset.next()) {
                    rssinfo = new String[2];
                    rssinfo[1] = rset.getString("QUERY");
                    rssinfo[0] = rset.getString("DATABASE");
                }
            } finally {
                if (rset != null) {
                    try {
                        rset.close();
                    } catch (Exception e) {
                    }
                }

                if (pstmt != null) {
                    try {
                        pstmt.close();
                    } catch (Exception sqle) {
                    }
                }
            }

            if (rssinfo != null) {
                try {
                    proc = con.prepareCall("{ call RSS_touch(?)}");
                    proc.setString(1, id);
                    proc.executeUpdate();
                } finally {
                    if (proc != null) {
                        try {
                            proc.close();
                        } catch (Exception sqle) {
                        }
                    }
                }
            }
        } catch (Exception sqle) {
            throw new XMLIOException(sqle);
        } finally {
            if (con != null) {
                try {
                    broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
                } catch (Exception cpe) {
                }
            }
        }

        return rssinfo;
    }

    /**
     * Set RSS queries into database
     *
     * @param rssID
     * @param rssQuery
     * @param database
     * @param customerID
     * @throws XMLIOException
     */
    public static void setQuery(String rssID, String rssQuery, String database, String customerID) throws XMLIOException {
        Connection con = null;
        ConnectionBroker broker = null;
        CallableStatement proc = null;
        try {
            broker = ConnectionBroker.getInstance();
            con = broker.getConnection(DatabaseConfig.SESSION_POOL);
            proc = con.prepareCall("{ call RSS_setQuery(?,?,?,?)}");
            proc.setString(1, rssID);
            proc.setString(2, rssQuery);
            proc.setString(3, database);
            proc.setString(4, customerID);
            proc.executeUpdate();
        } catch (Exception sqle) {
            throw new XMLIOException(sqle);
        } finally {
            if (proc != null) {
                try {
                    proc.close();
                } catch (Exception sqle) {
                }
            }

            if (con != null) {
                try {
                    broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
                } catch (Exception cpe) {
                }
            }
        }
    }

    /**
     * This method takes a database mask from a previously created RSS feed and converts
     * it to a set of credentials.
     * @param dbmask
     * @return
     */
    private static List<String> buildUserCartridgeForRSS(int dbmask) {
        if (dbmask <= 0) {
            throw new IllegalArgumentException("DB mask is incorrect!");
        }

        List<String> cartridge = new ArrayList<String>();
        if ((dbmask & DatabaseConfig.CPX_MASK) == DatabaseConfig.CPX_MASK) {
            cartridge.add(DatabaseConfig.CPX_PREF);
        }
        if ((dbmask & DatabaseConfig.INS_MASK) == DatabaseConfig.INS_MASK) {
            cartridge.add(DatabaseConfig.INS_PREF);
        }
        if ((dbmask & DatabaseConfig.CBN_MASK) == DatabaseConfig.CBN_MASK) {
            cartridge.add(DatabaseConfig.CBN_PREF);
        }
        if ((dbmask & DatabaseConfig.CHM_MASK) == DatabaseConfig.CHM_MASK) {
            cartridge.add(DatabaseConfig.CHM_PREF);
        }
        if ((dbmask & DatabaseConfig.ELT_MASK) == DatabaseConfig.ELT_MASK) {
            cartridge.add(DatabaseConfig.ELT_PREF);
        }
        if ((dbmask & DatabaseConfig.EPT_MASK) == DatabaseConfig.EPT_MASK) {
            cartridge.add(DatabaseConfig.EPT_PREF);
        }
        if ((dbmask & DatabaseConfig.EUP_MASK) == DatabaseConfig.EUP_MASK) {
            cartridge.add(DatabaseConfig.EUP_PREF);
        }
        if ((dbmask & DatabaseConfig.GEO_MASK) == DatabaseConfig.GEO_MASK) {
            cartridge.add(DatabaseConfig.GEO_PREF);
        }
        if ((dbmask & DatabaseConfig.GRF_MASK) == DatabaseConfig.GRF_MASK) {
            cartridge.add(DatabaseConfig.GRF_PREF);
        }
        if ((dbmask & DatabaseConfig.NTI_MASK) == DatabaseConfig.NTI_MASK) {
            cartridge.add(DatabaseConfig.NTI_PREF);
        }
        if ((dbmask & DatabaseConfig.PCH_MASK) == DatabaseConfig.PCH_MASK) {
            cartridge.add(DatabaseConfig.PCH_PREF);
        }
        if ((dbmask & DatabaseConfig.UPA_MASK) == DatabaseConfig.UPA_MASK) {
            cartridge.add(DatabaseConfig.UPT_PREF);
        }
        return cartridge;
    }


}
