package com.example.flashfastfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.flashfastfood.Adapter.ReviewViewHolder;
import com.example.flashfastfood.Data.Review;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import java.text.SimpleDateFormat;
import java.util.Calendar;


import de.hdodenhof.circleimageview.CircleImageView;

public class ReviewActivity extends AppCompatActivity {
    TextView foodNameReview;
    CircleImageView foodImgReview;

    ImageButton btnPostReview;
    EditText edtReview;

    String itemId, currentUserId, userName;

    Review review;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference reviewRef, itemsRef, userRef;
    FirebaseRecyclerOptions<Review> options;
    FirebaseRecyclerAdapter<Review, ReviewViewHolder> adapter;

    String saveCurrentDate, saveCurrentTime;

    RecyclerView rvReview;
    TextView goback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        goback = findViewById(R.id.backprevious);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        firebaseDatabase = FirebaseDatabase.getInstance("https://flashfastfood-81fee-default-rtdb.asia-southeast1.firebasedatabase.app");
        reviewRef = firebaseDatabase.getReference("Review");
        itemsRef = firebaseDatabase.getReference("Items");
        userRef = firebaseDatabase.getReference("Registered Users");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();

        //get current date time
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy");
        saveCurrentDate = simpleDateFormat.format(calendar.getTime());
        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("HH:mm a");
        saveCurrentTime = simpleTimeFormat.format(calendar.getTime());

        itemId = getIntent().getStringExtra("itemId");

        foodNameReview = findViewById(R.id.itemReviewName);
        foodImgReview = findViewById(R.id.itemFoodImgReview);

        edtReview = findViewById(R.id.edtReview);

        userRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userName = snapshot.child("FullName").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        rvReview = findViewById(R.id.rvReview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        rvReview.setLayoutManager(linearLayoutManager);

        btnPostReview = findViewById(R.id.btnPostReview);
        btnPostReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String mess = edtReview.getText().toString();
                if (!mess.equals("")){
                    review = new Review(mess,saveCurrentDate,saveCurrentTime,userName);
                    reviewRef.child(itemId).push().setValue(review);

                }else {
                    Toast.makeText(getApplicationContext(),"You have to write something first",Toast.LENGTH_SHORT).show();
                }
                edtReview.setText("");
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getReview();
        getItemReview();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.no_animation,R.anim.zoom_out);
    }

    private void getItemReview(){
        itemsRef.child(itemId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("itemName").getValue().toString();
                String img = snapshot.child("itemImg").getValue().toString();

                Picasso.get().load(img).into(foodImgReview);
                foodNameReview.setText(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getReview(){

        Query query = reviewRef.child(itemId);

        options =new FirebaseRecyclerOptions.Builder<Review>().setQuery(query,Review.class).build();

        adapter = new FirebaseRecyclerAdapter<Review, ReviewViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ReviewViewHolder holder, int position, @NonNull Review model) {
                String postKey = getRef(position).getKey();

                reviewRef.child(itemId).child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String textReview = snapshot.child("itemReviewText").getValue().toString();
                        String dateReview = snapshot.child("itemReviewDate").getValue().toString();
                        String timeReview = snapshot.child("itemReviewTime").getValue().toString();
                        String userReview = snapshot.child("itemReviewCurrentUser").getValue().toString();

                        holder.reviewText.setText(textReview);
                        holder.reviewTime.setText(timeReview);
                        holder.reviewDate.setText(dateReview);
                        holder.userNameReview.setText(userReview);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @NonNull
            @Override
            public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reviews, parent,false);
                ReviewViewHolder viewHolder = new ReviewViewHolder(view);
                return viewHolder;
            }
        };

        rvReview.setAdapter(adapter);
        adapter.startListening();
    }
}