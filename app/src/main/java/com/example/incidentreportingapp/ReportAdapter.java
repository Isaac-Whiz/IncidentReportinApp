package com.example.incidentreportingapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ReportAdapter extends RecyclerView.Adapter<ViewHolder>{
    private ArrayList<ReportedData> reportedData = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtAuthorReport.setText(reportedData.get(position).getAuthor());
        holder.txtCategoryReport.setText(reportedData.get(position).getCategory());
        holder.txtTimeReport.setText(reportedData.get(position).getCurrentTimeAndDate());
        holder.txtDescriptionReport.setText(reportedData.get(position).getDescription());
        holder.txtImagePathsReport.setText(reportedData.get(position).getImagePaths());
        holder.txtLocationReport.setText(reportedData.get(position).getLocation());
        holder.txtVideoUrlReport.setText(reportedData.get(position).getVideoUrl());

    }

    @Override
    public int getItemCount() {
        return reportedData.size();
    }

    public void setReports(ArrayList<ReportedData> reportedDataArrayList) {
        this.reportedData = reportedDataArrayList;
        notifyDataSetChanged();
    }
}
     class ViewHolder extends RecyclerView.ViewHolder{
    TextView txtAuthorReport, txtCategoryReport, txtTimeReport, txtDescriptionReport,
                txtImagePathsReport, txtLocationReport, txtVideoUrlReport;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtAuthorReport = itemView.findViewById(R.id.txtAuthorReport);
            txtCategoryReport = itemView.findViewById(R.id.txtCategoryReport);
            txtTimeReport = itemView.findViewById(R.id.txtTimeReport);
            txtDescriptionReport = itemView.findViewById(R.id.txtDescriptionReport);
            txtImagePathsReport = itemView.findViewById(R.id.txtImagePathsReport);
            txtLocationReport = itemView.findViewById(R.id.txtLocationReport);
            txtVideoUrlReport = itemView.findViewById(R.id.txtVideoUrlReport);
        }
    }

