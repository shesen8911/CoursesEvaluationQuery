package com.example.homework3.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homework3.R;
import com.example.homework3.object.CollectData;

import java.util.List;

public class CollectAdapter extends RecyclerView.Adapter<CollectAdapter.collectViewHolder>{

    private List<CollectData> collectDataList;
    private LayoutInflater layoutInflater;

    public CollectAdapter(List<CollectData> collectDataList, LayoutInflater layoutInflater) {
        this.collectDataList = collectDataList;
        this.layoutInflater = layoutInflater;
    }

    class collectViewHolder extends RecyclerView.ViewHolder {
        private TextView txtTitle;
        private LinearLayout itemLayout;
        public collectViewHolder(View view) {
            super(view);
            txtTitle = (TextView)itemView.findViewById(R.id.title);
            itemLayout = (LinearLayout)itemView.findViewById(R.id.rvCollect);
        }
    }

    @NonNull
    @Override
    public collectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.rv_item, parent, false);
        return new collectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull collectViewHolder holder, int position) {
        String href = collectDataList.get(position).getHref();

        holder.txtTitle.setText(collectDataList.get(position).getTitle());

        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //navigation到contentFragment
            }
        });
    }

    @Override
    public int getItemCount() {
        return collectDataList.size();
    }
}
