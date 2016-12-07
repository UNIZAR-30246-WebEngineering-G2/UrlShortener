package urlshortener.blackgoku.domain;

public class PlusObject {

    private String user;
    private String creationDate;
    private String targetUrl;

    public PlusObject(String user, String creationDate, String targetUrl, String ownerIP, int numClicks, int uniqueVisitors) {
        this.user = user;
        this.creationDate = creationDate;
        this.targetUrl = targetUrl;
        this.ownerIP = ownerIP;
        this.numClicks = numClicks;
        this.uniqueVisitors = uniqueVisitors;
    }

    public String getUser() {

        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    public String getOwnerIP() {
        return ownerIP;
    }

    public void setOwnerIP(String ownerIP) {
        this.ownerIP = ownerIP;
    }

    public int getNumClicks() {
        return numClicks;
    }

    public void setNumClicks(int numClicks) {
        this.numClicks = numClicks;
    }

    public int getUniqueVisitors() {
        return uniqueVisitors;
    }

    public void setUniqueVisitors(int uniqueVisitors) {
        this.uniqueVisitors = uniqueVisitors;
    }

    private String ownerIP;
    private int numClicks, uniqueVisitors;

}
