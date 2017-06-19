/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  javax.help.SearchHit
 *  javax.help.SearchTOCItem
 *  javax.help.search.MergingSearchEngine
 *  javax.help.search.SearchEvent
 *  javax.help.search.SearchItem
 *  javax.help.search.SearchListener
 *  javax.help.search.SearchQuery
 *  javax.servlet.jsp.JspException
 *  javax.servlet.jsp.JspTagException
 *  javax.servlet.jsp.JspWriter
 *  javax.servlet.jsp.PageContext
 *  javax.servlet.jsp.tagext.BodyContent
 *  javax.servlet.jsp.tagext.BodyTagSupport
 */
package javax.help.tagext;

import java.io.IOException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.Map;
import javax.help.NavigatorView;
import javax.help.SearchHit;
import javax.help.SearchTOCItem;
import javax.help.SearchView;
import javax.help.search.MergingSearchEngine;
import javax.help.search.SearchEvent;
import javax.help.search.SearchItem;
import javax.help.search.SearchListener;
import javax.help.search.SearchQuery;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class SearchTOCItemTag
extends BodyTagSupport
implements SearchListener {
    private Enumeration treeEnum;
    private Vector nodes;
    private SearchView view;
    private HelpBroker hb;
    private String query;
    private MergingSearchEngine helpsearch;
    private SearchQuery searchquery;
    private boolean searchFinished;
    private static final boolean debug = false;

    public void setSearchView(SearchView searchView) {
        this.view = searchView;
    }

    public void setHelpBroker(HelpBroker helpBroker) {
        this.hb = helpBroker;
    }

    public void setQuery(String string) {
        this.query = string;
    }

    public synchronized int doStartTag() {
        if (this.helpsearch == null) {
            this.helpsearch = new MergingSearchEngine((NavigatorView)this.view);
            this.searchquery = this.helpsearch.createQuery();
            this.searchquery.addSearchListener((SearchListener)this);
            this.addSubHelpSets(this.view.getHelpSet());
        }
        if (this.searchquery.isActive()) {
            this.searchquery.stop();
        }
        this.searchquery.start(this.query, Locale.getDefault());
        if (!this.searchFinished) {
            try {
                this.wait();
            }
            catch (InterruptedException var1_1) {
                // empty catch block
            }
        }
        if (this.treeEnum.hasMoreElements()) {
            SearchTOCItem searchTOCItem = (SearchTOCItem)this.treeEnum.nextElement();
            this.setNodeAttributes(searchTOCItem);
            return 2;
        }
        return 0;
    }

    private void addSubHelpSets(HelpSet helpSet) {
        Enumeration enumeration = helpSet.getHelpSets();
        while (enumeration.hasMoreElements()) {
            HelpSet helpSet2 = (HelpSet)enumeration.nextElement();
            if (helpSet2 == null) continue;
            NavigatorView[] arrnavigatorView = helpSet2.getNavigatorViews();
            int n = 0;
            while (n < arrnavigatorView.length) {
                if (arrnavigatorView[n] instanceof SearchView) {
                    this.helpsearch.merge(arrnavigatorView[n]);
                }
                ++n;
            }
            this.addSubHelpSets(helpSet2);
        }
    }

    public int doAfterBody() throws JspException {
        BodyContent bodyContent = this.getBodyContent();
        try {
            bodyContent.writeOut((Writer)this.getPreviousOut());
        }
        catch (IOException var2_2) {
            throw new JspTagException("SearchTOCItemTag: " + var2_2.getMessage());
        }
        bodyContent.clearBody();
        if (this.treeEnum.hasMoreElements()) {
            SearchTOCItem searchTOCItem = (SearchTOCItem)this.treeEnum.nextElement();
            this.setNodeAttributes(searchTOCItem);
            return 2;
        }
        return 0;
    }

    private void setNodeAttributes(SearchTOCItem searchTOCItem) {
        this.pageContext.setAttribute("name", (Object)searchTOCItem.getName());
        this.pageContext.setAttribute("helpID", (Object)this.getMapID(searchTOCItem));
        this.pageContext.setAttribute("confidence", (Object)Double.toString(searchTOCItem.getConfidence()));
        this.pageContext.setAttribute("hits", (Object)Integer.toString(searchTOCItem.hitCount()));
        this.pageContext.setAttribute("contentURL", (Object)searchTOCItem.getURL().toExternalForm());
        this.pageContext.setAttribute("hitBoundries", (Object)this.getSearchHits(searchTOCItem));
    }

    private String getMapID(SearchTOCItem searchTOCItem) {
        URL uRL = searchTOCItem.getURL();
        HelpSet helpSet = this.hb.getHelpSet();
        Map map = helpSet.getCombinedMap();
        Map.ID iD = map.getIDFromURL(uRL);
        if (iD == null) {
            return "";
        }
        return iD.id;
    }

    private String getSearchHits(SearchTOCItem searchTOCItem) {
        String string = "{ ";
        Enumeration enumeration = searchTOCItem.getSearchHits();
        while (enumeration.hasMoreElements()) {
            SearchHit searchHit = (SearchHit)enumeration.nextElement();
            string = string + "{" + searchHit.getBegin() + "," + searchHit.getEnd() + "}";
            if (!enumeration.hasMoreElements()) continue;
            string = string + ", ";
        }
        string = string + " }";
        return string;
    }

    public synchronized void itemsFound(SearchEvent searchEvent) {
        Enumeration enumeration = searchEvent.getSearchItems();
        while (enumeration.hasMoreElements()) {
            SearchTOCItem searchTOCItem;
            URL uRL;
            SearchItem searchItem = (SearchItem)enumeration.nextElement();
            try {
                uRL = new URL(searchItem.getBase(), searchItem.getFilename());
            }
            catch (MalformedURLException var6_6) {
                SearchTOCItemTag.debug("Failed to create URL from " + searchItem.getBase() + "|" + searchItem.getFilename());
                continue;
            }
            boolean bl = false;
            Enumeration enumeration2 = this.nodes.elements();
            while (enumeration2.hasMoreElements()) {
                searchTOCItem = (SearchTOCItem)enumeration2.nextElement();
                URL uRL2 = searchTOCItem.getURL();
                if (uRL2 == null || uRL == null || !uRL.sameFile(uRL2)) continue;
                searchTOCItem.addSearchHit(new SearchHit(searchItem.getConfidence(), searchItem.getBegin(), searchItem.getEnd()));
                bl = true;
                break;
            }
            if (bl) continue;
            searchTOCItem = new SearchTOCItem(searchItem);
            this.nodes.addElement(searchTOCItem);
        }
    }

    public synchronized void searchStarted(SearchEvent searchEvent) {
        this.nodes = new Vector();
        this.searchFinished = false;
    }

    public synchronized void searchFinished(SearchEvent searchEvent) {
        this.searchFinished = true;
        this.treeEnum = this.nodes.elements();
        this.notifyAll();
    }

    private static void debug(String string) {
    }
}

