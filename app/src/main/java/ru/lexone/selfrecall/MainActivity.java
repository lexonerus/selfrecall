package ru.lexone.selfrecall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telecom.TelecomManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import ru.lexone.selfrecall.Reciever;


import java.lang.reflect.Method;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity {

    public String simN = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        //обязательная инициализация TelephonyManager
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        telephonyManager.listen(stateListener, PhoneStateListener.LISTEN_CALL_STATE);

        setSupportText();

        Reciever detectSim = new Reciever();
        simN = detectSim.simId;





    }

    private void setSupportText() {
        PackageManager pm = getPackageManager();
        boolean isTelephonySupported = pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
        boolean isGSMSupported = pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_GSM);

        //инициализируем графический компонент
        TextView telSup = findViewById(R.id.tel_support);
        TextView gsmSup = findViewById(R.id.gsm_support);
        //задаем текст при условии
        if (isGSMSupported == true) {
            gsmSup.setText("На Вашем устройстве присутствует GSM модем.");
        } else {
            gsmSup.setText("К сожалению, Ваше устройство не имеет GSM модема.");
        }
          if (isTelephonySupported == true) {
            telSup.setText("Функция телефона также доступна.");
        } else {
            telSup.setText("Функция телефона недоступна.");
          }
    }


    //переопределяем метод onCallStateListener, чтобы получать уведомления об изменении состояния телефонного вызова
    PhoneStateListener stateListener = new PhoneStateListener() {
        public void onCallStateChanged(int state, String incomingNumber) {
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    showToast("Call state: idle");
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    showToast("Call state: offhook");
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    showToast("call state: ringing" + incomingNumber);


                    // Поступил звонок с номера incomingNumber
                    TextView incNum = findViewById(R.id.inc_num);
                    incNum.setText(incomingNumber);

                    TextView simid = findViewById(R.id.sim_id);
                    simid.setText(simN);


                    //doMagicWork(incomingNumber);

                    if (incomingNumber.equalsIgnoreCase("+74985201570")) {
                        endCall();



                        showToast("Номер сим карты: " + simN);

                        try {
                            sleep(5500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        callGates();
                    }

            }
        }

    };


    //Функция сброса вызова на android 8.0

    public static void endCall()
    {
        try {
            //String serviceManagerName = "android.os.IServiceManager";
            String serviceManagerName = "android.os.ServiceManager";
            String serviceManagerNativeName = "android.os.ServiceManagerNative";
            String telephonyName = "com.android.internal.telephony.ITelephony";

            Class telephonyClass;
            Class telephonyStubClass;
            Class serviceManagerClass;
            Class serviceManagerNativeClass;

            Method telephonyEndCall;

            Object telephonyObject;
            Object serviceManagerObject;

            telephonyClass = Class.forName(telephonyName);
            telephonyStubClass = telephonyClass.getClasses()[0];
            serviceManagerClass = Class.forName(serviceManagerName);
            serviceManagerNativeClass = Class.forName(serviceManagerNativeName);

            Method getService = serviceManagerClass.getMethod("getService", String.class);

            Method tempInterfaceMethod = serviceManagerNativeClass.getMethod("asInterface", IBinder.class);

            Binder tmpBinder = new Binder();
            tmpBinder.attachInterface(null, "fake");

            serviceManagerObject = tempInterfaceMethod.invoke(null, tmpBinder);
            IBinder retbinder = (IBinder) getService.invoke(serviceManagerObject, "phone");
            Method serviceMethod = telephonyStubClass.getMethod("asInterface", IBinder.class);

            telephonyObject = serviceMethod.invoke(null, retbinder);
            telephonyEndCall = telephonyClass.getMethod("endCall");
            telephonyEndCall.invoke(telephonyObject);
            Log.v("VoiceCall", "Call End Complete.");
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("VoiceCall", "FATAL ERROR: could not connect to telephony subsystem");
            Log.e("VoiceCall", "Exception object: " + e);
        }
    }


    private void callGates() {
        String phone = "+79263693322";
        Intent callIntent = new Intent(Intent.ACTION_CALL)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        callIntent.setData(Uri.parse("tel:" + phone));
        this.startActivity(callIntent);
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    //Создание всплывающего сообщения с информацией
    private void doMagicWork(String incomingNumber) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Важное сообщение!")
                .setMessage("Входящий вызов отработан. Вот номер звонящего: " + incomingNumber)
                .setCancelable(false)
                .setNegativeButton("Ок, спасибо за инфу",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }





}
