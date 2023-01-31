package com.example.myapplication.place_detail_adapter;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.List;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoAdapter.ViewHolder> {
    private List<PhotoItem> photosList;


    static class ViewHolder extends RecyclerView.ViewHolder {

        View itemView;
        ImageView photoView;

        public ViewHolder(View view) {
            super(view);
            itemView = view;
            photoView = view.findViewById(R.id.photo_show);
        }

    }

    public PhotoAdapter (List<PhotoItem> photoList) {
        photosList = photoList;
    }

    @Override
    public PhotoAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.photo_exhibit, parent, false);
        PhotoAdapter.ViewHolder holder = new PhotoAdapter.ViewHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAbsoluteAdapterPosition();
                PhotoItem item = photosList.get(position);
                // PlaceDetailActivity.clickStart(holder.itemView.getContext(), position + 1);
            }
        });

        holder.photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAbsoluteAdapterPosition();
                PhotoItem item = photosList.get(position);

                Bitmap curBitmap = item.getImg();
                bigImageLoader(curBitmap, view);

            }
        });


        return holder;
    }

    @Override
    public void onBindViewHolder(PhotoAdapter.ViewHolder holder, int position) {
        PhotoItem item = photosList.get(position);
        holder.photoView.setImageBitmap(item.getImg());
    }

    @Override
    public int getItemCount() {
        return photosList.size();
    }

    private void bigImageLoader(Bitmap bitmap, View view) {
        final Dialog dialog = new Dialog(view.getContext());
        ImageView image = new ImageView(view.getContext());

        Bitmap mBitmap = Bitmap.createScaledBitmap(bitmap,(int)(bitmap.getWidth() * 1.6),
                (int)(bitmap.getHeight() * 1.6), false);


        image.setImageBitmap(mBitmap);
        dialog.setContentView(image);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.show();
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });
    }


}
