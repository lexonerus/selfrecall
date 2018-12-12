package ru.lexone.selfrecall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import ru.lexone.selfrecall.MainActivity;

public class Reciever extends BroadcastReceiver {
        public String simId;

        @Override
        public void onReceive(Context context, Intent intent) {
            /*String callingSIM = "null0";
            Bundle bundle = intent.getExtras();
            callingSIM = String.valueOf(bundle.getInt("simId", -1));
            simId = callingSIM;*/
            simId = "Ресивер работает.";
        }
    }
