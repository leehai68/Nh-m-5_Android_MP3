package hteam.offline;

/**
 * Created by kiend on 4/29/2017.
 */

public class Util {
    /**
     * chuyển thời gian về định dạng hh:mm:ss cho 1 bài hát
     * @param milliseconds
     * @return
     */
    public static String milliSecondsToTimer(long milliseconds){
        String finalTimerString = "";
        String secondsString = "";

        int h = (int) (milliseconds/(1000*60*60));
        int m = (int) (milliseconds%(1000*60*60))/(1000*60);
        int s = (int) ((milliseconds % (1000 * 60 * 60)) % (1000 * 60) / 1000);
        if(h > 0){
            finalTimerString = h + ":";
        }
        if (s < 10){
            secondsString = "0" + s;
        }
        else{
            secondsString = "" + s;
        }
        finalTimerString = finalTimerString + m + ":" + secondsString;
        return  finalTimerString;
    }

    /**
     * tính % bài hát đã hoàn thành hiển thị lên progressBar
     * @param currentDuration
     * @param totalDuration
     * @return
     */
    public static int getProgressPercentage(long currentDuration, long totalDuration){
        Double percentage = (double) 0;
        long currentSeconds =(int) (currentDuration /1000);
        long totalSeconds = (int) (totalDuration / 1000);
        percentage = (((double)currentSeconds) / totalSeconds)*100;
        return percentage.intValue();
    }

    /**
     *xử lý kéo chọn thời gian bài hát
     * @param progress
     * @param totalDuration
     * @return
     */
    public static int progressToTimer(int progress, int totalDuration){
        int currentDuration = 0;
        totalDuration =(int) (totalDuration/1000);
        currentDuration = (int) ((((double)progress) /100)*totalDuration);
        //trả về theo milliseconds
        return  currentDuration *1000;
    }
}
