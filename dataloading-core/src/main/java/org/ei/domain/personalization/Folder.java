package org.ei.domain.personalization;

import java.io.UnsupportedEncodingException;

/**
 * Folder.java is a basictype that is related to the folder.It contains the FolderId and FolderName folderSize values
 */

public class Folder {

    // The folderId
    private String folderID;
    // The folderName
    private String folderName;
    // The Folder Size
    private int folderSize = 0;
    // The encodedfolderName
    private String encFolderName;
    // The maximum Folder Size
    private int maxfolderSize = 0;

    /**
     * constructor which takes folderId and FolderName
     * 
     * @throws UnsupportedEncodingException
     */
    public Folder(String strFolderId, String oFolderName) throws UnsupportedEncodingException {
        setFolderID(strFolderId);
        setFolderName(oFolderName);
        setEncFolderName(java.net.URLEncoder.encode(oFolderName, "UTF-8"));
    }

    /** constructor which takes folderId */
    /** no longer exists, made new Folder(String) ambigous */
    /*
     * public Folder(String strFolderId) { setFolderID(strFolderId);
     * 
     * }
     */
    /** constructor which takes FolderName */
    /** savedRecords.addFolder will assign folderId */
    public Folder(String oFolderName) {
        setFolderName(oFolderName);
    }

    /** method that sets the folderID */

    public void setFolderID(String strFolderId)

    {
        folderID = strFolderId;
    }

    /** method that sets the folderName */

    public void setFolderName(String oFolderName)

    {
        folderName = oFolderName;
    }

    /** method that sets the folderSize */
    protected void setFolderSize(int aFolderSize) {
        folderSize = aFolderSize;
    }

    /** returns folderID */
    public String getFolderID() {
        return folderID;
    }

    /** returns FolderName */
    public String getFolderName() {
        return folderName;
    }

    /** returns FolderSize */
    public int getFolderSize() {
        return folderSize;
    }

    /** returns toString */
    public String toString() {
        StringBuffer folderString = new StringBuffer();
        folderString.append("folder id :" + folderID);
        folderString.append("\n folderName:" + folderName);
        folderString.append("\n folderSize:" + folderSize);
        return folderString.toString();
    }

    /**
     * This method returns the xml formatted string of the object.
     * 
     * @return : String.
     */
    public String toXMLString() {
        StringBuffer xmlString = new StringBuffer("<FOLDER>");
        xmlString.append("<FOLDER-ID>").append(folderID).append("</FOLDER-ID>");
        xmlString.append("<FOLDER-NAME>");
        xmlString.append(folderName);
        xmlString.append("</FOLDER-NAME>");
        xmlString.append("<FOLDER-SIZE>");
        xmlString.append(folderSize);
        xmlString.append("</FOLDER-SIZE>");
        xmlString.append("</FOLDER>");
        return xmlString.toString();
    }

    public String getEncFolderName() {
        return encFolderName;
    }

    public void setEncFolderName(String encFolderName) {
        this.encFolderName = encFolderName;
    }

    public int getMaxfolderSize() {
        return maxfolderSize;
    }

    public void setMaxfolderSize(int maxfolderSize) {
        this.maxfolderSize = maxfolderSize;
    }

}
// end of Folder.java