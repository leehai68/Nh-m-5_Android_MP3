package hteam.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Admin on 3/5/2017.
 */

public class ZingMp3 implements Serializable
{
    private List<Data> data;


    public List<Data> getData() {
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }
}
