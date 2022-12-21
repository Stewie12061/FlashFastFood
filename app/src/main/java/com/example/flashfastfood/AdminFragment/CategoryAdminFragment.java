package com.example.flashfastfood.AdminFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.flashfastfood.Adapter.ItemsViewHolder;
import com.example.flashfastfood.AdapterAdmin.CateAdminViewHolder;
import com.example.flashfastfood.Admin.ItemAdminActivity;
import com.example.flashfastfood.Data.FoodCategories;
import com.example.flashfastfood.Data.Items;
import com.example.flashfastfood.ItemClickListener;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.UUID;

public class CategoryAdminFragment extends Fragment {

    RecyclerView rvAdCate;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference cateRef;
    FloatingActionButton openCate;

    private final int PICK_IMG_REQUEST = 1705;
    Uri uri = null;
    RoundedImageView imgCate;

    String cateId, cateName, img, catePositionId;
    FoodCategories foodCategories;
    ArrayList<String> arrayList = null;
    FirebaseRecyclerAdapter<FoodCategories, CateAdminViewHolder> adapter;

    private TextView countCate;

    boolean isUpLoad=false;

    public CategoryAdminFragment() {
        // Required empty public constructor
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_category_admin, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance("https://flashfastfood-81fee-default-rtdb.asia-southeast1.firebasedatabase.app");
        cateRef = firebaseDatabase.getReference("Categories");

        rvAdCate = view.findViewById(R.id.rvCategoryAdmin);
        rvAdCate.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        foodCategories = new FoodCategories();
        openCate = view.findViewById(R.id.openCreateCate);
        openCate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCate();
            }
        });

        countCate = view.findViewById(R.id.countCate);
        //get id for new category
        cateRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList = new ArrayList<String>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    arrayList.add(dataSnapshot.getKey());
                }
                //count cate
                countCate.setText(Integer.toString(arrayList.size()));

                //get last item in cate and create id for new cate
                String cateidString = arrayList.get(arrayList.size()-1);
                int cateidInt = Integer.parseInt(cateidString) +1;
                cateId = Integer.toString(cateidInt);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        getCateAdmin();
    }

    private void getCateAdmin() {
        FirebaseRecyclerOptions<FoodCategories> options = new FirebaseRecyclerOptions.Builder<FoodCategories>().setQuery(cateRef,FoodCategories.class).build();

        adapter = new FirebaseRecyclerAdapter<FoodCategories, CateAdminViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CateAdminViewHolder holder, int position, @NonNull FoodCategories model) {
                String id = getRef(position).getKey();

                cateRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String cateImg = snapshot.child("foodCateImg").getValue().toString();
                        String catename = snapshot.child("foodCateName").getValue().toString();

                        holder.cateadName.setText(catename);
                        Picasso.get().load(cateImg).into(holder.cateadImg);

                        //edit category
                        holder.btnmodify.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                //get cate id onclick
                                catePositionId = adapter.getRef(holder.getBindingAdapterPosition()).getKey();
                                modifyCate();
                            }
                        });

                        //delete category
                        holder.btndelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                catePositionId = adapter.getRef(holder.getBindingAdapterPosition()).getKey();
                                cateName = catename;
                                deleteCate();
                            }
                        });

                        holder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onClick(View view, int position, boolean isLongClick) {
                                Intent intent = new Intent(getContext(), ItemAdminActivity.class);
                                intent.putExtra("categoryId",adapter.getRef(position).getKey());
                                intent.putExtra("cateName",catename);
                                startActivity(intent);
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
            public CateAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_categories_admin,parent,false);
                CateAdminViewHolder viewHolder = new CateAdminViewHolder(view);
                return viewHolder;
            }
        };
        rvAdCate.setAdapter(adapter);
        adapter.startListening();
    }

    private void deleteCate() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder
                        (getContext());
        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.dialog_alert,
                (ConstraintLayout) getView().findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle))
                .setText("Delete category");
        ((TextView) view.findViewById(R.id.textMessage))
                .setText("Are you sure you want delete"+ " "+cateName+" "+"category?");
        ((Button) view.findViewById(R.id.buttonYes))
                .setText("Yes");
        ((Button) view.findViewById(R.id.buttonNo))
                .setText("No");
        final AlertDialog alertDialog = builder.create();
        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cateRef.child(catePositionId).removeValue();
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

    private void addCate(){

        AlertDialog.Builder builder =
                new AlertDialog.Builder
                        (getContext());
        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.dialog_create_category,
                (ConstraintLayout) getView().findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);

        ((TextView) view.findViewById(R.id.textTitle))
                .setText("Add new Category");
        ((TextView) view.findViewById(R.id.textMessage))
                .setText("Fill category name and upload image");
        ((Button) view.findViewById(R.id.buttonYes))
                .setText("Create");
        ((Button) view.findViewById(R.id.buttonNo))
                .setText("Cancel");

        Button select = (Button) view.findViewById(R.id.btnSelect);
        Button upload = (Button) view.findViewById(R.id.btnUpload);
        EditText cateName = (EditText) view.findViewById(R.id.edtCateName);
        imgCate = view.findViewById(R.id.imgCate);


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
                    progressDialog = new ProgressDialog(getContext());
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.dialog_progress);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    FirebaseStorage firebaseStorage;
                    StorageReference storageReference;

                    // get the Firebase  storage reference
                    firebaseStorage = FirebaseStorage.getInstance();
                    storageReference = firebaseStorage.getReference();

                    String imageName = UUID.randomUUID().toString();
                    StorageReference imageFolder = storageReference.child("Images/categories/"+imageName);

                    //put img to storage
                    imageFolder.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            isUpLoad=true;
                            progressDialog.dismiss();
                            Toast.makeText(getContext(),"Upload sussessed",Toast.LENGTH_SHORT).show();

                            //get uri img from storage
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    foodCategories = new FoodCategories(uri.toString(),cateName.getText().toString());
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            isUpLoad=false;
                            progressDialog.dismiss();
                            Toast.makeText(getContext(),"Upload failed",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(cateName.getText().toString().trim())){
                    cateName.setError("You have to fill this information!");
                    cateName.requestFocus();
                }
                else if(uri==null){
                    Toast.makeText(getContext(),"You have to fill full information to create category",Toast.LENGTH_SHORT).show();
                }
                else if (isUpLoad==false){
                    Toast.makeText(getContext(),"You have to upload image",Toast.LENGTH_SHORT).show();
                }
                else {
                    if (foodCategories != null){
                        alertDialog.dismiss();
                        cateRef.child(cateId).setValue(foodCategories).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getContext(),"Success create category",Toast.LENGTH_SHORT).show();
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

    private void modifyCate(){

        AlertDialog.Builder builder =
                new AlertDialog.Builder
                        (getContext());
        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.dialog_create_category,
                (ConstraintLayout) getView().findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);

        ((TextView) view.findViewById(R.id.textTitle))
                .setText("Modify Category");
        ((TextView) view.findViewById(R.id.textMessage))
                .setText("Fill category name and upload image");
        ((Button) view.findViewById(R.id.buttonYes))
                .setText("Update");
        ((Button) view.findViewById(R.id.buttonNo))
                .setText("Cancel");

        Button select = (Button) view.findViewById(R.id.btnSelect);
        Button upload = (Button) view.findViewById(R.id.btnUpload);
        EditText cateName = (EditText) view.findViewById(R.id.edtCateName);
        imgCate = view.findViewById(R.id.imgCate);

        cateRef.child(catePositionId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                img = snapshot.child("foodCateImg").getValue().toString();
                cateName.setText(snapshot.child("foodCateName").getValue().toString());
                Picasso.get().load(img).into(imgCate);
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
                    progressDialog = new ProgressDialog(getContext());
                    progressDialog.show();
                    progressDialog.setContentView(R.layout.dialog_progress);
                    progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

                    FirebaseStorage firebaseStorage;
                    StorageReference storageReference;

                    // get the Firebase  storage reference
                    firebaseStorage = FirebaseStorage.getInstance();
                    storageReference = firebaseStorage.getReference();

                    String imageName = UUID.randomUUID().toString();
                    StorageReference imageFolder = storageReference.child("Images/categories/"+imageName);

                    //put img to storage
                    imageFolder.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(),"Upload succeed",Toast.LENGTH_SHORT).show();

                            //get uri img from storage
                            imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    foodCategories = new FoodCategories(uri.toString(),cateName.getText().toString());
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            isUpLoad=false;
                            progressDialog.dismiss();
                            Toast.makeText(getContext(),"Upload failed",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(cateName.getText().toString().trim())){
                    cateName.setError("You have to fill this information!");
                    cateName.requestFocus();
                }
                else {
                    if (uri==null){
                        alertDialog.dismiss();
                        foodCategories = new FoodCategories(img,cateName.getText().toString());
                        cateRef.child(catePositionId).setValue(foodCategories).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getContext(),"Create succeed",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    else{
                        if (isUpLoad==false){
                            Toast.makeText(getContext(),"You have to upload image",Toast.LENGTH_SHORT).show();
                        }
                        alertDialog.dismiss();
                        cateRef.child(catePositionId).setValue(foodCategories).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(getContext(),"Create succeed",Toast.LENGTH_SHORT).show();
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
                imgCate.setImageURI(uri);
            }
        }
    }
}