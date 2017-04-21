package hteam.model;



/**
 * Created by Admin on 3/14/2017.
 */

public class PlaylistMp3
{
    String url_img_playlist;
    String url_playlist;
    String name_playlist;
    public PlaylistMp3(String s,String s1,String s2)
    {
        url_img_playlist=s;
        url_playlist=s1;
        name_playlist=s2;
    }

    public String getUrl_img_playlist() {
        return url_img_playlist;
    }

    public void setUrl_img_playlist(String url_img_playlist) {
        this.url_img_playlist = url_img_playlist;
    }

    public String getUrl_playlist() {
        return url_playlist;
    }

    public void setUrl_playlist(String url_playlist) {
        this.url_playlist = url_playlist;
    }

    public String getName_playlist() {
        return name_playlist;
    }

    public void setName_playlist(String name_playlist) {
        this.name_playlist = name_playlist;
    }
}
