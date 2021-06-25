package com.example.homework3.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.navigation.Navigation;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.homework3.FM.CollectFragmentDirections;
import com.example.homework3.FM.SearchFragmentDirections;
import com.example.homework3.R;
import com.example.homework3.object.CardData;

import java.util.List;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.cardViewHolder>{
    private List<CardData> cardlist;
    private LayoutInflater layoutInflater;


    public CardAdapter(List<CardData> cardlist, Context context) {
        this.cardlist = cardlist;
        this.layoutInflater = LayoutInflater.from(context);
    }

    class cardViewHolder extends RecyclerView.ViewHolder {
        private TextView txtTitle, txtLeft, txtRight;
        private ConstraintLayout itemLayout;
        public cardViewHolder(View view) {
            super(view);
            txtTitle = (TextView)itemView.findViewById(R.id.Title);
            txtLeft = (TextView)itemView.findViewById(R.id.Left);
            txtRight = (TextView)itemView.findViewById(R.id.Right);
            itemLayout = (ConstraintLayout)itemView.findViewById(R.id.oneRV);

            // SharedPreferences設定recylcerView的顏色
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(layoutInflater.getContext());
            if (sharedPreferences.getBoolean("checkToGray", false)) {
                txtTitle.setTextColor(Color.parseColor("white"));
                txtLeft.setTextColor(Color.parseColor("white"));
                txtRight.setTextColor(Color.parseColor("white"));
            } else {
                txtTitle.setTextColor(Color.parseColor("black"));
                txtLeft.setTextColor(Color.parseColor("black"));
                txtRight.setTextColor(Color.parseColor("black"));
            }
        }
    }

    @NonNull
    @Override
    public CardAdapter.cardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.rv_item, parent, false);
        return new cardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CardAdapter.cardViewHolder holder, int position) {

        String title = cardlist.get(position).getTitle();
        String left = cardlist.get(position).getLeft();
        String right = cardlist.get(position).getRight();
        String href = cardlist.get(position).getHref();
        String site = cardlist.get(position).getSite();
        boolean IsCollectFragment = cardlist.get(position).isCollectFragment();

        holder.txtTitle.setText(title);
        holder.txtLeft.setText(left);
        holder.txtRight.setText(right);

        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //因為是不同fragment，action會不同，其實抓的Content都一樣
                if (IsCollectFragment) {
                CollectFragmentDirections.ActionCollectFragmentToContentFragment action =
                        CollectFragmentDirections.actionCollectFragmentToContentFragment(title, left, right, href, site);
                    action.setTitle(title);
                    action.setLeft(left);
                    action.setRight(right);
                    action.setHref(href);
                    Navigation.findNavController(v).navigate(action);
                } else {
                    SearchFragmentDirections.ActionSearchFragmentToContentFragment action =
                            SearchFragmentDirections.actionSearchFragmentToContentFragment(title, left, right, href, site);
                    action.setTitle(title);
                    action.setLeft(left);
                    action.setRight(right);
                    action.setHref(href);
                    Navigation.findNavController(v).navigate(action);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return cardlist.size();
    }
}
