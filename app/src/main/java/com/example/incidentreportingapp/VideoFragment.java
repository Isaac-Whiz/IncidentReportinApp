package com.example.incidentreportingapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link VideoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VideoFragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private VideoView videoView;
    private Button btnAddVideo;
    private static final int REQUEST_VIDEO_CAPTURE = 1;
    private static final int REQUEST_PICK_VIDEO = 2;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public VideoFragment() {
        // Required empty public constructor
    }

    public static VideoFragment newInstance(String param1, String param2) {
        VideoFragment fragment = new VideoFragment();
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
    private void initViews(View view){
        btnAddVideo = view.findViewById(R.id.btnAddVideo);
        videoView = view.findViewById(R.id.videoView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        initViews(view);
        btnAddVideo.setOnClickListener(onClick -> {
            pickVideo();
        });

        btnAddVideo.setOnLongClickListener(longClick -> {
            recordVideo();
            return true;
        });
        // Inflate the layout for this fragment
        return view;
    }
    private void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivityForResult(intent, REQUEST_VIDEO_CAPTURE);
        }
    }

    private void pickVideo() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_PICK_VIDEO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == REQUEST_VIDEO_CAPTURE) {
                Uri videoUri = data.getData();
                Toast.makeText(requireContext(), "Video recorded: " + videoUri.toString(), Toast.LENGTH_SHORT).show();
                displayVideo(videoUri);
            } else if (requestCode == REQUEST_PICK_VIDEO) {
                Uri videoUri = data.getData();
                Toast.makeText(requireContext(), "Video picked: " + videoUri.toString(), Toast.LENGTH_SHORT).show();
                displayVideo(videoUri);
            }
        }
    }
    private void displayVideo(Uri videoUri) {
        videoView.setVideoURI(videoUri);
        videoView.requestFocus();
        videoView.start();
    }
}