package com.example.flashfastfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.andremion.counterfab.CounterFab;
import com.example.flashfastfood.Adapter.ItemsViewHolder;
import com.example.flashfastfood.Data.Items;
import com.example.flashfastfood.Fragment.BottomSheetCartFragment;
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

public class SearchActivity extends AppCompatActivity {

    TextView goback, appname1,appname2, appname3;
    String currentUserId;

    SearchView searchView;
    RecyclerView rvSearch;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference cartRef,itemsRef,cateRef;
    Toolbar toolbar;
    FirebaseRecyclerOptions<Items> options;
    FirebaseRecyclerAdapter<Items, ItemsViewHolder> adapter;
    ArrayList<String> arrayList = null;

    LottieAnimationView searchWaiting;
    CounterFab btnCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        appname1 = findViewById(R.id.appname1);
        appname2 = findViewById(R.id.appname2);
        appname3 = findViewById(R.id.appname3);

        firebaseDatabase = FirebaseDatabase.getInstance("https://flashfastfood-81fee-default-rtdb.asia-southeast1.firebasedatabase.app");
        itemsRef = firebaseDatabase.getReference("Items");
        cateRef = firebaseDatabase.getReference("Categories");
        cartRef = firebaseDatabase.getReference("Shopping Cart");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = user.getUid();

        toolbar = findViewById(R.id.toolbarSearch);
        goback = toolbar.findViewById(R.id.backprevious);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        rvSearch = findViewById(R.id.rvSearch);
        rvSearch.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));

        searchWaiting = findViewById(R.id.searchWaiting);

        searchView = toolbar.findViewById(R.id.searchView);
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchWaiting.setVisibility(View.INVISIBLE);
                rvSearch.setVisibility(View.VISIBLE);
                searchView.setMinimumWidth(300);
                loadDataSearch("");
                appname1.setVisibility(View.GONE);
                appname2.setVisibility(View.GONE);
                appname3.setVisibility(View.GONE);
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                searchWaiting.setVisibility(View.VISIBLE);
                rvSearch.setVisibility(View.INVISIBLE);
                searchView.setMinimumWidth(50);
                appname1.setVisibility(View.VISIBLE);
                appname2.setVisibility(View.VISIBLE);
                appname3.setVisibility(View.VISIBLE);
                return false;
            }
        });

        btnCart = findViewById(R.id.itemFabCart);
        btnCart.setVisibility(View.GONE);
        getCartQuantity();

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("callbackkey", "1");
                BottomSheetCartFragment bottomSheetFragment = new BottomSheetCartFragment();
                bottomSheetFragment.setArguments(bundle);
                bottomSheetFragment.show(getSupportFragmentManager(),bottomSheetFragment.getTag());
            }
        });
    }

    public void getCartQuantity(){
        cartRef.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    arrayList.add(dataSnapshot.getKey());
                }
                String countItemInCart= Integer.toString(arrayList.size());
                int itemInCart = Integer.parseInt(countItemInCart);
                if (itemInCart==0){
                    btnCart.setVisibility(View.GONE);
                }
                else {
                    btnCart.setVisibility(View.VISIBLE);
                    btnCart.setCount(itemInCart);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
    }

    @Override
    protected void onStart() {
        super.onStart();
        getCartQuantity();
        arrayList = new ArrayList<String>();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadDataSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadDataSearch(newText);
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getCartQuantity();
        arrayList = new ArrayList<String>();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadDataSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadDataSearch(newText);
                return false;
            }
        });
    }

    private void loadDataSearch(String searchText) {
        Query query;
        if(searchText==""){
            query = itemsRef.orderByChild("itemName");
        }
        else {
            query = itemsRef.orderByChild("itemName").startAt(searchText).endAt(searchText+"\uf8ff");
        }


        options = new FirebaseRecyclerOptions.Builder<Items>().setQuery(query,Items.class).build();
        adapter = new FirebaseRecyclerAdapter<Items, ItemsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ItemsViewHolder holder, int position, @NonNull Items model) {
                String postKey = adapter.getRef(position).getKey();

                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String currentUserId = user.getUid();

                if (arrayList.contains(postKey)){
                    holder.itemView.setVisibility(View.GONE);
                    holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
                }

                itemsRef.child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String name = snapshot.child("itemName").getValue().toString();
                        String rating = snapshot.child("itemRating").getValue().toString();
                        String price = snapshot.child("itemPrice").getValue().toString();
                        String img = snapshot.child("itemImg").getValue().toString();
                        String cateId = snapshot.child("cateId").getValue().toString();

                        holder.itemname.setText(name);
                        holder.itemprice.setText(price);
                        holder.itemrating.setText(rating);
                        Picasso.get().load(img).into(holder.itemimg);

                        cateRef.child(cateId).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                holder.itemcatename.setText(snapshot.child("FoodCateName").getValue().toString());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        holder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                Intent intent = new Intent(SearchActivity.this, ItemDetailActivity.class);
                                intent.putExtra("itemId", adapter.getRef(position).getKey());
                                startActivity(intent);

                                overridePendingTransition(R.anim.slide_from_right,R.anim.slide_to_left);
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
            public ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_items, parent,false);
                ItemsViewHolder viewHolder = new ItemsViewHolder(view);
                return viewHolder;
            }
        };
        rvSearch.setAdapter(adapter);
        adapter.startListening();
    }
}