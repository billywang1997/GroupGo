package com.example.myapplication.view_place;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.myapplication.LocalDatabase.MyDatabase;
import com.example.myapplication.R;
import com.example.myapplication.account_management.HomePageFragment;
import com.example.myapplication.add_location_management.AddLocationActivity;
import com.example.myapplication.add_location_management.LocationDataHelper;
import com.example.myapplication.add_location_management.RemoteLocationHelper;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import io.reactivex.CompletableObserver;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AddPhotoActivity extends AppCompatActivity {
    TextView photoView;
    TextView nameView, addressView, typeView, post;

    ImageView photoView1, photoView2, photoView3, photoView4;

    List<Uri> uriList = new ArrayList<>();
    List<ImageView> imageViewList = new ArrayList<>();

    List<Bitmap> bitmapList = new ArrayList<>();

    int picNumber;
    Uri curUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_photo);

        setTitle("Add Photos");

        photoView = findViewById(R.id.photo_frame);

        nameView = findViewById(R.id.place_name);
        addressView = findViewById(R.id.address);
        typeView = findViewById(R.id.type);
        post = findViewById(R.id.post);

        photoView1 = findViewById(R.id.photo1);
        photoView2 = findViewById(R.id.photo2);
        photoView3 = findViewById(R.id.photo3);
        photoView4 = findViewById(R.id.photo4);

        imageViewList.add(photoView1);
        imageViewList.add(photoView2);
        imageViewList.add(photoView3);
        imageViewList.add(photoView4);


        Intent intent = this.getIntent();
        String placeName = intent.getStringExtra("place_name");
        String placeAddress = intent.getStringExtra("place_address");
        String placeType = intent.getStringExtra("place_type");

        nameView.setText(placeName);
        addressView.setText(placeAddress);

        if (placeType == null || placeType.equals("")) {
            typeView.setVisibility(View.INVISIBLE);
        } else {
            typeView.setText("· " + placeType.substring(0, 1).toUpperCase() +
                    placeType.substring(1).toLowerCase());
        }


        final LocationDataHelper localLocationHelper = new RemoteLocationHelper();

        photoView.setOnClickListener(v -> {
            final BottomSheetDialog dialog = new BottomSheetDialog(this);
            View dialogView = LayoutInflater.from(this).inflate(R.layout.photo_bottom_dialog, null);
            TextView takePhotoView = dialogView.findViewById(R.id.take_photo);
            TextView fromAlbumView = dialogView.findViewById(R.id.get_from_album);

            takePhotoView.setOnClickListener(v1 -> {
                dialog.dismiss();

                File outputImage = new File(getExternalCacheDir(), "output_image.jpg");
                try {
                    if (outputImage.exists()) {
                        outputImage.delete();
                    }
                    outputImage.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                curUri = FileProvider.getUriForFile(AddPhotoActivity.this,
                        "com.example.myapplication.provider", outputImage);

                if (Build.VERSION.SDK_INT > 22){
                    if (ContextCompat.checkSelfPermission(AddPhotoActivity.this,
                            android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

                        ActivityCompat.requestPermissions(AddPhotoActivity.this,
                                new String[]{android.Manifest.permission.CAMERA}, 1);

                    } else {
                        if (picNumber < 4) {
                            picNumber++;
                            uriList.add(curUri);

                            Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriList.get(uriList.size() - 1));
                            startActivityForResult(photoIntent, 1);
                        } else {
                            Toast.makeText(AddPhotoActivity.this, "You can upload at most 4 photos", Toast.LENGTH_SHORT).show();
                        }

                    }
                }else {

                }



            });

            fromAlbumView.setOnClickListener(v2 -> {
                dialog.dismiss();

                if (ContextCompat.checkSelfPermission(AddPhotoActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddPhotoActivity.this, new String[] {
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                } else {
                    openAlbum();
                }



            });
            dialog.setCanceledOnTouchOutside(true);
            dialog.setContentView(dialogView);
            dialog.show();
        });

        post.setOnClickListener(v -> {

            localLocationHelper.getPhotos(HomePageFragment.curGroupNumber, placeName, placeAddress)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<String>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onSuccess(String s) {
                            StringBuilder photoListBase64 = new StringBuilder();

                            for (Bitmap bitmap : bitmapList) {
                                photoListBase64.append(bitmapToBase64(bitmap)).append(";newOne;");
                            }

                            if (photoListBase64 == null || photoListBase64.toString().equals("") || photoListBase64.toString().isEmpty()) {
                                Toast.makeText(AddPhotoActivity.this, "Please upload at least one photo", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.i("AddPhotoActivity", "addNewPhotos photos: " + photoListBase64);
                                localLocationHelper.addNewPhotos(HomePageFragment.curGroupNumber, placeName, placeAddress, s + photoListBase64.toString())
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new CompletableObserver() {
                                            @Override
                                            public void onSubscribe(Disposable d) {

                                            }

                                            @Override
                                            public void onComplete() {
                                                Toast.makeText(AddPhotoActivity.this, "Your new photo has been added", Toast.LENGTH_SHORT).show();

                                                Intent newIntent = new Intent(AddPhotoActivity.this, PlaceDetailActivity.class);
                                                newIntent.putExtra("place_name", placeName);
                                                newIntent.putExtra("place_address", placeAddress);
                                                newIntent.putExtra("place_type", placeType);
                                                startActivity(newIntent);

                                                AddPhotoActivity.this.finish();
                                            }

                                            @Override
                                            public void onError(Throwable e) {

                                            }
                                        });
                            }
                        }

                        @Override
                        public void onError(Throwable e) {

                            StringBuilder photoListBase64 = new StringBuilder();

                            for (Bitmap bitmap : bitmapList) {
                                photoListBase64.append(bitmapToBase64(bitmap)).append(";newOne;");
                            }

                            if (photoListBase64 == null || photoListBase64.toString().equals("") || photoListBase64.toString().isEmpty()) {
                                Toast.makeText(AddPhotoActivity.this, "Please upload at least one photo", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.i("AddPhotoActivity", "addNewPhotos photos: " + photoListBase64);
                                localLocationHelper.addNewPhotos(HomePageFragment.curGroupNumber, placeName, placeAddress, "" + photoListBase64.toString())
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new CompletableObserver() {
                                            @Override
                                            public void onSubscribe(Disposable d) {

                                            }

                                            @Override
                                            public void onComplete() {
                                                Toast.makeText(AddPhotoActivity.this, "Your new photo has been added", Toast.LENGTH_SHORT).show();

                                                Intent newIntent = new Intent(AddPhotoActivity.this, PlaceDetailActivity.class);
                                                newIntent.putExtra("place_name", placeName);
                                                newIntent.putExtra("place_address", placeAddress);
                                                newIntent.putExtra("place_type", placeType);
                                                startActivity(newIntent);

                                                AddPhotoActivity.this.finish();
                                            }

                                            @Override
                                            public void onError(Throwable e) {

                                            }
                                        });
                            }

                        }
                    });



        });



    }

    private void openAlbum() {

        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, 2);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(AddPhotoActivity.this, "Get permission", Toast.LENGTH_SHORT).show();

                    if (picNumber < 4) {
                        picNumber++;
                        uriList.add(curUri);

                        Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriList.get(uriList.size() - 1));
                        startActivityForResult(photoIntent, 1);
                    } else {
                        Toast.makeText(AddPhotoActivity.this, "You can upload at most 4 photos", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(AddPhotoActivity.this, "Refused to get permission", Toast.LENGTH_SHORT).show();
                }
                break;

            case 2:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied this permission", Toast.LENGTH_SHORT).show();
                }
                break;

            default:
                break;
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if(resultCode != RESULT_CANCELED){
                    if (resultCode == RESULT_OK) {
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(uriList.get(uriList.size() - 1));
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            byte[] buffer = new byte[1024];
                            int len;
                            while ((len = inputStream.read(buffer)) > -1 ) {
                                baos.write(buffer, 0, len);
                            }
                            baos.flush();
                            InputStream is1 = new ByteArrayInputStream(baos.toByteArray());
                            InputStream is2 = new ByteArrayInputStream(baos.toByteArray());
                            int orientation = 0;
                            try {
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                    ExifInterface exif = new ExifInterface(is1);
                                    orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                                    Log.i("AddPhotoActivity", "case 1, orientation: " + orientation);
                                }
                            } catch (IOException e) {
                                Log.e("AddPhotoActivity", "case 1, ExifInterface error: " + e);
                            }
                            Bitmap bitmap = BitmapFactory.decodeStream(is2);
                            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                                Matrix matrix = new Matrix();
                                matrix.postRotate(90);
                                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                            }
                            ImageView curImageView = imageViewList.get(picNumber - 1);
                            curImageView.setImageBitmap(bitmap);
                            bitmapList.add(bitmap);
                            curImageView.setVisibility(View.VISIBLE);


                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                break;

            case 2:
                if (resultCode == RESULT_OK) {
                    handleImageOnKitKat(data);
                }

            default:
                break;
        }
    }


    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        curUri = data.getData();

        if (picNumber < 4) {
            picNumber++;
            uriList.add(curUri);


            if (DocumentsContract.isDocumentUri(this, curUri)) {
                String docId = DocumentsContract.getDocumentId(curUri);
                if ("com.android.providers.media.documents".equals(curUri.getAuthority())) {
                    String id = docId.split(":")[1];
                    String selection = MediaStore.Images.Media._ID + "=" + id;
                    imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);

                } else if ("com.android.providers.downloads.documents".equals(curUri.getAuthority())) {
                    Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                    imagePath = getImagePath(contentUri, null);

                }
            } else if ("content".equalsIgnoreCase(curUri.getScheme())) {
                imagePath = getImagePath(curUri, null);
            } else if ("file".equalsIgnoreCase(curUri.getScheme())) {
                imagePath = curUri.getPath();
            }
            displayImage(imagePath);
        } else {
            Toast.makeText(AddPhotoActivity.this, "You can upload at most 4 photos", Toast.LENGTH_SHORT).show();
        }

    }

    @SuppressLint("Range")
    private String getImagePath(Uri uri, String selection) {
        String path = null;
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();

        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            int orientation = 0;
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    ExifInterface exif = new ExifInterface(imagePath);
                    orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                    Log.i("AddPhotoActivity", "case 1, orientation: " + orientation);
                }
            } catch (IOException e) {
                Log.e("AddPhotoActivity", "case 1, ExifInterface error: " + e);
            }
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            }
            ImageView curImageView = imageViewList.get(picNumber - 1);
            curImageView.setImageBitmap(bitmap);
            bitmapList.add(bitmap);
            curImageView.setVisibility(View.VISIBLE);

        } else {
            Toast.makeText(this, "Failed to fetch image", Toast.LENGTH_SHORT).show();
        }
    }



    private static String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                Matrix matrix = new Matrix();
                float ratio = 550f / bitmap.getHeight();
                if (ratio < 1) {
                    matrix.setScale(ratio, ratio);
                }
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);




                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}
