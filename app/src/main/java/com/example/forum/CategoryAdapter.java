package com.example.forum;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

public class CategoryAdapter extends BaseAdapter {

    private Context context;
    private List<String> categories;

    public CategoryAdapter(Context context, List<String> categories) {
        this.context = context;
        this.categories = categories;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.category_grid_item, parent, false);
        }

        ImageView imageView = convertView.findViewById(R.id.categoryImage);
        TextView textView = convertView.findViewById(R.id.categoryName);

        String categoryName = categories.get(position);
        textView.setText(categoryName);

        // Here you'll set the image for the category. For example:
        // This is a placeholder. You'll need to implement your own way to map category names to images.
        int imageId = context.getResources().getIdentifier("drawable/" + categoryName.toLowerCase(), null, context.getPackageName());
        if (imageId > 0) {
            imageView.setImageResource(imageId);
        } else {
            // Default image if not found
            imageView.setImageResource(R.drawable.uwindsor_logo);
        }

        return convertView;
    }
}
