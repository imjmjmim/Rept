package com.example.rept;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class PointActivity extends AppCompatActivity {
    private TextView txt_point;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_point);

        txt_point = findViewById(R.id.txt_point);

        Intent intent = getIntent();
        String userID = intent.getStringExtra("userID");

        txt_point.setText(userID);
    }
}
