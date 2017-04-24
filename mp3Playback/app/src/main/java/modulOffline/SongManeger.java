package modulOffline;

import com.kiendn.kiendnoffline.SongModel;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;

/**
 * Created by Kiendn on 18/03/2017.
 */

public class SongManeger {
    //    khởi tạo đường dẫn tới thư mục lưu danh sách bài hát
    final String MEDIA_PATH = new String("/Card/NhacCuaTui/");

    private ArrayList<SongModel> songList = new ArrayList<SongModel>();

    /**
     * lấy toàn bộ danh sách bài hát
     */
    public ArrayList<SongModel> getPlayList(){
        //tạo 1 thư mục lưu trong chương trình
        File home = new File(MEDIA_PATH);

        //kiển tra nếu tồn tại file nhạc trong "home" thì lấy ra tên và đường dẫn
        if (home.listFiles( new FileExtensionFilter()).length > 0){
            for (File file : home.listFiles( new FileExtensionFilter())){
                SongModel model = new SongModel();
                model.path=file.getPath();
                model.title=file.getName().substring(0,(file.getName().length()-4));
                //thêm vào danh sách bài hát
                songList.add(model);
            }
        }
        return songList;
    }

    /**
     * lấy ra các file có đuôi *.mp3 | *.MP3 trong thẻ nhớ
     */
    class FileExtensionFilter implements FilenameFilter{

        @Override
        public boolean accept(File dir, String name) {
            return (name.endsWith(".mp3") || name.endsWith(".MP3"));
        }
    }
}
