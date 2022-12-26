package com.example.flashfastfood.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Fragment;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flashfastfood.Adapter.MessageAdapter;
import com.example.flashfastfood.Data.Chat;
import com.example.flashfastfood.Fragment.HomeFragment;
import com.example.flashfastfood.Fragment.ProfileFragment;
import com.example.flashfastfood.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;


public class MessageAdminActivity extends AppCompatActivity {

    String userId;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference userRef, chatRef, chatNotifiRef;
    String adminId;

    TextView userNameMessage, userEmail;
    TextView goback;

    EditText edtMessage;
    ImageButton btnSendMess;

    RecyclerView rvMessageAdmin;
    MessageAdapter messageAdapter;
    ArrayList<Chat> chatArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_admin);

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

        userNameMessage = findViewById(R.id.userNameMessage);
        userEmail = findViewById(R.id.userEmail);

        userId = getIntent().getStringExtra("userId");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        adminId = user.getUid();

        edtMessage = findViewById(R.id.edtMessage);
        btnSendMess = findViewById(R.id.btnSendMess);
        btnSendMess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mess = edtMessage.getText().toString();
                if (!mess.equals("")){
                    sendMessage(adminId,userId,mess);
                }else {
                    Toast.makeText(getApplicationContext(),"You have to write something first",Toast.LENGTH_SHORT).show();
                }
                edtMessage.setText("");
            }
        });

        rvMessageAdmin = findViewById(R.id.rvMessageAdmin);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        linearLayoutManager.setStackFromEnd(true);
        rvMessageAdmin.setLayoutManager(linearLayoutManager);

        userRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userNameMessage.setText(snapshot.child("FullName").getValue().toString());
                userEmail.setText(snapshot.child("Email").getValue().toString());
                getMessage(adminId,userId);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.no_animation,R.anim.zoom_out);
    }


    private void sendMessage(String sender, String receiver, String message){
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");
        String saveCurrentDate = simpleDateFormat.format(calendar.getTime());
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm:ss");
        String saveCurrentTime = simpleTimeFormat.format(calendar.getTime());

        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);

        chatRef.child(saveCurrentDate+" "+saveCurrentTime).setValue(hashMap);
        chatNotifiRef.child(adminId).child(userId).child(saveCurrentDate+" "+saveCurrentTime).setValue(hashMap);
    }

    private void getMessage(String adminId, String userId){
        chatArrayList = new ArrayList<>();
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatArrayList.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getReceiver().equals(adminId) && chat.getSender().equals(userId) ||
                            chat.getReceiver().equals(userId) && chat.getSender().equals(adminId)){
                        chatArrayList.add(chat);
                    }
                    messageAdapter = new MessageAdapter(MessageAdminActivity.this,chatArrayList);
                    rvMessageAdmin.setAdapter(messageAdapter);
                }
                messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}