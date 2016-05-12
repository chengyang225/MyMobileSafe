package activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;

import com.testdemo.chanian.mymobilesafe.R;

import utils.IntentUtils;
import utils.SmsUtils;
import utils.ToastUtils;

/**
 * Created by ChanIan on 16/5/6.
 */
public class AToolAcyivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atool);
    }
    //归属地查询
    public void queryLocationClick(View view){
        IntentUtils.startActivity(this,QueryAddressLocationActivity.class);
    }
    //公共号码查询
    public void queryCommonsClick(View view){
        IntentUtils.startActivity(this,CommonsActivity.class);
    }
    //短信备份
    public void smsBackupClick(View view){
        //弹出进度条显示备份进度
        final ProgressDialog dialog=new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);//设置水平进度条
        dialog.setTitle("正在备份");
        dialog.show();
        //备份可能比较耗时,在子线程操作
        new Thread(){
            public void run(){
                boolean sms = SmsUtils.backupSms(AToolAcyivity.this, "smsback.xml", new SmsUtils.MyCallBack() {
                    @Override
                    public void beforeBackUp(int max) {//备份前
                        dialog.setMax(max);
                    }

                    @Override
                    public void afterBackUp(int progress) {//备份后
                        dialog.setProgress(progress);
                    }
                    
                });
                if(sms) {
                    ToastUtils.getToast(AToolAcyivity.this,"备份成功");
                }else {
                    ToastUtils.getToast(AToolAcyivity.this,"备份失败");
                }
                dialog.dismiss();
            }
        }.start();
    }
    //软件锁
    public void appLockClick(View view){
        IntentUtils.startActivity(this,AppLockActivity.class);
    }
}
