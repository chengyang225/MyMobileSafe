package receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

/**
 * Created by ChanIan on 16/5/3.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //1.取出当前绑定的手机串号
        TelephonyManager fm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String newSim = fm.getSimSerialNumber();

        //2.取出原来绑定的手机串号
        SharedPreferences sp=context.getSharedPreferences("config",Context.MODE_PRIVATE);
        String sim = sp.getString("sim", "");
        //3.判断是否一致
        if(newSim.equals(sim)) {
            //一致

        }else {
            //不一致.偷偷发短信
            SmsManager.getDefault().sendTextMessage(sp.getString("safeNum",null),null,"sim changer",null,null);
        }
    }
}
