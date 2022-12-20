package com.example.flashfastfood.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.flashfastfood.Data.Chat;
import com.example.flashfastfood.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>{

    public static final int MESSAGE_RIGHT = 1;
    public static final int MESSAGE_LEFT = 0;
    private Context context;
    private ArrayList<Chat> chatArrayList;

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
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Chat chat = chatArrayList.get(position);
        holder.message.setText(chat.getMessage());
    }

    @Override
    public int getItemCount() {
        return chatArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView message;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.itemMessage);
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
