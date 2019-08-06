package sg.edu.rp.c346.smsapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button btn, btn2;
    EditText et1, et2;
    BroadcastReceiver br;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.sendBtn);
        btn2 = findViewById(R.id.switchIntentBtn);
        et1 = findViewById(R.id.toText);
        et2 = findViewById(R.id.contentText);

        checkPermission();

        br = new MessageReceiver();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] contacts = et1.getText().toString().trim().split(",");

                try {
                    for (int i = 0; i < contacts.length; i++) {
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(contacts[i], null, et2.getText().toString().trim(), null, null);
                    }
                    Toast.makeText(MainActivity.this, "Message sent", Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Log.e("MAINACTIVITY", e.getMessage());
                    Log.e("MAINACTIVITY INPUT", et1.getText().toString() + " #|# " + et2.getText().toString());
                    Toast.makeText(MainActivity.this, "An error has occurs during sending", Toast.LENGTH_LONG).show();
                }
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse("smsto:" + et1.getText().toString())); // This ensures only SMS apps respond
                intent.putExtra("sms_body", et2.getText().toString());
                startActivity(intent);
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        this.registerReceiver(br, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(br);
    }

    private void checkPermission() {
        int permissionSendSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);
        int permissionRecvSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS);
        if (permissionSendSMS != PackageManager.PERMISSION_GRANTED &&
                permissionRecvSMS != PackageManager.PERMISSION_GRANTED) {
            String[] permissionNeeded = new String[]{Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS};
            ActivityCompat.requestPermissions(this, permissionNeeded, 1);
        }
    }
}
