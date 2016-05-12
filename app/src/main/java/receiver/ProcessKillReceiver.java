package receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import engine.ProcessInfoProvider;

/**
 * Created by ChanIan on 16/5/10.
 */
public class ProcessKillReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ProcessInfoProvider.killAllProcess(context);
    }
}
