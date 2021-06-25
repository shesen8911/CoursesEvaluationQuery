package com.example.homework3;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    public static final int RC_SIGN_IN = 1;
    Button Btn;
    TextView tvMain;
    LinearLayoutCompat layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Btn = findViewById(R.id.btn);
        tvMain = findViewById(R.id.mainTXT);
        layout = findViewById(R.id.mainXML);
        setColor();
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //已登入直接跳轉
        if (user != null) {
            //已驗證就跳轉查詢畫面
            if (user.isEmailVerified()) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
                finish();
            }else {
                //寄信
                Btn.setText("發送電子郵件");
                tvMain.setText(user.getDisplayName() + ", 發送郵件並進行登入");
            }
        }

        Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth = FirebaseAuth.getInstance();
                user = mAuth.getCurrentUser();

                if (user != null) {
                    //寄信
                    sendEmailVerification();
                    createNotificationChannel();
                    sendNotification();
                }

                List<AuthUI.IdpConfig> providers = Arrays.asList(
                        new AuthUI.IdpConfig.EmailBuilder().build());

                startActivityForResult(
                        AuthUI.getInstance()
                                .createSignInIntentBuilder()
                                .setAvailableProviders(providers).build(), RC_SIGN_IN);
            }
        });
    }

    //進行寄信
    private void sendEmailVerification() {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                    }
                });
    }

    //傳送通知
    private void sendNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.ic_baseline_school_24)
                .setContentTitle("課程評價查詢 － 已寄送電子郵件")
                .setContentText("請至您的電子郵件點選驗證連結後，再回此APP進行登入")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, builder.build());
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setColor() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.getBoolean("checkToGray", false)) {
            layout.setBackgroundColor(Color.parseColor("gray"));
            tvMain.setTextColor(Color.parseColor("white"));
        } else {
            layout.setBackgroundColor(Color.parseColor("white"));
            tvMain.setTextColor(Color.parseColor("black"));
        }
    }
}