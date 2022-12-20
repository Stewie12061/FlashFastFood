package com.example.flashfastfood.Fragment;


import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.flashfastfood.Adapter.CategoryTypeViewHolder;
import com.example.flashfastfood.Data.FoodCategories;
import com.example.flashfastfood.ItemClickListener;
import com.example.flashfastfood.ItemsActivity;
import com.example.flashfastfood.R;
import com.example.flashfastfood.SearchActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class HomeFragment extends Fragment {

    TextView btnShowAll;
    RecyclerView rvCategories;

    ImageSlider imageSlider;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference sliderAdRef, categoriesRef;
    List<SlideModel> slideModelArrayList;
    FoodCategories foodCategories;

    private GoogleMap mMap;
    Geocoder geocoder;

    Button btnSearch;

    FirebaseRecyclerAdapter<FoodCategories, CategoryTypeViewHolder> adapterCategories;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SupportMapFragment supportMapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull GoogleMap googleMap) {
                mMap = googleMap;

                geocoder = new Geocoder(getContext(), Locale.getDefault());
                List<Address> addressList = null;

                String ItemAddress = "828 Sư Vạn Hạnh, Phường 13, Quận 10, TP. Hồ Chí Minh";

                try {
                    addressList = geocoder.getFromLocationName(ItemAddress,1);


                if (addressList != null && addressList.size() > 0){
                    Address address = addressList.get(0);

                    Log.d(TAG,address.getLatitude()+" "+ address.getLongitude());

                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                    mMap.addMarker(new MarkerOptions().position(latLng).title("828 Sư Vạn Hạnh, Phường 13, Quận 10").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                    mMap.getUiSettings().setZoomControlsEnabled(true);
                }
                else {
                    Toast.makeText(getContext(), "Can't find location",Toast.LENGTH_SHORT).show();

                }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });


        firebaseDatabase = FirebaseDatabase.getInstance("https://flashfastfood-81fee-default-rtdb.asia-southeast1.firebasedatabase.app");
        sliderAdRef = firebaseDatabase.getReference("SliderAd");
        categoriesRef = firebaseDatabase.getReference("Categories");

        imageSlider = view.findViewById(R.id.advertise);


        rvCategories = (RecyclerView) view.findViewById(R.id.rvCategory);
        rvCategories.setLayoutManager(new GridLayoutManager(getContext(), 2));

        btnShowAll = view.findViewById(R.id.showAllItems);
        btnShowAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ItemsActivity.class);
                getActivity().overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                startActivity(intent);
            }
        });

        btnSearch = view.findViewById(R.id.searchBtn);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        slideModelArrayList = new ArrayList<>();
        getDetailImgSlider(slideModelArrayList);
        getCategories();
    }

    private void getCategories(){
        Query query = categoriesRef;

        FirebaseRecyclerOptions<FoodCategories> options = new FirebaseRecyclerOptions.Builder<FoodCategories>().setQuery(query  , FoodCategories.class).build();

        adapterCategories = new FirebaseRecyclerAdapter<FoodCategories, CategoryTypeViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CategoryTypeViewHolder categoryTypeViewHolder, int position, @NonNull FoodCategories foodCategories) {

                String id = getRef(position).getKey();

                categoriesRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String catename = dataSnapshot.child("foodCateName").getValue().toString();
                        String cateimg = dataSnapshot.child("foodCateImg").getValue().toString();

                        categoryTypeViewHolder.cateName.setText(catename);
                        Picasso.get().load(cateimg).into(categoryTypeViewHolder.cateImg);


                        //set random bg color
//                        String [] color = {"#fef4e5", "#E5F1FE", "#EBFEE5", "#F9E4E4"};
//                        Random rnd = new Random();
//                        int select = rnd.nextInt(color.length);
//                        categoryTypeViewHolder.cardView.setCardBackgroundColor(Color.parseColor(color[select]));

                        switch (id){
                            case "1":{
                                categoryTypeViewHolder.cardView.setCardBackgroundColor(Color.parseColor("#fef4e5"));
                                break;
                            }
                            case "2":{
                                categoryTypeViewHolder.cardView.setCardBackgroundColor(Color.parseColor("#E5F1FE"));
                                break;
                            }
                            case "3":{
                                categoryTypeViewHolder.cardView.setCardBackgroundColor(Color.parseColor("#EBFEE5"));
                                break;
                            }
                            case "4":{
                                categoryTypeViewHolder.cardView.setCardBackgroundColor(Color.parseColor("#F9E4E4"));
                                break;
                            }
                            default: {
                                String [] color = {"#fef4e5", "#E5F1FE", "#EBFEE5", "#F9E4E4"};
                                Random rnd = new Random();
                                int select = rnd.nextInt(color.length);
                                categoryTypeViewHolder.cardView.setCardBackgroundColor(Color.parseColor(color[select]));
                            }
                        }

                        categoryTypeViewHolder.setItemClickListener(new ItemClickListener() {

                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                Intent intent = new Intent(getContext(), ItemsActivity.class);
                                intent.putExtra("IdCategory",adapterCategories.getRef(position).getKey());
                                startActivity(intent);
                                getActivity().overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @NonNull
            @Override
            public CategoryTypeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categories, parent,false);
                CategoryTypeViewHolder viewHolder = new CategoryTypeViewHolder(view);
                return viewHolder;
            }
        };

        rvCategories.setAdapter(adapterCategories);
        adapterCategories.startListening();
    }

    private void getDetailImgSlider(List<SlideModel> slideModelArrayList){
        sliderAdRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    slideModelArrayList.add(new SlideModel(dataSnapshot.child("url").getValue().toString(), ScaleTypes.CENTER_CROP));
                    imageSlider.setImageList(slideModelArrayList, ScaleTypes.CENTER_CROP);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}