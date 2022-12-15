package com.example.flashfastfood.Fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.flashfastfood.Adapter.FavoriteViewHolder;
import com.example.flashfastfood.Data.Favorite;
import com.example.flashfastfood.ItemClickListener;
import com.example.flashfastfood.ItemDetailActivity;
import com.example.flashfastfood.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class FavoriteFragment extends Fragment {


    RecyclerView rvFavorite;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference favoriteRef;
    Boolean favoriteChecker = false;
    FirebaseRecyclerAdapter<Favorite, FavoriteViewHolder> adapter;
    String currentUserId;
    ArrayList<String> arrayList = null;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FavoriteFragment() {
        // Required empty public constructor
    }

    public static FavoriteFragment newInstance(String param1, String param2) {
        FavoriteFragment fragment = new FavoriteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvFavorite = view.findViewById(R.id.rvFavorite);
        rvFavorite.setHasFixedSize(true);
        rvFavorite.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();

        firebaseDatabase = FirebaseDatabase.getInstance("https://flashfastfood-81fee-default-rtdb.asia-southeast1.firebasedatabase.app");
        favoriteRef = firebaseDatabase.getReference("Favorite");
    }

    @Override
    public void onStart() {
        super.onStart();
        getDataFavorite();

    }

    private void getDataFavorite() {
        Query query = favoriteRef.child(currentUserId);

        FirebaseRecyclerOptions<Favorite> options = new FirebaseRecyclerOptions.Builder<Favorite>().setQuery(query,Favorite.class).build();

        adapter = new FirebaseRecyclerAdapter<Favorite, FavoriteViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position, @NonNull Favorite model) {
                String postKey = getRef(position).getKey();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String currentUserId = user.getUid();

                favoriteRef.child(currentUserId).child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String Name = snapshot.child("favoriteName").getValue().toString();
                        String Price = snapshot.child("favoritePrice").getValue().toString();
                        String Rating = snapshot.child("favoriteRating").getValue().toString();
                        String Image = snapshot.child("favoriteImage").getValue().toString();

                        holder.FavoriteName.setText(Name);
                        holder.FavoritePrice.setText(Price);
                        holder.FavoriteRating.setText(Rating);
                        Picasso.get().load(Image).into(holder.FavoriteImg);

                        holder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                Intent intent = new Intent(getContext(), ItemDetailActivity.class);
                                intent.putExtra("itemId", adapter.getRef(position).getKey());
                                startActivity(intent);
                                getActivity().overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                            }
                        });

                        holder.favoriteCheck(postKey);
                        holder.sparkButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                holder.sparkButton.playAnimation();
                                if (holder.isInMyFavorite){
                                    favoriteRef.child(currentUserId).child(postKey).removeValue()
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    String text = "Remove"+" "+Name+" "+"from favorite list";
                                                    Spannable centeredText = new SpannableString(text);
                                                    centeredText.setSpan(
                                                            new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                                                            0, text.length() - 1,
                                                            Spannable.SPAN_INCLUSIVE_INCLUSIVE
                                                    );

                                                    Toast.makeText(getContext(), centeredText, Toast.LENGTH_SHORT).show();
//                                            Toast.makeText(getContext(), "Removed from favorite list", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        });
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
            @NonNull
            @Override
            public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent,false);
                FavoriteViewHolder viewHolder = new FavoriteViewHolder(view);
                return viewHolder;
            }
        };
        rvFavorite.setAdapter(adapter);
        adapter.startListening();
    }
}