package com.example.nexby;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.CompanyViewHolder> {

    private Context context;
    private List<Company> companies;

    public CompanyAdapter(Context context, List<Company> companies) {
        this.context = context;
        this.companies = companies;
    }

    @Override
    public CompanyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_company_card, parent, false);
        return new CompanyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CompanyViewHolder holder, int position) {
        Company company = companies.get(position);
        holder.companyNameText.setText(company.getName());

        // Get the distance and set it in the TextView
        String distanceText = "Од тебе на: " + String.format("%.0f", company.getDistance()) + "m";
        holder.companyDistanceText.setText(distanceText);
    }

    @Override
    public int getItemCount() {
        return companies.size();
    }

    public static class CompanyViewHolder extends RecyclerView.ViewHolder {
        TextView companyNameText;
        TextView companyDistanceText;  // Declare this as TextView

        public CompanyViewHolder(View itemView) {
            super(itemView);
            companyNameText = itemView.findViewById(R.id.companyName);
            companyDistanceText = itemView.findViewById(R.id.companyDistance);  // Initialize the TextView for distance
        }
    }
}
