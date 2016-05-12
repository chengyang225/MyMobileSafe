package activity;

import android.app.Activity;
import android.net.TrafficStats;
import android.os.Bundle;
import android.text.format.Formatter;

/**
 * Created by ChanIan on 16/5/10.
 */
public class TrafficManagerActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        long mobilerx = TrafficStats.getMobileRxBytes(); //获取手机(2g/3g/4g)下载的数据信息单位是byte
        long mobiletx = TrafficStats.getMobileTxBytes(); //获取手机 2g/3g/4g上传的数据信息

        long totalrx = TrafficStats.getTotalRxBytes();//获取全部端口下载的流量数据. 包括wifi和 2g/3g/4g的流量
        long totaltx = TrafficStats.getTotalTxBytes();//获取全部端口上传的流量数据. 包括wifi和 2g/3g/4g的流量

        long browserrx = TrafficStats.getUidRxBytes(10004);
        long browsertx =TrafficStats.getUidTxBytes(10004);

        System.out.println("手机上传:"+ Formatter.formatFileSize(this, mobiletx));
        System.out.println("手机下载:"+Formatter.formatFileSize(this, mobilerx));
        System.out.println("wifi上传:"+Formatter.formatFileSize(this, totaltx - mobiletx));
        System.out.println("wifi下载:"+Formatter.formatFileSize(this, totalrx- mobilerx));
        System.out.println("浏览器上传:"+Formatter.formatFileSize(this, browsertx));
        System.out.println("浏览器下载:"+Formatter.formatFileSize(this, browserrx));
    }
}
