package com.example.incidentreportingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Report extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ReportAdapter reportAdapter;
    private Button btnPrint;
    private ArrayList<ReportedData> reportedDataArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
       initViews();
        recyclerView.setAdapter(reportAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(Report.this));
        retrieveData();
        printReport();
    }
    private void initViews(){
        recyclerView = findViewById(R.id.recyclerView);
        btnPrint = findViewById(R.id.btnPrint);
        reportAdapter = new ReportAdapter();
        reportedDataArrayList = new ArrayList<>();
    }
    private void retrieveData(){
        ReportedData reportedData = new ReportedData();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Report backups");
//        databaseReference.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                reportedDataArrayList.clear();
//                for (DataSnapshot entrySnapshot : snapshot.getChildren()){
////                    String author = Objects.requireNonNull(entrySnapshot.child("author").getValue()).toString();
////                    String category = Objects.requireNonNull(entrySnapshot.child("category").getValue()).toString();
////                    String time = Objects.requireNonNull(entrySnapshot.child("currentTimeAndDate").getValue()).toString();
////                    String description = Objects.requireNonNull(entrySnapshot.child("description").getValue()).toString();
////                    String imagePaths = Objects.requireNonNull(entrySnapshot.child("imagePaths").getValue()).toString();
////                    String location = Objects.requireNonNull(entrySnapshot.child("location").getValue()).toString();
////                    String videoUrl = Objects.requireNonNull(entrySnapshot.child("videoUrl").getValue()).toString();
//
//String author = entrySnapshot.child("author").getValue(String.class);
//                    String category = entrySnapshot.child("category").getValue(String.class);
//                    String time = entrySnapshot.child("currentTimeAndDate").getValue(String.class);
//                    String description = entrySnapshot.child("description").getValue(String.class);
//                    String imagePaths = entrySnapshot.child("imagePaths").getValue(String.class);
//                    String location = entrySnapshot.child("location").getValue(String.class);
//                    String videoUrl = entrySnapshot.child("videoUrl").getValue(String.class);
//
//
//
//                    reportedData.setAuthor(author);
//                    reportedData.setCategory(category);
//                    reportedData.setCurrentTimeAndDate(time);
//                    reportedData.setDescription(description);
//                    reportedData.setImagePaths(imagePaths);
//                    reportedData.setLocation(location);
//                    reportedData.setVideoUrl(videoUrl);
//
//                    reportedDataArrayList.add(reportedData);
//                }
//                reportAdapter.setReports(reportedDataArrayList);
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        });




        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                reportedDataArrayList.clear();
                for (DataSnapshot entrySnapshot : snapshot.getChildren()) {
                    // Fetch individual values from each entry
                    String author = entrySnapshot.child("author").getValue(String.class);
                    String category = entrySnapshot.child("category").getValue(String.class);
                    String time = entrySnapshot.child("currentTimeAndDate").getValue(String.class);
                    String description = entrySnapshot.child("description").getValue(String.class);
                    String imagePaths = entrySnapshot.child("imagePaths").getValue(String.class);
                    String location = entrySnapshot.child("location").getValue(String.class);
                    String videoUrl = entrySnapshot.child("videoUrl").getValue(String.class);



                    reportedData.setAuthor(author);
                    reportedData.setCategory(category);
                    reportedData.setCurrentTimeAndDate(time);
                    reportedData.setDescription(description);
                    reportedData.setImagePaths(imagePaths);
                    reportedData.setLocation(location);
                    reportedData.setVideoUrl(videoUrl);

                    reportedDataArrayList.add(reportedData);
                }
                reportAdapter.setReports(reportedDataArrayList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void printReport(){
        btnPrint.setOnClickListener(view -> {
            RelativeLayout parentActivity = findViewById(R.id.parentRelativeLayout);
            PdfGenerator.saveRelativeLayoutAsPdf(Report.this, parentActivity);
            exitActivity();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
       exitActivity();
    }
    private void exitActivity(){
        startActivity(new Intent(this, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}