package service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.testdemo.chanian.mymobilesafe.R;

import db.dao.NumberLocationDao;

/**
 * Created by ChanIan on 16/5/6.
 */
//显示通话归属地服务
public class ShowAddressService extends Service {

    private TelephonyManager tm;
    private MyPhoneListener mListener;
    private MyPhoneReceiver mReceiver;
    private int[] colors={R.mipmap.call_locate_white9,R.mipmap.call_locate_orange9,
            R.mipmap.call_locate_blue9,R.mipmap.call_locate_gray9,R.mipmap.call_locate_green9};
    private final WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
    private WindowManager mWm;
    private TextView mTv_address;
    private View mView;
    private SharedPreferences mSp;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {//服务开启
        super.onCreate();
//        Log.v("ian", "来电显示开启");
        mSp = getSharedPreferences("config", MODE_PRIVATE);
        //监听来电
        tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mListener = new MyPhoneListener();
        tm.listen(mListener, PhoneStateListener.LISTEN_CALL_STATE);
        //注册服务
        mReceiver = new MyPhoneReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(mReceiver, filter);

    }

    @Override
    public void onDestroy() {//服务关闭
        super.onDestroy();
        //注销监听
//        Log.v("ian", "来电显示关闭");
        tm.listen(mListener, PhoneStateListener.LISTEN_NONE);
        mListener = null;
        //注销服务
        unregisterReceiver(mReceiver);
        mReceiver = null;
    }

    class MyPhoneListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE://空闲状态
                    if (mView != null) {
                        mWm.removeViewImmediate(mView);//挂断时移除土司
                        mView = null;
                    }

                    break;

                case TelephonyManager.CALL_STATE_OFFHOOK://挂断状态
                    break;

                case TelephonyManager.CALL_STATE_RINGING://响铃状态
                    String address = NumberLocationDao.getAddress(incomingNumber);
                    showMyToast(address);
                    break;

            }
        }
    }

    //自定义土司
    private void showMyToast(String address) {
        mWm = (WindowManager) getSystemService(WINDOW_SERVICE);
        final WindowManager.LayoutParams params = mParams;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.address_toast, null);
        mTv_address = (TextView) mView.findViewById(R.id.tv_address);
        mTv_address.setText(address);
        mView.setBackgroundResource(colors[mSp.getInt("which",0)]);
        mWm.addView(mView, params);
        //回显上次位置
        mSp=getSharedPreferences("config",MODE_PRIVATE);
        params.x= (int) mSp.getFloat("lastx",0);
        params.y= (int) mSp.getFloat("lasty",0);
        //给控件添加触摸移动事件
        mView.setOnTouchListener(new View.OnTouchListener() {

            private float mStartY;
            private float mStartX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case  MotionEvent.ACTION_DOWN://按下
                        //起始位置
                        mStartX = event.getRawX();
                        mStartY = event.getRawY();
                        break;
                    case  MotionEvent.ACTION_MOVE://移动
                        //移动后位置
                        float endX=event.getRawX();
                        float endY=event.getRawY();
                        //偏移值
                        float dx=endX-mStartX;
                        float dy=endY-mStartY;
                        //移动控件
                        params.x+=dx;
                        params.y+=dy;
                        //更新位置
                        mWm.updateViewLayout(mView,params);
                        //重新定义初始位置
                        mStartX = event.getRawX();
                        mStartY = event.getRawY();
                        break;
                    case  MotionEvent.ACTION_UP://抬起
                        //将最后位置储存起来
                        float x=event.getRawX();
                        float y=event.getRawY();
                        SharedPreferences.Editor edit = mSp.edit();
                        edit.putFloat("lastx",x);
                        edit.putFloat("lasty",y);
                        edit.commit();
                        break;
                }
                return true;//开启自动控制
            }
        });
    }

    //监听去电
    class MyPhoneReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String data = getResultData();//得到外拨的电话号码
            String address = NumberLocationDao.getAddress(data);
            showMyToast(address);
        }
    }
}
