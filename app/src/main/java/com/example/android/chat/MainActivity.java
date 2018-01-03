package com.example.android.chat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    public static String IP=null;
    public static String NICK=null;
    EditText ipEditText,nickEditText;
    Button send;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ipEditText= (EditText) findViewById(R.id.ipEditText);
        nickEditText= (EditText) findViewById(R.id.nickEditText);
        send= (Button)findViewById(R.id.startButton);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ChatActivity.class);
                intent.putExtra(IP,ipEditText.getText().toString());
                intent.putExtra(NICK,nickEditText.getText().toString());
                startActivity(intent);
            }
        });

    }
}
