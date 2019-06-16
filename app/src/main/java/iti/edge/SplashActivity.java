package iti.edge;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class SplashActivity extends AppCompatActivity {

    private ImageView imageView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
        imageView = findViewById(R.id.imageView);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        //Wait for 3 seconds and start Activity Main
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SplashActivity.this.startActivity(new Intent(SplashActivity.this,LoginActivity.class));
                SplashActivity.this.finish();
            }
        },3000); // 3000 = 3seconds

    }
}
