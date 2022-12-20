package com.example.flashfastfood.AdminFragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.flashfastfood.AdapterAdmin.CateAdminViewHolder;
import com.example.flashfastfood.AdapterAdmin.DiscountAdminViewHolder;
import com.example.flashfastfood.Admin.ItemAdminActivity;
import com.example.flashfastfood.Data.Discount;
import com.example.flashfastfood.Data.FoodCategories;
import com.example.flashfastfood.ItemClickListener;
import com.example.flashfastfood.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;
import java.util.UUID;


public class DiscountManagementFragment extends Fragment {

    RecyclerView rvAdDiscount;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference discountRef;
    FloatingActionButton openCreateDiscount;

    int mDayIn,mMonthIn,mYearIn;


    String discountId, discountName, discountPositionId;
    Discount discount;
    ArrayList<String> arrayList = null;
    FirebaseRecyclerAdapter<Discount, DiscountAdminViewHolder> adapter;



    private TextView countDiscount;

    public DiscountManagementFragment() {
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
        return inflater.inflate(R.layout.fragment_discount_management, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseDatabase = FirebaseDatabase.getInstance("https://flashfastfood-81fee-default-rtdb.asia-southeast1.firebasedatabase.app");
        discountRef = firebaseDatabase.getReference("Discount");

        rvAdDiscount = view.findViewById(R.id.rvDiscountAd);
        rvAdDiscount.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        discount = new Discount();
        openCreateDiscount = view.findViewById(R.id.openCreateDiscount);
        openCreateDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addDiscount();
            }
        });

        countDiscount = view.findViewById(R.id.countDiscount);
        //get id for new category
        discountRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayList = new ArrayList<String>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    arrayList.add(dataSnapshot.getKey());
                }
                //count cate
                countDiscount.setText(Integer.toString(arrayList.size()));

                //get last item in cate and create id for new cate
                String cateidString = arrayList.get(arrayList.size()-1);
                int cateidInt = Integer.parseInt(cateidString) +1;
                discountId = Integer.toString(cateidInt);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        getDiscountAd();
    }

    private void getDiscountAd() {
        FirebaseRecyclerOptions<Discount> options = new FirebaseRecyclerOptions.Builder<Discount>().setQuery(discountRef,Discount.class).build();

        adapter = new FirebaseRecyclerAdapter<Discount, DiscountAdminViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull DiscountAdminViewHolder holder, int position, @NonNull Discount model) {
                String id = getRef(position).getKey();

                discountRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String discountname = snapshot.child("name").getValue().toString();
                        String discountDes = snapshot.child("des").getValue().toString();
                        String discountExpDay = snapshot.child("expDay").getValue().toString();

                        holder.discountDes.setText(discountDes);
                        holder.discountExpDay.setText(discountExpDay);
                        holder.discountName.setText(discountname);

                        holder.btnDeleteDiscount.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                discountPositionId = adapter.getRef(holder.getBindingAdapterPosition()).getKey();
                                discountName = discountname;
                                deleteDiscount();
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
            public DiscountAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_discount_admin,parent,false);
                DiscountAdminViewHolder viewHolder = new DiscountAdminViewHolder(view);
                return viewHolder;
            }
        };
        rvAdDiscount.setAdapter(adapter);
        adapter.startListening();
    }

    private void deleteDiscount() {
        AlertDialog.Builder builder =
                new AlertDialog.Builder
                        (getContext());
        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.dialog_alert,
                (ConstraintLayout) getView().findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);
        ((TextView) view.findViewById(R.id.textTitle))
                .setText("Delete voucher");
        ((TextView) view.findViewById(R.id.textMessage))
                .setText("Are you sure you want delete"+ " "+discountName+" "+"Voucher?");
        ((Button) view.findViewById(R.id.buttonYes))
                .setText("Yes");
        ((Button) view.findViewById(R.id.buttonNo))
                .setText("No");
        final AlertDialog alertDialog = builder.create();
        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discountRef.child(discountPositionId).removeValue();
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

    private void addDiscount(){

        AlertDialog.Builder builder =
                new AlertDialog.Builder
                        (getContext());
        View view = LayoutInflater.from(getContext()).inflate(
                R.layout.dialog_create_discount,
                (ConstraintLayout) getView().findViewById(R.id.layoutDialogContainer)
        );
        builder.setView(view);

        ((TextView) view.findViewById(R.id.textTitle))
                .setText("Create Voucher");
        ((TextView) view.findViewById(R.id.textMessage))
                .setText("Fill category name and upload image");
        ((Button) view.findViewById(R.id.buttonYes))
                .setText("Create");
        ((Button) view.findViewById(R.id.buttonNo))
                .setText("Cancel");

        EditText edtDiscountName = (EditText) view.findViewById(R.id.edtDiscountName);
        EditText edtDiscountValue = (EditText) view.findViewById(R.id.edtDiscountValue);
        EditText edtDiscountCondition = (EditText) view.findViewById(R.id.edtDiscountCondition);
        EditText edtDiscountType = (EditText) view.findViewById(R.id.edtDiscountType);
        EditText edtDiscountExpDay = (EditText) view.findViewById(R.id.edtDiscountExpDay);
        EditText edtDiscountDes = (EditText) view.findViewById(R.id.edtDiscountDes);

        edtDiscountExpDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                //set time zone
                calendar.setTimeZone(TimeZone.getTimeZone("UTC"));

                int selectedYear = calendar.get(Calendar.YEAR);
                int selectedMonth = calendar.get(Calendar.MONTH);
                int selectedDay = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            public void onDateSet(DatePicker view, int selectedYear,
                                                  int selectedMonth, int selectedDay) {
                                mDayIn = selectedDay;
                                mMonthIn = selectedMonth;
                                mYearIn = selectedYear;

                                edtDiscountExpDay.setText((mMonthIn+1) + "-" + mDayIn + "-" + mYearIn);
                            }
                        }, mDayIn, mMonthIn, mYearIn);
                datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

                final Calendar calendar2 = Calendar.getInstance();
                calendar2.set(2023, 1, 1);
                datePickerDialog.getDatePicker().setMaxDate(calendar2.getTimeInMillis());
                datePickerDialog.setTitle("Select Date Expire");
                datePickerDialog.show();
            }
        });

        final AlertDialog alertDialog = builder.create();

        view.findViewById(R.id.buttonYes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(edtDiscountName.getText().toString().trim())){
                    edtDiscountName.setError("You have to fill this information!");
                    edtDiscountName.requestFocus();
                }else if (TextUtils.isEmpty(edtDiscountValue.getText().toString().trim())){
                    edtDiscountValue.setError("You have to fill this information!");
                    edtDiscountValue.requestFocus();
                }else if (TextUtils.isEmpty(edtDiscountCondition.getText().toString().trim())){
                    edtDiscountCondition.setError("You have to fill this information!");
                    edtDiscountCondition.requestFocus();
                }else if (TextUtils.isEmpty(edtDiscountType.getText().toString().trim())){
                    edtDiscountType.setError("You have to fill this information!");
                    edtDiscountType.requestFocus();
                }else if (TextUtils.isEmpty(edtDiscountExpDay.getText().toString().trim())){
                    edtDiscountExpDay.setError("You have to fill this information!");
                    edtDiscountExpDay.requestFocus();
                }else if (TextUtils.isEmpty(edtDiscountDes.getText().toString().trim())){
                    edtDiscountDes.setError("You have to fill this information!");
                    edtDiscountDes.requestFocus();
                }
                else {
                    alertDialog.dismiss();
                    discount = new Discount(edtDiscountName.getText().toString(),edtDiscountDes.getText().toString(), edtDiscountType.getText().toString(), edtDiscountExpDay.getText().toString()
                    ,Integer.parseInt(edtDiscountValue.getText().toString()),edtDiscountCondition.getText().toString());
                    discountRef.child(discountId).setValue(discount).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getContext(),"Success create category",Toast.LENGTH_SHORT).show();
                        }
                    });
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

}