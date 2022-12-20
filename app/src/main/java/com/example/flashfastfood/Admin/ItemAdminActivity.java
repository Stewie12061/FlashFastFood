package com.example.flashfastfood.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flashfastfood.AdapterAdmin.ItemAdminVIewHolder;
import com.example.flashfastfood.Data.Items;
import com.example.flashfastfood.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.UUID;

public class ItemAdminActivity extends AppCompatActivity {

    int PICK_IMG_REQUEST = 1705;
    TextView countItem, itemCateName,goback;
    ArrayList<String> arrayList;
    ArrayList<String> arrayList2;
    RecyclerView rvItemAd;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference itemRef;
    Uri uri;
    Items items;

    String itemIdForCreate;

    FloatingActionButton openAddItem;

    String cateId;
    FirebaseRecyclerAdapter<Items, ItemAdminVIewHolder> adapter;

    String itemPositionId, itemName, catename;

    String Name, Price, Status, Rating, Image, Description;
    public RoundedImageView imgItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_admin);

        firebaseDatabase = FirebaseDatabase.getInstance("https://flashfastfood-81fee-default-rtdb.asia-southeast1.firebasedatabase.app");
        itemRef = firebaseDatabase.getReference("Items");

        goback = findViewById(R.id.backprevious);
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        rvItemAd = findViewById(R.id.rvItemAdmin);
        rvItemAd.setLayoutManager(new LinearLayoutManager(ItemAdminActivity.this,LinearLayoutManager.VERTICAL,false));

        getNewItemKeyForCreate();
        cateId = getIntent().getStringExtra("categoryId");

        openAddItem = findViewById(R.id.openCreateItem);
        openAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createItem();
            }
        });

        itemCateName = findViewById(R.id.itemCateName);
        catename = getIntent().getStringExtra("cateName");
        itemCateName.setText(catename);
        countItem = findViewById(R.id.countItem);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (cateId!=null){
            getItemData();
        }
    }

    private void getItemData() {
        Query query = itemRef.orderByChild("cateId").equalTo(cateId);
        FirebaseRecyclerOptions<Items> options = new FirebaseRecyclerOptions.Builder<Items>().setQuery(query,Items.class).build();

        adapter = new FirebaseRecyclerAdapter<Items, ItemAdminVIewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ItemAdminVIewHolder holder, int position, @NonNull Items model) {
                String postKey = getRef(position).getKey();

                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        arrayList = new ArrayList<String>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            arrayList.add(dataSnapshot.getKey());
                        }
                        //count cate
                        countItem.setText(Integer.toString(arrayList.size()));
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                itemRef.child(postKey).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Name = snapshot.child("itemName").getValue().toString();
                        Price = snapshot.child("itemPrice").getValue().toString();
                        Status = snapshot.child("itemStatus").getValue().toString();
                        Rating = snapshot.child("itemRating").getValue().toString();
                        Image = snapshot.child("itemImg").getValue().toString();

                        holder.ItemAdName.setText(Name);
                        holder.ItemAdStatus.setText(Status);
                        holder.ItemAdPrice.setText(Price);
                        holder.ItemAdRating.setText(Rating);
                        Picasso.get().load(Image).into(holder.ItemAdImg);

                        holder.btnDeleteItem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                itemPositionId = adapter.getRef(holder.getBindingAdapterPosition()).getKey();
                                itemName = Name;
                                deleteItem();
                            }
                        });

                        holder.btnModifyItem.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                itemPositionId = adapter.getRef(holder.getBindingAdapterPosition()).getKey();
                                updateItem();
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
            public ItemAdminVIewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_item_admin,parent,false);
                ItemAdminVIewHolder viewHolder = new ItemAdminVIewHolder(view);
                return viewHolder;
            }
        };
        rvItemAd.setAdapter(adapter);
        adapter.startListening();
    }

    private void updateItem() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder
                        (ItemAdminActivity.this);
        View view = LayoutInflater.from(ItemAdminActivity.this).inflate(
                R.layout.dialog_update_item,
                (NestedScrollView) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);

        ((TextView) view.findViewById(R.id.textTitle))
                .setText("Update Item");
        ((TextView) view.findViewById(R.id.textMessage))
                .setText("Fill all information to update");
        ((Button) view.findViewById(R.id.buttonYes))
                .setText("Update");
        ((Button) view.findViewById(R.id.buttonNo))
                .setText("Cancel");

        Button select = (Button) view.findViewById(R.id.btnSelect);
        Button upload = (Button) view.findViewById(R.id.btnUpload);
        EditText itemName = (EditText) view.findViewById(R.id.edtItemName);
        EditText itemStatus = (EditText) view.findViewById(R.id.edtItemStatus);
        EditText itemPrice = (EditText) view.findViewById(R.id.edtItemPrice);
        EditText itemRating = (EditText) view.findViewById(R.id.edtItemRating);
        EditText itemDes = (EditText) view.findViewById(R.id.edtItemDes);
        imgItem = view.findViewById(R.id.imgItem);

        itemRef.child(itemPositionId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Name = snapshot.child("itemName").getValue().toString();
                Price = snapshot.child("itemPrice").getValue().toString();
                Status = snapshot.child("itemStatus").getValue().toString();
                Rating = snapshot.child("itemRating").getValue().toString();
                Image = snapshot.child("itemImg").getValue().toString();
                Description = snapshot.child("itemDes").getValue().toString();

                itemName.setText(Name);
                itemStatus.setText(Status);
                itemPrice.setText(Price);
                itemRating.setText(Rating);
                itemDes.setText(Description);
                Picasso.get().load(Image).into(imgItem);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImg();
            }

        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uri!=null){
                    ProgressDialog progressDialog;
                    progressDialog = new ProgressDialog(ItemAdminActivity.this);
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.dialog_progress);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    FirebaseStorage firebaseStorage;
                    StorageReference storageReference;

                    // get the Firebase  storage reference
                    firebaseStorage = FirebaseStorage.getInstance();
                    storageReference = firebaseStorage.getReference();

                    String imageName = UUID.randomUUID().toString();
                    StorageReference imageFolder = storageReference.child("Images/items/"+imageName);

                    //put img to storage
                    imageFolder.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(ItemAdminActivity.this,"Upload succeed",Toast.LENGTH_SHORT).show();

                            //get uri img from storage
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    items = new Items(itemDes.getText().toString(),uri.toString(),cateId,itemName.getText().toString(),itemStatus.getText().toString(),itemPrice.getText().toString(),itemRating.getText().toString());
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ItemAdminActivity.this,"Upload failed",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(itemName.getText().toString().trim())){
                    itemName.setError("You have to fill this information!");
                    itemName.requestFocus();
                }
                else if(TextUtils.isEmpty(itemStatus.getText().toString().trim())){
                    itemStatus.setError("You have to fill this information!");
                    itemStatus.requestFocus();
                }
                else if(TextUtils.isEmpty(itemPrice.getText().toString().trim())){
                    itemPrice.setError("You have to fill this information!");
                    itemPrice.requestFocus();
                }
                else if(TextUtils.isEmpty(itemDes.getText().toString().trim())){
                    itemDes.setError("You have to fill this information!");
                    itemDes.requestFocus();
                }
                else if(TextUtils.isEmpty(itemRating.getText().toString().trim())){
                    itemRating.setError("You have to fill this information!");
                    itemRating.requestFocus();
                }
                else {
                    if (uri==null){
                        alertDialog.dismiss();
                        items = new Items(itemDes.getText().toString(),Image,cateId,itemName.getText().toString(),itemStatus.getText().toString(),itemPrice.getText().toString(),itemRating.getText().toString());
                        itemRef.child(itemPositionId).setValue(items).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getApplicationContext(),"Update succeed",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else{
                        alertDialog.dismiss();
                        itemRef.child(itemPositionId).setValue(items).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getApplicationContext(),"Update succeed",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }


            }
        });
        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        if (alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void createItem() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder
                        (ItemAdminActivity.this);
        View view = LayoutInflater.from(ItemAdminActivity.this).inflate(
                R.layout.dialog_update_item,
                (NestedScrollView) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);

        ((TextView) view.findViewById(R.id.textTitle))
                .setText("Update Item");
        ((TextView) view.findViewById(R.id.textMessage))
                .setText("Fill all information to update");
        ((Button) view.findViewById(R.id.buttonYes))
                .setText("Update");
        ((Button) view.findViewById(R.id.buttonNo))
                .setText("Cancel");

        Button select = (Button) view.findViewById(R.id.btnSelect);
        Button upload = (Button) view.findViewById(R.id.btnUpload);
        EditText itemName = (EditText) view.findViewById(R.id.edtItemName);
        EditText itemStatus = (EditText) view.findViewById(R.id.edtItemStatus);
        EditText itemPrice = (EditText) view.findViewById(R.id.edtItemPrice);
        EditText itemRating = (EditText) view.findViewById(R.id.edtItemRating);
        EditText itemDes = (EditText) view.findViewById(R.id.edtItemDes);
        imgItem = view.findViewById(R.id.imgItem);

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImg();
            }

        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uri!=null){
                    ProgressDialog progressDialog;
                    progressDialog = new ProgressDialog(ItemAdminActivity.this);
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.dialog_progress);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    FirebaseStorage firebaseStorage;
                    StorageReference storageReference;

                    // get the Firebase  storage reference
                    firebaseStorage = FirebaseStorage.getInstance();
                    storageReference = firebaseStorage.getReference();

                    String imageName = UUID.randomUUID().toString();
                    StorageReference imageFolder = storageReference.child("Images/items/"+imageName);

                    //put img to storage
                    imageFolder.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(ItemAdminActivity.this,"Upload succeed",Toast.LENGTH_SHORT).show();

                            //get uri img from storage
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    items = new Items(itemDes.getText().toString(),uri.toString(),cateId,itemName.getText().toString(),itemStatus.getText().toString(),itemPrice.getText().toString(),itemRating.getText().toString());
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ItemAdminActivity.this,"Upload failed",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(itemName.getText().toString().trim())){
                    itemName.setError("You have to fill this information!");
                    itemName.requestFocus();
                }
                else if(TextUtils.isEmpty(itemStatus.getText().toString().trim())){
                    itemStatus.setError("You have to fill this information!");
                    itemStatus.requestFocus();
                }
                else if(TextUtils.isEmpty(itemPrice.getText().toString().trim())){
                    itemPrice.setError("You have to fill this information!");
                    itemPrice.requestFocus();
                }
                else if(TextUtils.isEmpty(itemDes.getText().toString().trim())){
                    itemDes.setError("You have to fill this information!");
                    itemDes.requestFocus();
                }
                else if(TextUtils.isEmpty(itemRating.getText().toString().trim())){
                    itemRating.setError("You have to fill this information!");
                    itemRating.requestFocus();
                }
                else {
                    if (uri==null){
                        alertDialog.dismiss();
                        items = new Items(itemDes.getText().toString(),Image,cateId,itemName.getText().toString(),itemStatus.getText().toString(),itemPrice.getText().toString(),itemRating.getText().toString());
                        itemRef.child(itemIdForCreate).setValue(items).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getApplicationContext(),"Create succeed",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else{
                        alertDialog.dismiss();
                        itemRef.child(itemIdForCreate).setValue(items).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getApplicationContext(),"Create succeed",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }


            }
        });
        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        if (alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void deleteItem() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder
                        (ItemAdminActivity.this);
        View view = LayoutInflater.from(ItemAdminActivity.this).inflate(
                R.layout.dialog_alert,
                (ConstraintLayout) findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle))
                .setText("Delete item");
        ((TextView) view.findViewById(R.id.textMessage))
                .setText("Are you sure you want delete"+ " "+itemName+" "+"?");
        ((Button) view.findViewById(R.id.buttonYes))
                .setText("Yes");
        ((Button) view.findViewById(R.id.buttonNo))
                .setText("No");
        final AlertDialog alertDialog = builder.create();
        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemRef.child(itemPositionId).removeValue();
                alertDialog.dismiss();
            }
        });
        view.findViewById(R.id.buttonNo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
        if (alertDialog.getWindow() != null){
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }

    private void getNewItemKeyForCreate() {
        itemRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList2 = new ArrayList<String>();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    arrayList2.add(dataSnapshot.getKey());
                }
                //get last itemId in item and create id for new item
                String itemidString = arrayList2.get(arrayList2.size()-1);
                int itemidInt = Integer.parseInt(itemidString) +1;
                itemIdForCreate = Integer.toString(itemidInt);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void chooseImg() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),PICK_IMG_REQUEST);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMG_REQUEST && resultCode == Activity.RESULT_OK
                && data!=null && data.getData() != null){
            uri = data.getData();
            if (null != uri) {
                // update the preview image in the layout
                imgItem.setImageURI(uri);
            }
        }
    }
}