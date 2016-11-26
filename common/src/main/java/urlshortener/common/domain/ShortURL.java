package urlshortener.common.domain;

import java.net.URI;
import java.sql.Date;
import java.sql.Timestamp;

public class ShortURL implements Comparable{

    private String hash;
    private String target;
    private URI uri;
    private String sponsor;
    private Date created;
    private String owner;
    private Integer mode;
    private Boolean safe;
    private String ip;
    private String country;
    private Integer timePublicity;
    private String urlPublicity;
    private boolean active;
    private int update_status;        // 0 = pending -- 1=updating
    private Timestamp last_change;
    private Timestamp last_time_up;

    public ShortURL(String hash, String target, URI uri, String sponsor,
                    Date created, String owner, Integer mode, Boolean safe, String ip,
                    String country, Integer timePublicity, String urlPublicity, Timestamp last_change, boolean active, int update_status) {
        this.hash = hash;
        this.target = target;
        this.uri = uri;
        this.sponsor = sponsor;
        this.created = created;
        this.owner = owner;
        this.mode = mode;
        this.safe = safe;
        this.ip = ip;
        this.country = country;
        this.timePublicity = timePublicity;
        this.urlPublicity = urlPublicity;
        this.last_change = last_change;
        this.active = active;
        this.update_status = update_status;
    }

    public ShortURL(String hash, String target, URI uri, String sponsor,
                    Date created, String owner, Integer mode, Boolean safe, String ip,
                    String country, Integer timePublicity, String urlPublicity, boolean active) {
        this.hash = hash;
        this.target = target;
        this.uri = uri;
        this.sponsor = sponsor;
        this.created = created;
        this.owner = owner;
        this.mode = mode;
        this.safe = safe;
        this.ip = ip;
        this.country = country;
        this.timePublicity = timePublicity;
        this.urlPublicity = urlPublicity;
        this.active = active;
    }

    public ShortURL() {
    }

    public String getHash() {
        return hash;
    }

    public String getTarget() {
        return target;
    }

    public URI getUri() {
        return uri;
    }

    public Date getCreated() {
        return created;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Integer getMode() {
        return mode;
    }

    public String getSponsor() {
        return sponsor;
    }

    public Boolean getSafe() {
        return safe;
    }

    public String getIP() {
        return ip;
    }

    public String getCountry() {
        return country;
    }

    public Integer getTimePublicity() {
        return timePublicity;
    }

    public String getUrlPublicity() {
        return urlPublicity;
    }

    public Timestamp getLastChange() {
        return last_change;
    }

    public boolean getActive() {
        return active;
    }

    public int getUpdate_status() {return update_status;}

    public Timestamp getLast_time_up() {return last_time_up;}

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setLastChange(Timestamp last_change) {
        this.last_change = last_change;
    }

    public void setUpdate_status(int update_status) {
        this.update_status = update_status;
    }

    public void setLast_time_up(Timestamp last_time_up){this.last_time_up = last_time_up;}

    public int compareTo(Object o) {
        ShortURL nu = (ShortURL) o;
        if (nu.update_status == this.update_status) {
            return 1;
        } else if (nu.update_status < this.update_status) {
            return -1;
        } else {
            return 1;
        }

    }
}
