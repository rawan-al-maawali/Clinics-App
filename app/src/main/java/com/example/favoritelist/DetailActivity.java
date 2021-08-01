package com.example.favoritelist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class DetailActivity extends AppCompatActivity {
    public static final String BOOK_STORE_OBJECT_MAPS = "BookStoreObjectMaps";
    //making Book class global
    private Clinic clinic;
    private int noOfSessions;
    private double subTotal;
    private TextView tvSearchFor;
    private Spinner spSearchFor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //get data from MainActivity
        clinic = (Clinic) getIntent().getSerializableExtra(MainActivity.Clinic_OBJECT);
        if(clinic ==null) {
            Toast.makeText(this, "Data is not currently available", Toast.LENGTH_SHORT).show();
            return;
        }
        setInitialData(clinic);
        processSpinner();


        tvSearchFor = findViewById(R.id.tvSearchFor);
        spSearchFor = findViewById(R.id.spSearchFor);
        tvSearchFor.setVisibility(View.GONE);
        spSearchFor.setVisibility(View.GONE);




    }
    //Method to process the spinner for the number of books
    private void processSpinner() {
        Spinner spinner = processSpinnerCommon(R.id.spinnerNoOfSessionsDetail, R.array.numbers_array);

        //Set a listener into the spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                //find the number of books wanted by the user
                noOfSessions = Integer.parseInt(adapterView.getItemAtPosition(position) + "");
                //find the total price of the books
                subTotal = noOfSessions * clinic.getSessionPrice();
                //show on activity Detail layout
                TextView tvSubtotal = findViewById(R.id.tvSessionSubTotalValueDetail);
                tvSubtotal.setText(subTotal + " OMR");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




    }

    private Spinner processSpinnerCommon(int spinnerType, int array) {
        Spinner spinner = findViewById(spinnerType);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //set the spinner to the adapter
        spinner.setAdapter(adapter);
        return spinner;
    }

    //Method to initialize data
    private void setInitialData(Clinic clinic) {
        //setting the title of the page to the name of the book
        setTitle(clinic.getClinicName());
        //get the views from activity_detail.xml
        TextView tvClinicNameDetail = findViewById(R.id.tvClinicNameDetail);
        TextView tvClinicTypeDetail = findViewById(R.id.tvClinicTypeDetail);
        TextView tvClinicLocationDetail = findViewById(R.id.tvClinicLocationDetail);
        ImageView ivClinicImageDetail = findViewById(R.id.ivClinicImageDetail);
        TextView tvDescriptionDetail = findViewById(R.id.tvDescriptionDetail);
        TextView tvPriceDetail = findViewById(R.id.tvPriceDetail);
        //set value in the views
        tvClinicNameDetail.setText(clinic.getClinicName());
        tvClinicLocationDetail.setText(clinic.getClinicLocation());
        tvClinicTypeDetail.setText(clinic.getClinicType());
        int resId = getResources().getIdentifier(clinic.getClinicImage(), "drawable", getPackageName());
        ivClinicImageDetail.setImageResource(resId);
        // int resId2 = getResources().getIdentifier(bookStore.getBookRating(), "drawable", getPackageName());
        //ivBookRatingDetail.setImageResource(resId2);
        tvDescriptionDetail.setText(clinic.getClinicDesc());
        tvPriceDetail.setText(clinic.getSessionPrice() + " OMR");
    }
    //To enable notification
    public void bookSession(View view) {
        NotificationChannel channel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channel = new NotificationChannel("1", "ClinicChannel", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Book Channel Description");
        }
        NotificationManager notificationManager = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationManager = getSystemService(NotificationManager.class);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel);
        }
        String message = "Thank you for booking " + noOfSessions + " sessions to " + clinic.getClinicName() + " from our app for a price of " + subTotal + " OMR";

        PendingIntent smsPendingIntent = sendSMS(message);
        PendingIntent emailPendingIntent = sendEmail(message);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.ic_launcher_book_foreground)
                .setContentTitle(clinic.getClinicName())
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .addAction(R.drawable.ic_baseline_sms_24, getString(R.string.sms), smsPendingIntent)
                .addAction(R.drawable.ic_baseline_email_24, getString(R.string.sendEmail), emailPendingIntent)
                .setAutoCancel(true);
        notificationManager.notify(100, builder.build());
    }

    private PendingIntent sendEmail(String message) {
        String[] to = {"17-0024@student.gutech.edu.om"};
        String[] cc = {"rawan.almaawali124@takatufscholars.om"};
        String[] bcc = {"17-0024@student.gutech.edu.om"};
        String email_subject = "Notification from " + clinic.getClinicName() + "Bookstore";
        String emailBody = "Dear Bookworm\n" +
                message+"\n" + clinic.getClinicDesc()
                + "\n Best Regards,\n" + "\n" + clinic.getClinicName();
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO)
                .setData(Uri.parse("mailto:"))
                .setType("text/plain")
                .putExtra(Intent.EXTRA_EMAIL, to)
                .putExtra(Intent.EXTRA_CC, cc)
                .putExtra(Intent.EXTRA_BCC, bcc)
                .putExtra(Intent.EXTRA_SUBJECT, email_subject)
                .putExtra(Intent.EXTRA_TEXT, emailBody);

        TaskStackBuilder stackBuilderEmail = TaskStackBuilder.create(this);
        stackBuilderEmail.addNextIntentWithParentStack(emailIntent);
        return stackBuilderEmail.getPendingIntent(98, PendingIntent.FLAG_UPDATE_CURRENT);



    }

    private PendingIntent sendSMS(String message) {
        String phoneNumber = "0096893225347";
        Uri uri = Uri.parse("smsto:" + phoneNumber);
        Intent SMSintent = new Intent(Intent.ACTION_SENDTO, uri);
        SMSintent.putExtra("sms_body", message);

        TaskStackBuilder stackBuilderSMS = TaskStackBuilder.create(this);
        stackBuilderSMS.addNextIntentWithParentStack(SMSintent);
        return stackBuilderSMS.getPendingIntent(99, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    //map internal fragments
    public void showOnMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra(BOOK_STORE_OBJECT_MAPS, clinic);
        if(intent.resolveActivity(getPackageManager()) != null )
            startActivity(intent);
    }
    //google maps
    public void showOnGoogleMaps(View view) {
        Uri uri = Uri.parse("geo:" + clinic.getLatitude()+"," + clinic.getLongitude());
        runGoogleMaps(uri);

    }
    //navigate in google maps
    public void navigate(View view) {
        Uri uri = Uri.parse("google.navigation:q=" + clinic.getLatitude()+"," + clinic.getLongitude());
        runGoogleMaps(uri);
    }

    private void runGoogleMaps(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
    }
    //panorama
    public void displayPanorama(View view) {
        Uri uri = Uri.parse("google.streetview:cbll=" + clinic.getLatitude()+"," + clinic.getLongitude());
        runGoogleMaps(uri);
    }
    //Categorial Search
    public void searchCategory(View view) {
        tvSearchFor.setVisibility(View.VISIBLE);
        spSearchFor.setVisibility(View.VISIBLE);

        Spinner spinner = processSpinnerCommon(R.id.spSearchFor, R.array.exampleSearches_array);

        //Set a listener into the spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                String searchQuery = adapterView.getItemAtPosition(i).toString();
                if(!searchQuery.contains("select")) {
                    Uri uri = Uri.parse("geo:" + clinic.getLatitude()+"," + clinic.getLongitude() + "?q=" + searchQuery);
                    runGoogleMaps(uri);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }
}