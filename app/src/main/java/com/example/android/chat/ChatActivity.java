package com.example.android.chat;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    EditText messageEditText;
    Button send;
    TextView infoTextView;
    ListView chatListView;
    public static String IP=null;
    public static String NICK=null;
    ArrayList<String> listItem= new ArrayList<>();
    ArrayAdapter<String> adapter;
    MqttClient sampleClient= null;
    Handler myHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            listItem.add("["+msg.getData().getString("NICK")+"]" +msg.getData().get("MSG"));
            adapter.notifyDataSetChanged();
            chatListView.setSelection(listItem.size()-1);
            return true;
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        messageEditText=(EditText)findViewById(R.id.messageEditText);
        send=(Button)findViewById(R.id.sendButton);
        infoTextView=(TextView) findViewById(R.id.infoTextView);
        chatListView=(ListView)findViewById(R.id.chatListView);
        IP=getIntent().getStringExtra(MainActivity.IP);
        NICK=getIntent().getStringExtra(MainActivity.NICK);
        infoTextView.setText("Twój nick: "+NICK);
        adapter= new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,listItem);
        chatListView.setAdapter(adapter);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  Message msg= myHandler.obtainMessage();
                Bundle b = new Bundle();
                b.putString("NICK",NICK);
                b.putString("MSG",messageEditText.getText().toString());
                msg.setData(b);
                myHandler.sendMessage(msg);*/
             // MqttMessage http://lewandowskit.s.pwste.edu.pl/android/Android-chat.pdf
              sampleClient.getTopic("WTRTI/#").publish()
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                startMQTT();
            }
        }).start();
    }
    private void startMQTT(){
        String clientId;
        MemoryPersistence persistence = new MemoryPersistence();
        try{
            String broker ="top://"+IP+":1883";
            clientId=NICK;
            sampleClient = new MqttClient(broker,clientId,persistence);
            MqttConnectOptions connOpt= new MqttConnectOptions();
            connOpt.setCleanSession(true);
            System.out.println("coonenting to broker"+broker);
            sampleClient.subscribe("WTRTI/#");
            sampleClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable throwable) {

                }

                @Override
                public void messageArrived(String s, MqttMessage mqttMessage) throws Exception {
                    Message msg= myHandler.obtainMessage();
                    Bundle b = new Bundle();
                    b.putString("NICK",s);
                    b.putString("MSG",mqttMessage.toString());
                    msg.setData(b);
                    myHandler.sendMessage(msg);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

                }
            });
        }catch (Exception ex){

        }
    }
}
