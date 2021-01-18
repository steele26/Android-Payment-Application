package com.design.steelenaidoo.receivenfc;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.cardemulation.CardEmulation;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static TextView activityLog;
    private static final StringBuilder logCache = new StringBuilder();
    private static final DateFormat timeStampFmt = new SimpleDateFormat("HH:mm:ss.SSS  ", Locale.US);

    private static final String TAG = MainActivity.class.getSimpleName();


    private static void log(String tag, Object... messageFragments) {
        StringBuilder message = new StringBuilder();
        for(Object fragment : messageFragments) {
            message.append(fragment.toString());
        }
        String text = message.toString();

        logCache.append(timeStampFmt.format(new Date())).append(tag).append(" ").append(text).append('\n');

        // any logging before activityLog is initialized does not get
        // cleared and will be printed to the screen later
        if (activityLog != null) {
            activityLog.append(logCache);
            logCache.setLength(0);
        }

        //
        //  The logs may have credit card numbers in them, so don't send the output to the system
        //  log below unless you are testing.
        //
        //Log.d(tag, text);
    }


    private static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle data = msg.getData();
            log(data.getString("tag"), (Object[])data.getStringArray("messageFragments"));
            super.handleMessage(msg);
        }
    };


    /*
     *  Receives log messages from another thread like the PaymentService and passes
     *  them along to the thread for this activity class.
     */
    public static void sendLog(String tag, String ...messageFragments) {
        Message message = Message.obtain();
        Bundle data = new Bundle();
        data.putString("tag", tag);
        data.putStringArray("messageFragments", messageFragments);
        message.setData(data);
        handler.sendMessage(message);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }
        setContentView(R.layout.activity_main);
        activityLog = (TextView) findViewById(R.id.activity_log);

        CardEmulation cardEmulationManager = CardEmulation.getInstance(NfcAdapter.getDefaultAdapter(this));
        ComponentName paymentServiceComponent =
                new ComponentName(getApplicationContext(), PaymentService.class.getCanonicalName());

        if (!cardEmulationManager.isDefaultServiceForCategory(paymentServiceComponent, CardEmulation.CATEGORY_PAYMENT)) {
            Intent intent = new Intent(CardEmulation.ACTION_CHANGE_DEFAULT);
            intent.putExtra(CardEmulation.EXTRA_CATEGORY, CardEmulation.CATEGORY_PAYMENT);
            intent.putExtra(CardEmulation.EXTRA_SERVICE_COMPONENT, paymentServiceComponent);
            startActivityForResult(intent, 0);
            log(TAG, "onCreate: Requested Android to make SwipeYours the default payment app");
        } else {
            log(TAG, "onCreate: TapMe is the default NFC payment app");
        }
    }
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to go back?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("No", null)
                .show();
                }
    }


