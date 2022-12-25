package com.example.flashfastfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flashfastfood.Adapter.MessageAdapter;
import com.example.flashfastfood.Data.Chat;
import com.example.flashfastfood.Fragment.ProfileFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {

    String adId;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference userRef, chatRef, chatNotifiRef;
    String currentUserId;

    TextView adminNameMessage;

    TextView goback;

    EditText edtMessage;
    ImageButton btnSendMess;

    RecyclerView rvMessage;
    MessageAdapter messageAdapter;
    ArrayList<Chat> chatArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        goback = findViewById(R.id.backprevious);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance("https://flashfastfood-81fee-default-rtdb.asia-southeast1.firebasedatabase.app");
        userRef = firebaseDatabase.getReference("Registered Users");
        chatRef = firebaseDatabase.getReference("Chats");
        chatNotifiRef = firebaseDatabase.getReference("Chats Notification");

        adminNameMessage = findViewById(R.id.adminNameMessage);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();

        edtMessage = findViewById(R.id.edtMessage);
        btnSendMess = findViewById(R.id.btnSendMess);
        btnSendMess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mess = edtMessage.getText().toString();
                if (!mess.equals("")){
                    sendMessage(currentUserId,adId,mess);
                }else {
                    Toast.makeText(getApplicationContext(),"You have to write something first",Toast.LENGTH_SHORT).show();
                }
                edtMessage.setText("");
            }
        });

        userRef.child("admin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                adId = snapshot.child("id").getValue().toString();

                userRef.child(adId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        adminNameMessage.setText(snapshot.child("FullName").getValue().toString());
                        getMessage(currentUserId,adId);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });


        rvMessage = findViewById(R.id.rvMessage);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        linearLayoutManager.setStackFromEnd(true);
        rvMessage.setLayoutManager(linearLayoutManager);



    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.no_animation,R.anim.zoom_out);
    }


    private void sendMessage(String sender, String receiver, String message){
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);

        chatNotifiRef.child(currentUserId).push().setValue(hashMap);
        chatRef.push().setValue(hashMap);
    }

    private void getMessage(String currentUserId, String adId){
        chatArrayList = new ArrayList<>();
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatArrayList.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(currentUserId) && chat.getSender().equals(adId) ||
                            chat.getReceiver().equals(adId) && chat.getSender().equals(currentUserId)){
                        chatArrayList.add(chat);
                    }
                    messageAdapter = new MessageAdapter(MessageActivity.this,chatArrayList);
                    rvMessage.setAdapter(messageAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}