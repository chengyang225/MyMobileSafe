package receiver;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsMessage;
import android.widget.Toast;

import com.testdemo.chanian.mymobilesafe.R;

import service.MyLocationService;
import utils.ToastUtils;

/**
 * Created by ChanIan on 16/5/3.
 */
public class SMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Object[] puds = (Object[]) intent.getExtras().get("pdus");
        ComponentName componentName = new ComponentName(context, MyReceiver.class);
        DevicePolicyManager dm = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        for (Object obj  : puds) {
            SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
            String body = sms.getMessageBody();//得到短信内容
            if("*alarm*".equals(body)) {
                MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
                player.setLooping(true);
                player.start();
                abortBroadcast();
            }
            if("*lockscreen*".equals(body)) {
                if(dm.isAdminActive(componentName)) {
                    dm.lockNow();
                }else {
                    Toast.makeText(context,"请在设置向导四开启服务", Toast.LENGTH_SHORT).show();
                }
                abortBroadcast();
            }
            if("*wipedata*".equals(body)) {
                if(dm.isAdminActive(componentName)) {
                    dm.wipeData(0);//清除所有数据
                }else {
                    Toast.makeText(context,"请在设置向导四开启服务", Toast.LENGTH_SHORT).show();
                }
            }
            if("*location*".equals(body)) {
                Intent intent1 = new Intent(context, MyLocationService.class);
                context.startService(intent1);
                abortBroadcast();
            }


        }

    }
}
