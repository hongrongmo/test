package org.ei.domain;

import java.util.*;

public class Pagemaker {
    private int pageSize;
    private List<DocID> fullList;
    private int fullListSize;
    private String sessionID;
    private int currentIndex = 0;
    private String dataFormat;
    private MultiDatabaseDocBuilder builder = new MultiDatabaseDocBuilder();

    public int getCurrentIndex() {
        return this.currentIndex;
    }

    public void setCurrentIndex(int _currentIndex) {
        this.currentIndex = _currentIndex;
    }

    public Pagemaker(String _sessionID, int _pageSize, List<DocID> _fullList, String _dataFormat) {
        this.sessionID = _sessionID;
        this.pageSize = _pageSize;
        this.fullList = _fullList;
        this.fullListSize = _fullList.size();
        this.dataFormat = _dataFormat;
    }

    public boolean hasMorePages() {
        if (currentIndex < fullListSize) {
            return true;
        }

        return false;
    }

    private List<?> buildPage(List<DocID> didList) throws Exception {

        List<?> docList = this.builder.buildPage(didList, this.dataFormat);

        return docList;
    }

    public List<?> nextPage() throws Exception {
        ArrayList<DocID> list = new ArrayList<DocID>();
        for (int i = 0; i < pageSize; i++) {
            if (currentIndex < fullListSize) {
                list.add(fullList.get(currentIndex));
            } else {
                break;
            }

            currentIndex++;
        }

        List<?> page = buildPage(list);
        return page;
    }
}