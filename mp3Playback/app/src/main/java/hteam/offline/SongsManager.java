package hteam.offline;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Created by kiend on 4/28/2017.
 */

public class SongsManager {
    //SDcard PATH
//    final String MEDIA_PATH = new String("/storage/B1B6-121B/NhacCuaTui/");
    final String MEDIA_PATH = new String("/storage/71CF-5324/mp4");
//    final String MEDIA_PATH = new String("storage/emulated/legacy/Music");
    private ArrayList<SongModel> songsList = new ArrayList<SongModel>(); // mang luu danh sach bai hat co trong the nho
    private String TAG = getClass().getSimpleName();
    /**
     * lay toan bo file mp3 trong sdcard luu vao mang
     */
    public ArrayList<SongModel> getPlayList(){
        //tạo 1 thư mục lưu trong chương trình
        File home = new File(MEDIA_PATH);
        //kiển tra nếu tồn tại file nhạc trong "home" thì lấy ra tên và đường dẫn
        if (home.listFiles( new FileExtensionFilter()).length > 0){
            for (File file : home.listFiles( new FileExtensionFilter())){
                SongModel model = new SongModel();
                model.Path=file.getPath();
                model.Name=file.getName().substring(0,(file.getName().length()-4));
                //thêm vào danh sách bài hát
                songsList.add(model);
            }
        }
        return songsList;
    }

    /**
     * loc cac file co duoi .mp3 || .MP3 trong sdcard
     */
    class FileExtensionFilter implements FilenameFilter {

        @Override
        public boolean accept(File dir, String name) {
            return (name.endsWith(".mp3") || name.endsWith(".MP3"));
        }
    }
}
