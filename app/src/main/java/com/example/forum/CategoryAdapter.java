package com.example.forum;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class CategoryAdapter extends BaseAdapter {

    private Context context;
    private List<String> categories;
    private Map<String, Integer> imageResourceMap;


    public CategoryAdapter(Context context, List<String> categories) {
        this.context = context;
        this.categories = categories;
        initializeImageResourceMap();
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Override
    public Object getItem(int position) {
        return categories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void initializeImageResourceMap() {
        imageResourceMap = new HashMap<>();
        imageResourceMap.put("Anime", R.drawable.anime);
        imageResourceMap.put("Game", R.drawable.game);
        imageResourceMap.put("VTuber", R.drawable.vtuber);
        imageResourceMap.put("General", R.drawable.general);
        imageResourceMap.put("Business", R.drawable.business);
        imageResourceMap.put("Social", R.drawable.social);
        imageResourceMap.put("Computer Science", R.drawable.computer_science);
        imageResourceMap.put("My Threads", R.drawable.my_threads);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.category_grid_item, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.categoryImage);
        TextView textView = convertView.findViewById(R.id.categoryName);

        String categoryName = categories.get(position);
        textView.setText(categoryName);

        Integer imageId = imageResourceMap.get(categoryName);
        if (imageId != null && imageId > 0) {
            imageView.setImageResource(imageId);
        } else {
            imageView.setImageResource(R.drawable.uwindsor_logo);
        }

        return convertView;
    }
}
