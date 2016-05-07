package service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.android.internal.telephony.ITelephony;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import db.dao.BlackNumberDao;

/**
 * Created by ChanIan on 16/5/5.
 */
//监听电话服务
public class CallPhoneBlackServicve extends Service {

    private SmsReceiver mReceiver;
    private BlackNumberDao mDao;
    private TelephonyManager mManager;
    private PhoneListener mListener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
//        Log.v("ian", "拦截服务开启了");
        mDao=new BlackNumberDao(getApplicationContext());
        mManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mListener = new PhoneListener();
        //监听电话状态
        mManager.listen(mListener,PhoneStateListener.LISTEN_CALL_STATE);
        mReceiver=new SmsReceiver();
        //准备好filter
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.setPriority(Integer.MAX_VALUE);
        //注册服务
        registerReceiver(mReceiver,filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        Log.v("ian", "拦截服务关闭了");
        //服务停止时候不需要监听
        mManager.listen(mListener,PhoneStateListener.LISTEN_NONE);
        mListener=null;
        //注销服务
        unregisterReceiver(mReceiver);
        mReceiver=null;
    }
    //动态监听短信服务
    class SmsReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] pdus = (Object[]) intent.getExtras().get("pdus");
            for (Object obj :pdus){
                SmsMessage sms = SmsMessage.createFromPdu((byte[]) obj);
                String address = sms.getOriginatingAddress();
                String mode = mDao.query(address);
                switch (mode) {
                    case "1":
                    Log.v("ian", "电话拦截");
                    break;
                    case "2":
                    Log.v("ian", "短信拦截");
                    break;
                    case "3":
                    Log.v("ian", "全部拦截");
                    break;

                    default:
                        break;
                }
                abortBroadcast();
            }
        }
    }
    class PhoneListener extends PhoneStateListener{
        @Override
        public void onCallStateChanged(int state, final String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE://空闲状态
                
                break;

                case TelephonyManager.CALL_STATE_OFFHOOK://挂断状态

                    break;

                case TelephonyManager.CALL_STATE_RINGING://接听状态
                    String mode = mDao.query(incomingNumber);
                    if("1".equals(mode)||"3".equals(mode)) {

                        endCall();
                        //使用内容观察者删除所拦截号码
                        Uri uri = Uri.parse("content://call_log/calls");//准备uri
                        getContentResolver().registerContentObserver(uri, true, new ContentObserver(new Handler()) {
                            @Override
                            public void onChange(boolean selfChange) {//删除号码记录
                                super.onChange(selfChange);
                                delete(incomingNumber);
                            }
                        });
                    }
                    break;
                default:
                    break;
            }
        }
    }

    private void delete(String incomingNumber) {
        //准备uri
        Uri uri = Uri.parse("content://call_log/calls");
        //获取解析者
        ContentResolver resolver = getContentResolver();
        //删除数据
        resolver.delete(uri,"number=?",new String[]{incomingNumber});
    }

    //结束通话
        private void endCall() {
            try {
                Class<?> clazz = CallPhoneBlackServicve.class.getClassLoader().loadClass("android.os.ServiceManager");
                Method method = clazz.getDeclaredMethod("getService", String.class);
                IBinder b= (IBinder) method.invoke(null, TELEPHONY_SERVICE);
                ITelephony telephony = ITelephony.Stub.asInterface(b);
                telephony.endCall();

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
}
