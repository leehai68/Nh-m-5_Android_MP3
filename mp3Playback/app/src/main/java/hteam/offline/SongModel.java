package hteam.offline;

import java.io.Serializable;

/**
 * Created by kiend on 4/28/2017.
 */

public class SongModel implements Serializable{
    public String Name;
    public String Path;

    public SongModel() {
    }

    public SongModel(String name, String path) {
        Name = name;
        Path = path;
    }
}
