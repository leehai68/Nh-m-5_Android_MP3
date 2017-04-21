package hteam.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Admin on 3/5/2017.
 */

public class Data implements Serializable{
    private String name;
    private String artist;
    private String cover;
    private List<String> source_list;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }


    public List<String> getSource_list() {
        return source_list;
    }

    public void setSource_list(List<String> source_list) {
        this.source_list = source_list;
    }
}
