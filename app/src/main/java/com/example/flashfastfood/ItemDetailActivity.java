package com.example.flashfastfood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.andremion.counterfab.CounterFab;
import com.example.flashfastfood.Adapter.ItemsViewHolder;
import com.example.flashfastfood.Data.Cart;
import com.example.flashfastfood.Data.CartItemGuest;
import com.example.flashfastfood.Data.Favorite;
import com.example.flashfastfood.Data.Items;
import com.example.flashfastfood.Fragment.BottomSheetCartFragment;
import com.example.flashfastfood.Guest.BottomSheetCartGuestFragment;
import com.example.flashfastfood.Guest.BottomSheetListener;
import com.example.flashfastfood.Guest.MainGuestActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.varunest.sparkbutton.SparkButton;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ItemDetailActivity extends AppCompatActivity implements BottomSheetListener {

    TextView detailItemRating, detailItemName, detailItemPrice, detailItemDes,
            quantity, totalPrice, goback,
            detailItemReviewQuantity, btnShowReview;
    ImageButton btnPlus, btnMinus;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference itemsRef, favoriteRef, cartRef;

    FirebaseRecyclerOptions<Items> options;
    FirebaseRecyclerAdapter<Items, ItemsViewHolder> adapter;

    Items items;
    Favorite favorite;
    Cart cart;
    CardView btnAddToCart;
    SparkButton sparkButton;
    ImageView detailItemImg;
    RecyclerView rvRecommendedItems;
    String itemId;
    Boolean isInMyFavorite = false;
    int totalQuantity = 1;
    int priceint, fullprice;
    String currentUserId, Image;
    ArrayList<String> arrayList = null;
    CounterFab btnCart;
    LinearLayout btnBackHome;
    String guestFlag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_detail);

        detailItemPrice = findViewById(R.id.itemDetailPrice);
        detailItemName = findViewById(R.id.itemDetailName);
        detailItemRating = findViewById(R.id.itemDetailRating);
        detailItemDes = findViewById(R.id.itemDetailDes);
        detailItemReviewQuantity = findViewById(R.id.itemDetailReviewQuantity);
        detailItemImg = findViewById(R.id.itemDetailImg);

        btnMinus = findViewById(R.id.btnMinus);
        btnPlus = findViewById(R.id.btnPlus);
        quantity = findViewById(R.id.quantity);

        btnAddToCart = findViewById(R.id.btnAddToCart);
        totalPrice = findViewById(R.id.itemDetailTotalPrice);

        sparkButton = findViewById(R.id.addFav);

        goback = findViewById(R.id.backprevious);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        items = new Items();
        favorite = new Favorite();
        cart = new Cart();

        firebaseDatabase = FirebaseDatabase.getInstance("https://flashfastfood-81fee-default-rtdb.asia-southeast1.firebasedatabase.app");
        itemsRef = firebaseDatabase.getReference("Items");
        favoriteRef = firebaseDatabase.getReference("Favorite");
        cartRef = firebaseDatabase.getReference("Shopping Cart");

        guestFlag = getIntent().getStringExtra("guestFlag");
        if (guestFlag==null){
            guestFlag="user";
        }
        if (!guestFlag.equals("guest")){
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            currentUserId = user.getUid();
        }
        rvRecommendedItems = findViewById(R.id.rvShowAllItem);
        rvRecommendedItems.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));

        itemId = getIntent().getStringExtra("itemId");

        loadRecommendedItems();
        getDetailItem(itemId);
        getItemQuantity();


        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!guestFlag.equals("guest")) {
                    addToCart();
                }else {
                    CartItemGuest cartItemGuest = new CartItemGuest(
                            itemId,Image,detailItemName.getText().toString(),detailItemPrice.getText().toString(),quantity.getText().toString(),totalPrice.getText().toString());
                    addToCartGuest(cartItemGuest);
                    Toast.makeText(ItemDetailActivity.this, "Item added to cart!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btnCart = findViewById(R.id.detailFabCart);
        if (!guestFlag.equals("guest")){
            getCartQuantity();
            btnCart.setVisibility(View.GONE);
            btnCart.setImageResource(R.drawable.ic_cart);
            btnCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    BottomSheetCartFragment bottomSheetFragment = new BottomSheetCartFragment();
                    bottomSheetFragment.setArguments(bundle);
                    bottomSheetFragment.show(getSupportFragmentManager(),bottomSheetFragment.getTag());
                }
            });
        }else {
            getCartGuestQuantity();
            btnCart.setVisibility(View.VISIBLE);
            btnCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bundle bundle = new Bundle();
                    bundle.putString("guestFlag","guest");
                    BottomSheetCartGuestFragment bottomSheetFragment2 = new BottomSheetCartGuestFragment();
                    bottomSheetFragment2.setBottomSheetListener(ItemDetailActivity.this);
                    bottomSheetFragment2.setArguments(bundle);
                    bottomSheetFragment2.show(getSupportFragmentManager(),bottomSheetFragment2.getTag());
                }
            });
        }

        btnBackHome = findViewById(R.id.btnBackHome);
        btnBackHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ItemDetailActivity.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                int idHome = 1;
                String IDHOME = Integer.toString(idHome);
                intent.putExtra("Fragment",IDHOME);
                startActivity(intent);
            }
        });

        btnShowReview = findViewById(R.id.btnShowReview);
        if (!guestFlag.equals("guest")){
            btnShowReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ItemDetailActivity.this,ReviewActivity.class);
                    intent.putExtra("itemId",itemId);
                    startActivity(intent);
                }
            });
        }else {
            btnShowReview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ItemDetailActivity.this, LoginActivity.class);
                    intent.putExtra("guestLogin","guestLogin");
                    startActivity(intent);
                }
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!guestFlag.equals("guest")){
            getCartQuantity();
        }else {
            getCartGuestQuantity();
        }

    }

    public void addToCartGuest(CartItemGuest item) {
        SharedPreferences sharedPreferences = getSharedPreferences("Cart", MODE_PRIVATE);
        String cartJson = sharedPreferences.getString("CartItems", "{}");
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, CartItemGuest>>(){}.getType();
        Map<String, CartItemGuest> cartItems = gson.fromJson(cartJson, type);

        // If the item already exists in the cart, update its quantity and total price
        if (cartItems.containsKey(item.getItemId())) {
            CartItemGuest existingItem = cartItems.get(item.getItemId());
            Integer newPrice = Integer.parseInt(existingItem.getItemCartTotalPrice()) + Integer.parseInt(item.getItemCartTotalPrice());
            Integer newQuantity = Integer.parseInt(existingItem.getItemCartQuantity()) + Integer.parseInt(item.getItemCartQuantity());

            existingItem.setItemCartQuantity(String.valueOf(newQuantity));
            existingItem.setItemCartTotalPrice(String.valueOf(newPrice));
            cartItems.put(item.getItemId(), existingItem);
        } else {
            // Otherwise, add the new item to the cart
            cartItems.put(item.getItemId(), item);
        }

        // Save the updated cart to the SharedPreferences
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("CartItems", gson.toJson(cartItems));
        editor.apply();
        getCartGuestQuantity();
    }
    public void getCartGuestQuantity() {
        SharedPreferences sharedPreferences = getSharedPreferences("Cart", MODE_PRIVATE);
        String cartJson = sharedPreferences.getString("CartItems", "{}");
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, CartItemGuest>>() {
        }.getType();
        Map<String, CartItemGuest> cartItems = gson.fromJson(cartJson, type);
        int totalItem= 0;
        for (String key : cartItems.keySet()) {
            totalItem++;
        }
        btnCart.setCount(totalItem);
    }

    public void getCartQuantity() {
        cartRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
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

    private void addToCart() {

        cart = new Cart(Image,detailItemName.getText().toString(),detailItemPrice.getText().toString(),quantity.getText().toString(),totalPrice.getText().toString());

        cartRef.child(currentUserId).child(itemId).setValue(cart).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getApplicationContext(),"Added to cart",Toast.LENGTH_SHORT).show();
                getCartQuantity();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(),"Can't add to cart",Toast.LENGTH_SHORT).show();
                getCartQuantity();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_from_left,R.anim.slide_to_right);
    }

    public void getItemQuantity() {
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalQuantity<10){
                    totalQuantity++;
                    quantity.setText(String.valueOf(totalQuantity));
                    fullprice = (int) (priceint*(Integer.parseInt(quantity.getText().toString())));
                    totalPrice.setText(String.valueOf(fullprice));
                }
                if (totalQuantity == 10){
                    Toast.makeText(ItemDetailActivity.this,"You can't add more",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalQuantity>1){
                    totalQuantity--;
                    quantity.setText(String.valueOf(totalQuantity));
                    fullprice = (int) (priceint*(Integer.parseInt(quantity.getText().toString())));
                    totalPrice.setText(String.valueOf(fullprice));
                }
            }
        });
    }

    private void getDetailItem(String itemId) {
        itemsRef.child(itemId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String Name = snapshot.child("itemName").getValue().toString();
                String Price = snapshot.child("itemPrice").getValue().toString();
                String Rating = snapshot.child("itemRating").getValue().toString();
                String Description = snapshot.child("itemDes").getValue().toString();
                Image = snapshot.child("itemImg").getValue().toString();

                detailItemDes.setText(Description);
                detailItemName.setText(Name);
                detailItemPrice.setText(Price);
                detailItemRating.setText("Rate " + Rating);

                Picasso.get().load(Image).into(detailItemImg);
                totalPrice.setText(Price);
                priceint = Integer.parseInt(Price);

                if (!guestFlag.equals("guest")){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String currentUserId = user.getUid();

                    favoriteCheck();
                    favorite.setfavoriteName(Name);
                    favorite.setfavoritePrice(Price);
                    favorite.setfavoriteRating(Rating);
                    favorite.setfavoriteImage(Image);

                    sparkButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sparkButton.playAnimation();
                            if (isInMyFavorite){
                                favoriteRef.child(currentUserId).child(itemId).removeValue()
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

                                                Toast.makeText(ItemDetailActivity.this, centeredText, Toast.LENGTH_SHORT).show();
//                                            Toast.makeText(ItemDetailActivity.this, "Removed from favorite list", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }else {
                                favoriteRef.child(currentUserId).child(itemId).setValue(favorite)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                String text = "Add"+" "+Name+" "+"to favorite list";
                                                Spannable centeredText = new SpannableString(text);
                                                centeredText.setSpan(
                                                        new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER),
                                                        0, text.length() - 1,
                                                        Spannable.SPAN_INCLUSIVE_INCLUSIVE
                                                );

                                                Toast.makeText(ItemDetailActivity.this, centeredText, Toast.LENGTH_SHORT).show();
//                                            Toast.makeText(ItemDetailActivity.this, "Added to favorite list", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    });
                }else {
                    sparkButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(ItemDetailActivity.this, LoginActivity.class);
                            intent.putExtra("guestLogin","guestLogin");
                            startActivity(intent);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void favoriteCheck() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final String userId = user.getUid();

        favoriteRef.child(userId).child(itemId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        isInMyFavorite = snapshot.exists();
                        if (isInMyFavorite){
                            sparkButton.setChecked(true);
                        }else {
                            sparkButton.setChecked(false);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


    private void loadRecommendedItems(){

        Query query = itemsRef.orderByChild("itemRating").startAt("8");

        options =new FirebaseRecyclerOptions.Builder<Items>().setQuery(query,Items.class).build();

        adapter = new FirebaseRecyclerAdapter<Items, ItemsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ItemsViewHolder holder, int position, @NonNull Items model) {
                String postKey = getRef(position).getKey();

                if (itemId.equals(postKey)){
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

                        holder.itemname.setText(name);
                        holder.itemprice.setText(price);
                        holder.itemrating.setText(rating);
                        Picasso.get().load(img).into(holder.itemimg);


                        holder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                Intent intent = new Intent(ItemDetailActivity.this, ItemDetailActivity.class);
                                intent.putExtra("itemId", adapter.getRef(position).getKey());
                                if (guestFlag.equals("guest")){
                                    intent.putExtra("guestFlag","guest");
                                }
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
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommended, parent,false);
                ItemsViewHolder viewHolder = new ItemsViewHolder(view);
                return viewHolder;
            }
        };

        rvRecommendedItems.setAdapter(adapter);
        adapter.startListening();

    }

    @Override
    public void onDismiss(Context context) {
        if (context instanceof ItemDetailActivity) {
            ((ItemDetailActivity) context).getCartGuestQuantity();
        } else if (context instanceof MainGuestActivity) {
            ((MainGuestActivity) context).getCartQuantity();
        }else if (context instanceof ItemsActivity) {
            ((ItemsActivity) context).getCartGuestQuantity();
        }
    }
}