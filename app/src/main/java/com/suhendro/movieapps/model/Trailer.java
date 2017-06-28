package com.suhendro.movieapps.model;

/**
 * Created by Suhendro on 6/28/2017.
 */

public class Trailer {
    private String key;
    private String name;
    private String site;
    private String type;
    private Integer size;

    public Trailer() {}

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trailer)) return false;

        Trailer trailer = (Trailer) o;

        if (!getKey().equals(trailer.getKey())) return false;
        return getSite() != null ? getSite().equals(trailer.getSite()) : trailer.getSite() == null;

    }

    @Override
    public int hashCode() {
        int result = getKey().hashCode();
        result = 31 * result + (getSite() != null ? getSite().hashCode() : 0);
        return result;
    }
}
