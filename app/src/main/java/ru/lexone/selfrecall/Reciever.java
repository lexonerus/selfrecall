package ru.lexone.selfrecall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import ru.lexone.selfrecall.MainActivity;

import static android.content.ContentValues.TAG;
import static java.lang.Thread.sleep;

public class Reciever extends BroadcastReceiver {
    public String getextra;

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context,"HELLO FROM RECIEVER",Toast.LENGTH_SHORT).show();
        isSimAvailable(context);
    }

    public boolean isSimAvailable(Context context) {
        SubscriptionManager sManager = (SubscriptionManager) context.getSystemService(Context.TELEPHONY_SUBSCRIPTION_SERVICE);
        SubscriptionInfo infoSim1 = sManager.getActiveSubscriptionInfoForSimSlotIndex(0);
        SubscriptionInfo infoSim2 = sManager.getActiveSubscriptionInfoForSimSlotIndex(1);
        if (infoSim1 != null || infoSim2 != null) {
            return true;
        }
        return false;
    }


    }
