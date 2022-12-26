package com.example.flashfastfood.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashfastfood.Data.Chat;
import com.example.flashfastfood.ItemsActivity;
import com.example.flashfastfood.MessageActivity;
import com.example.flashfastfood.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{

    public static final int MESSAGE_RIGHT = 1;
    public static final int MESSAGE_LEFT = 0;
    private Context context;
    private ArrayList<Chat> chatArrayList;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference chatRef, chatNotifiRef;
    String currentUserId;

    FirebaseUser user;

    public MessageAdapter(Context context,ArrayList<Chat> chatArrayList){
        this.context = context;
        this.chatArrayList = chatArrayList;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MESSAGE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_right,parent,false);
            return new MessageAdapter.ViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_chat_left,parent,false);
            return new MessageAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Chat chat = chatArrayList.get(position);
        holder.message.setText(chat.getMessage());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();
        firebaseDatabase = FirebaseDatabase.getInstance("https://flashfastfood-81fee-default-rtdb.asia-southeast1.firebasedatabase.app");
        chatRef = firebaseDatabase.getReference("Chats");
        chatRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    if (dataSnapshot.child("isDelete").getValue().equals(true)){
                        holder.message.setBackgroundResource(R.drawable.bg_rounded_border);
                        holder.message.setTextColor(Color.parseColor("#E25822"));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.chatView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete Message");
                builder.setMessage("Are You Sure To Delete This Messgae");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMsg(position);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.create().show();

                return false;
            }
        });
    }

    private void deleteMsg(int position) {
        firebaseDatabase = FirebaseDatabase.getInstance("https://flashfastfood-81fee-default-rtdb.asia-southeast1.firebasedatabase.app");
        chatRef = firebaseDatabase.getReference("Chats");
        chatNotifiRef = firebaseDatabase.getReference("Chats Notification");
        String dateTimeSend = chatArrayList.get(position).getDateTimeSend();

        Query query = chatRef.orderByChild("dateTimeSend").equalTo(dateTimeSend);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.child("message").getValue().equals("This Message Was Deleted")){
                        if (dataSnapshot.child("sender").getValue().equals(currentUserId)) {
                            dataSnapshot.getRef().removeValue();
                            chatNotifiRef.child(currentUserId).child(dateTimeSend).removeValue();
                        } else {
                            Toast.makeText(context, "You can only delete you message!", Toast.LENGTH_LONG).show();
                        }
                    }else {
                        if (dataSnapshot.child("sender").getValue().equals(currentUserId)) {
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("message", "This Message Was Deleted");
                            hashMap.put("isDelete", true);
                            dataSnapshot.getRef().updateChildren(hashMap);
                        } else {
                            Toast.makeText(context, "You can only delete you message!", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return chatArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView message;
        CardView chatView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.itemMessage);
            chatView = itemView.findViewById(R.id.chatView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (chatArrayList.get(position).getSender().equals(user.getUid())){
            return MESSAGE_RIGHT;
        }else {
            return MESSAGE_LEFT;
        }
    }
}
