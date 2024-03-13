package com.example.incidentreportingapp;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfGenerator {

    private static final int REQUEST_WRITE_STORAGE = 1;

    public static void saveRelativeLayoutAsPdf(Activity activity, RelativeLayout relativeLayout) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Request the permission if not granted
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
            return;
        }

        // Take a screenshot of the RelativeLayout
        Bitmap bitmap = getBitmapFromView(relativeLayout);

        // Load the screenshot as a Bitmap using Glide
        Glide.with(activity)
                .asBitmap()
                .load(bitmap)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                        // Create the output file path
                        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                        File pdfFile = new File(downloadsDir, "layout_to_pdf.pdf");

                        try {
                            // Create a Document with default page size (A4)
                            Document document = new Document(PageSize.A4);

                            // Create a PdfWriter to write the document to the file
                            PdfWriter.getInstance(document, new FileOutputStream(pdfFile));

                            // Open the document for writing
                            document.open();

                            // Convert the Bitmap to a byte array in PNG format
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                            byte[] byteArray = stream.toByteArray();

                            // Create an Image from the byte array
                            Image image = Image.getInstance(byteArray);

                            // Scale the image to fit the page if needed
                            image.scaleToFit(document.getPageSize().getWidth(), document.getPageSize().getHeight());

                            // Add the image to the PDF
                            document.add(image);

                            // Close the document
                            document.close();

                            showResultMessage(activity, "PDF saved.");
                        } catch (IOException e) {
                            e.printStackTrace();
                            showResultMessage(activity, "Error saving PDF: IOException");
                        } catch (Exception e) {
                            e.printStackTrace();
                            showResultMessage(activity, "Error saving PDF");
                        }
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        // Handle the case when image loading fails
                        showResultMessage(activity, "Error loading image from screenshot");
                    }
                });
    }

    private static Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private static void showResultMessage(Activity activity, String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }
}
