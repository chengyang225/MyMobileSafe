package activity;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Process;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.testdemo.chanian.mymobilesafe.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import db.dao.VirusDao;

/**
 * Created by ChanIan on 16/5/12.
 */
public class KillVirusActivity extends Activity {
    private ImageView iv_rota;
    private ProgressBar pg_virus;
    private LinearLayout ll_virus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kill_virus);
        ll_virus = (LinearLayout) findViewById(R.id.ll_virus);
        pg_virus = (ProgressBar) findViewById(R.id.pg_virus);
        iv_rota = (ImageView) findViewById(R.id.iv_rota);

        //扫描动画
        RotateAnimation ra = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, .5f,
                Animation.RELATIVE_TO_SELF, .5f);
        ra.setDuration(2000);
        ra.setRepeatCount(Animation.INFINITE);
        iv_rota.startAnimation(ra);

        //扫描病毒
        scanVirus();
    }

    private void scanVirus() {
        new Thread() {
            public void run() {
                PackageManager pm = getPackageManager();
                //得到所有的包
                List<PackageInfo> infos = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES +
                        PackageManager.GET_SIGNATURES);
                try {
                    //设置进度条最大值
                    pg_virus.setMax(infos.size());
                    int process = 0;
                    for (PackageInfo info : infos) {
                        pg_virus.setProgress(process++);
                        String path = info.applicationInfo.sourceDir;//得到安装包路径
                        File file = new File(path);
                        MessageDigest digest=MessageDigest.getInstance("md5");
                        //将文件读进流里
                        FileInputStream fis=new FileInputStream(file);
                        byte[] buffer=new byte[1024];
                        int len=0;
                        while ((len=fis.read(buffer))!=-1){
                            digest.update(buffer,0,len);
                        }
                        byte[] bytes = digest.digest();
                        StringBuffer sb=new StringBuffer();
                        for (byte b :bytes){
                            String st = Integer.toHexString(b & 0xff);
                            if(st.length()==1) {
                                sb.append("0");
                            }
                            sb.append(st);
                        }
                        String md5 = sb.toString();
                        final String name = info.applicationInfo.loadLabel(pm).toString().trim();
                        final String ret = VirusDao.isVirus(md5);
                        //回到主线程设置扫描结果
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView tv=new TextView(getApplicationContext());
                                if(ret!=null) {
                                    tv.setText(name+"    发现病毒");
                                    tv.setTextColor(Color.RED);
                                    tv.setTextSize(20);
                                }else {
                                    tv.setTextSize(18);
                                    tv.setTextColor(Color.GREEN);
                                    tv.setText(name+"    未发现病毒");
                                }
                                ll_virus.addView(tv,0);
                            }
                        });
                        Thread.sleep(100);
                    }
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
