package com.example.favoritelist;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
//we are implementing the ItemClickListener Interface
public class MainActivity extends AppCompatActivity implements ItemClickListener{
    public static final String Clinic_OBJECT = "ClinicObject";
    //Fifth create a list of books
    List<Clinic> clinicList = new ArrayList<>();
    //7th
    private ClinicAdapter clinicAdapter;

    private DatabaseReference myRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Clinics");
        //9th: connect everything together
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        clinicAdapter = new ClinicAdapter();
        //setSampleClinicData();


        //Firebase database Instance
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //cache database locally
        database.setPersistenceEnabled(true);
        myRef = database.getReference("Clinics");

        //writeClinicDataToFirebase();
        readClinicDataToFirebase();



        //use this setting to improve performance if you know that changes in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        //use a linear layout manager
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        //Add a default animator
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //Add a divider line between the rows
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        //specify an adapter
        recyclerView.setAdapter(clinicAdapter);
        //Add the listener setter to the adapter
        clinicAdapter.setItemClickListener(this);




    }
    //Read from Firebase database
    private void readClinicDataToFirebase() {
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(! dataSnapshot.exists()) {
                    Toast.makeText(MainActivity.this, "dataSnapshot does not exist" ,  Toast.LENGTH_SHORT).show();
                    return;
                }
                clinicList.clear();
                for (DataSnapshot bookDataSnapshot: dataSnapshot.getChildren()
                     ) {
                    Clinic clinic = bookDataSnapshot.getValue(Clinic.class);
                    clinicList.add(clinic);
                }
                clinicAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Database Error" + databaseError.getMessage(),  Toast.LENGTH_SHORT).show();

            }
        });
    }

    // Write book list to your database
    private void writeClinicDataToFirebase() {
        if(clinicList == null)
            Toast.makeText(this, "bookStoreList is null", Toast.LENGTH_SHORT).show();
        else if(clinicList.size() == 0)
            Toast.makeText(this, "bookStoreList contains 0 objects", Toast.LENGTH_SHORT).show();
        else {

            myRef.setValue(clinicList).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(MainActivity.this, "Bookstore data saved successfully", Toast.LENGTH_SHORT).show();
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, "Bookstore data could not be saved", Toast.LENGTH_SHORT).show();
                        }
                    })
            ;
        }
    }

    @Override
    //Transition from one activity to another
    public void onClick(View view, int position) {
        //Toast.makeText(this, bookList.get(position).getBookName(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, DetailActivity.class);
        //to get information about a specific movie based on position
        Clinic clinic = clinicList.get(position);
        //send data to another activity
        intent.putExtra(Clinic_OBJECT, clinic);


        startActivity(intent);
    }



    //first add this for the adapter then Alt + Enter
    private class ClinicAdapter extends RecyclerView.Adapter<ClinicAdapter.ClinicViewHolder> {

        private ItemClickListener itemClickListener;
        @NonNull
        @Override
        //here we get the customized view from book_list_row
        public ClinicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //Second add the itemView
            View itemView = LayoutInflater.from(MainActivity.this)
                    .inflate(R.layout.clinic_list_row, parent, false);
            return new ClinicViewHolder(itemView);
        }

        @Override
        //Fourth add the setters and getters
        public void onBindViewHolder(@NonNull ClinicViewHolder holder, int position) {
            Clinic clinic = clinicList.get(position);
            holder.tvClinicName.setText(clinic.getClinicName());
            holder.tvClinicLocation.setText(clinic.getClinicLocation());
            holder.tvClinicType.setText(clinic.getClinicType());
            int redId = getResources().getIdentifier(clinic.getClinicImage(), "drawable", getPackageName());
            holder.ivClinicImage.setImageResource(redId);

        }

        @Override
        //Sixth get the item count
        public int getItemCount() {
            if(clinicList.size() >0)
                return clinicList.size();
            else
                return 0;
        }
        //the Listener setter
        private void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }
        //Third add the properties of the Views + implement the Listener
        public class ClinicViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView tvClinicName;
            private TextView tvClinicLocation;
            private TextView tvClinicType;
            private ImageView ivClinicImage;
            //private ImageView ivBookRating;
            public ClinicViewHolder(@NonNull View itemView) {
                super(itemView);
                tvClinicName = itemView.findViewById(R.id.tvClinicName);
                tvClinicLocation = itemView.findViewById(R.id.tvClinicLocation);
               // tvBookGenre = itemView.findViewById(R.id.tvBookGenre);
                tvClinicType = itemView.findViewById(R.id.tvClinicService);
                ivClinicImage = itemView.findViewById(R.id.ivClinicImage);
               // ivBookRating = itemView.findViewById(R.id.ivBookRating);
                //add the listener to the constructor
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                if(itemClickListener != null) {
                    itemClickListener.onClick(view, getAdapterPosition());
                }

            }
        }
    }
     //8th: Data Source Provider (update price as you like)

    private void setSampleClinicData() {
        Clinic clinic = new Clinic("Kaya Clinic", "Muscat City Centre", "kaya", 23.60031460345703, 58.24813288639798, "The amazing thing about beauty is how powerful it makes you feel. It elevates how you feel about yourself and unlocks a confidence like never before. Women today are becoming more aware and comfortable in the way they express their beauty. They are fearless in their choices and unlimited in their ambitions and aspirations.\n" +
                "At Kaya, we believe that beauty should be empowering. It must always be a bold expression of one’s true self. It is this sentiment we uphold and celebrate at Kaya, each day and it is our purpose to “empower women to be fearless in their pursuit of beauty.”\n", 12.00, "Dermatology");
        clinicList.add(clinic);
        clinic = new Clinic("Elite Clinic", "Azaiba 18 November street", "elite", 23.62599369921746, 58.36712033090482, "Elite clinic is a clinic specialized in plastic surgery and hair transplant in the heart of Muscat. In our clinic we use the latest technique in plastic surgery and hair treatments. Elite clinic is a prestigious plastic surgery and hair transplant clinic in Oman", 9.50, "Plastic Surgery, Dental");
        clinicList.add(clinic);
        clinic = new Clinic("Muscat Private Clinic", "Al Khoudh", "muscat", 23.615937371989865, 58.18922688799782, "The new state-of-the-art clinic built upon our commitment to enhancing care and quality healthcare services to the people of Oman.\n" +
                "Muscat Private Clinics – Al Khoud, brings together a highly qualified team of experienced doctors and caregivers to provide healthcare of the highest international standards and offer one-stop solutions for consultations, treatments, therapies, procedures, laboratory, and comprehensive diagnostic imaging services to our patients, all under one roof.\n", 10.50, "Dental, Cosmetology, Internal Medicine");
        clinicList.add(clinic);
        clinic = new Clinic("Sandiane Clinic", "48 Way, Muscat", "sand", 23.59591083059052, 58.36306943865562, "We are SANDIANE Dermatology, Cosmetology & Laser Centre, state of the art specialized medical center for Dermatology & Cosmetology in Muscat, Sultanate of Oman in Al Azaibah.", 8.50, "Dermatology, Cosmetology");
        clinicList.add(clinic);
        clinic = new Clinic("EMC", "Seeb", "emc", 23.60662077593206, 58.45355824599797, "EMC prides itself in being the leading, highly respected and exclusive center in providing skin health, anti-aging, rejuvenation, Aesthetic Gynecology, dental services and Orthotdontics in Oman. Since founding our center in 2000, we have helped thousands of patients to improve their appearance with confidence. Even after the media attention, we are still proud that the majority of the new patients come from personal recommendations. We have a superb team of medical and support staff who are qualified and committed to providing you with the best care possible. Our first priority is the safety, comfort, privacy, well-being and satisfaction of our patients and clients. State of art equipment further supports us to meet your needs effectively. We are confident that with our dedicated staff and your valued support and feedback, EMC will continue to build a brand name known for excellence, to meet your personal needs. We presently have five branches across the Sultanate of Oman, Muscat, Mawalah, Salalah, Sohar and Al Mabilah. Our promise to you is to remain one of the leading skin health providers in the Sultanate of Oman, exceeding your expectations.", 8.50, "Dermatology, Dental, Cosmetology");
        clinicList.add(clinic);
        clinic = new Clinic("Clinica Joelle", "3012 Way, Muscat", "clinica", 23.606772659808698, 58.452979813411424, "Established in 2013, by the beauty icon Joelle Mardinian. Clinica Joelle is an aesthetic clinic that specializes in transforming lives using the latest aesthetic techniques, technology, and trends.\n" +
                "With a portfolio of experts in the field of aesthetics and beauty, Clinica Joelle aims to help clients look beautiful and feel confident through surgical and non-surgical solutions.\n", 11.00, "Plastic Surgery, Dental, Hair");
        clinicList.add(clinic);
        clinic = new Clinic("Eva Clinic", "18th November Street, Muscat", "eva", 23.602170202926292, 58.36431225524691, "Providing you with the best services on dermatology and Cosmetology by experienced doctors. Maintaining your skin with our leading medical brand (Image skincare) along with a special laser machines for skin rejuvenation, tightening and brightening.", 14.00, "Tanning, Dermatology, Cosmetology");
        clinicList.add(clinic);
        clinic = new Clinic("Dermaglow Clinic", "Muscat", "glow", 23.597300863530922, 58.35888217638193, "Dermaglow Clinic has expertise in treatment and prevention for all of your general dermatologic needs including all forms of acne, from \"blackheads\" to cystic acne. Our clinic uses both topical and oral medications as well as procedural modalities", 8.00, "Facial, Cosmetology, Chemical Peel");
        clinicList.add(clinic);
        clinic = new Clinic("American Speciality Center", "Muscat", "american", 23.59314445023211, 58.38498671555757, "The center is considered a pioneer in providing medical services in the United States of America in the state of Ohio and Kentucky, and we are proud to provide these distinguished services in the Sultanate of Oman as a leading center in providing advanced medicine with no less efficiency than our centers in the United States during the past fifteen years.\n" +
                "Our goal in general is to provide the health care that the family needs, by applying our high standards of quality, and that the specialized doctors and medical staff are fully qualified and with the highest qualifications. We care most about patients’ happiness and this is our ultimate goal.", 10.50, "Dental, Dermatology, Gynecology");
        clinicList.add(clinic);


        clinicAdapter.notifyDataSetChanged();
    }
}