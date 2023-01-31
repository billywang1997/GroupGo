package com.example.myapplication.add_location_management;

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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
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
import com.example.myapplication.MainActivity;
import com.example.myapplication.PlaceType;
import com.example.myapplication.R;
import com.example.myapplication.account_management.AccountSettingActivity;
import com.example.myapplication.account_management.HomePageFragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.CompletableObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AddLocationActivity extends AppCompatActivity {

    EditText addressEdit, nameEdit, reasonEdit;
    TextView foodTag, drinkTag, indoorTag, outdoorTag;
    TextView photoFrame;
    ImageView addressReselection;
    TextView postButton;

    String address, name, reason;
    PlaceType placeType;

    ImageView photoView1, photoView2, photoView3, photoView4, photoView5, photoView6, photoView7;

    List<Uri> uriList = new ArrayList<>();
    List<ImageView> imageViewList = new ArrayList<>();

    List<Bitmap> bitmapList = new ArrayList<>();

    int picNumber;
    Uri curUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_location);

        setTitle("Add Location");

        Intent intent = this.getIntent();
        double latitude = intent.getDoubleExtra("latitude", 0);
        double longitude = intent.getDoubleExtra("longitude", 0);

        String pinedAddress = intent.getStringExtra("address");
        String curName = intent.getStringExtra("name");

        addressEdit = findViewById(R.id.address_edit);
        nameEdit = findViewById(R.id.name_edit);

        final LocationDataHelper localLocationHelper = new RemoteLocationHelper();

        if (pinedAddress != null && !pinedAddress.isEmpty()) {
            addressEdit.setText(pinedAddress);
            addressEdit.setKeyListener(null);
        }

        if (curName != null && !curName.isEmpty()) {
            nameEdit.setText(curName);
        }


        foodTag = findViewById(R.id.tag_food);
        drinkTag = findViewById(R.id.tag_drink);
        indoorTag = findViewById(R.id.tag_indoor);
        outdoorTag = findViewById(R.id.tag_outdoor);

        photoFrame = findViewById(R.id.photo_frame);

        reasonEdit = findViewById(R.id.reason_frame);

        photoView1 = findViewById(R.id.photo1);
        photoView2 = findViewById(R.id.photo2);
        photoView3 = findViewById(R.id.photo3);
        photoView4 = findViewById(R.id.photo4);
        photoView5 = findViewById(R.id.photo5);
        photoView6 = findViewById(R.id.photo6);
        photoView7 = findViewById(R.id.photo7);

        imageViewList.add(photoView1);
        imageViewList.add(photoView2);
        imageViewList.add(photoView3);
        imageViewList.add(photoView4);
        imageViewList.add(photoView5);
        imageViewList.add(photoView6);
        imageViewList.add(photoView7);

        addressReselection = findViewById(R.id.address_reselection);
        postButton = findViewById(R.id.tv_post);

        photoFrame.setOnClickListener(v -> {
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
                        curUri = FileProvider.getUriForFile(AddLocationActivity.this,
                                "com.example.myapplication.provider", outputImage);

                        if (Build.VERSION.SDK_INT>22){
                            if (ContextCompat.checkSelfPermission(AddLocationActivity.this,
                                    android.Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){

                                ActivityCompat.requestPermissions(AddLocationActivity.this,
                                        new String[]{android.Manifest.permission.CAMERA}, 1);

                            } else {
                                if (picNumber < 7) {
                                    picNumber++;
                                    uriList.add(curUri);

                                    Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriList.get(uriList.size() - 1));
                                    startActivityForResult(photoIntent, 1);
                                } else {
                                    Toast.makeText(AddLocationActivity.this, "You can upload at most 7 photos", Toast.LENGTH_SHORT).show();
                                }

                            }
                        }else {

                        }



                    });

                    fromAlbumView.setOnClickListener(v2 -> {
                        dialog.dismiss();

                        if (ContextCompat.checkSelfPermission(AddLocationActivity.this,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(AddLocationActivity.this, new String[] {
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
                        } else {
                            openAlbum();
                        }



                    });
            dialog.setCanceledOnTouchOutside(true);
            dialog.setContentView(dialogView);
            dialog.show();

                });




        foodTag.setOnClickListener(v -> {
            placeType = PlaceType.FOOD;
            resetTags();

            foodTag.setTextColor(Color.parseColor("#FFFEFE"));
            foodTag.setBackground(getResources().getDrawable(R.drawable.bg_add_location));

        });

        drinkTag.setOnClickListener(v -> {
            placeType = PlaceType.DRINK;
            resetTags();

            drinkTag.setTextColor(Color.parseColor("#FFFEFE"));
            drinkTag.setBackground(getResources().getDrawable(R.drawable.bg_add_location));
        });

        indoorTag.setOnClickListener(v -> {
            placeType = PlaceType.INDOOR_ACTIVITY;
            resetTags();

            indoorTag.setTextColor(Color.parseColor("#FFFEFE"));
            indoorTag.setBackground(getResources().getDrawable(R.drawable.bg_add_location));
        });

        outdoorTag.setOnClickListener(v -> {
            placeType = PlaceType.OUTDOOR_ACTIVITY;
            resetTags();

            outdoorTag.setTextColor(Color.parseColor("#FFFEFE"));
            outdoorTag.setBackground(getResources().getDrawable(R.drawable.bg_add_location));
        });

        postButton.setOnClickListener(v -> {
            address = addressEdit.getText().toString().trim();
            name = nameEdit.getText().toString().trim();
            reason = reasonEdit.getText().toString().trim();

            StringBuilder photoListBase64 = new StringBuilder();

            for (Bitmap bitmap : bitmapList) {
                photoListBase64.append(bitmapToBase64(bitmap)).append(";newOne;");
            }

            boolean ifAnythingEmpty = TextUtils.isEmpty(address) || TextUtils.isEmpty(name) || TextUtils.isEmpty(reason);

            if (ifAnythingEmpty) {
                Toast.makeText(AddLocationActivity.this, "Please fill in address, name and recommended reason", Toast.LENGTH_SHORT).show();
            } else if (latitude == 0 && longitude == 0) {
                Toast.makeText(AddLocationActivity.this, "The latitude and longitude data is wrong", Toast.LENGTH_SHORT).show();
            } else {
                localLocationHelper.addLocation(HomePageFragment.curGroupNumber, name, address, HomePageFragment.curUser,
                        placeType != null ? placeType.toString().toLowerCase() : "", reason + ";newOneComment;",
                        photoListBase64 != null ? photoListBase64.toString() : null,
                        String.valueOf(longitude), String.valueOf(latitude))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new CompletableObserver() {
                            @Override
                            public void onSubscribe(Disposable d) {

                            }

                            @Override
                            public void onComplete() {
                                Toast.makeText(AddLocationActivity.this, "You have successfully added the new place", Toast.LENGTH_SHORT).show();

                                Intent newIntent = new Intent(AddLocationActivity.this, MainActivity.class)
                                        .putExtra("curUser", HomePageFragment.curUser)
                                        .putExtra("nickname", HomePageFragment.thisUser.nickname)
                                        .putExtra("user", HomePageFragment.thisUser)
                                        .putExtra("fragment_id", 2);
                                startActivity(newIntent);
                            }

                            @Override
                            public void onError(Throwable e) {
                                System.out.println("============ e ++++++++++        " + e);
                                Toast.makeText(AddLocationActivity.this, "There's something error", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 1:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(AddLocationActivity.this, "Get permission", Toast.LENGTH_SHORT).show();

                    if (picNumber < 7) {
                        picNumber++;
                        uriList.add(curUri);

                        Intent photoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriList.get(uriList.size() - 1));
                        startActivityForResult(photoIntent, 1);
                    } else {
                        Toast.makeText(AddLocationActivity.this, "You can upload at most 7 photos", Toast.LENGTH_SHORT).show();
                    }


                } else {
                    Toast.makeText(AddLocationActivity.this, "Refused to get permission", Toast.LENGTH_SHORT).show();
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
                if (resultCode == RESULT_OK) {
                    try {
                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uriList.get(uriList.size() - 1)));
                        ImageView curImageView = imageViewList.get(picNumber - 1);
                        curImageView.setImageBitmap(bitmap);
                        bitmapList.add(bitmap);
                        curImageView.setVisibility(View.VISIBLE);


                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
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

    private void openAlbum() {

        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, 2);
    }

    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        curUri = data.getData();

        if (picNumber < 7) {
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
            Toast.makeText(AddLocationActivity.this, "You can upload at most 7 photos", Toast.LENGTH_SHORT).show();
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
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            ImageView curImageView = imageViewList.get(picNumber - 1);
            curImageView.setImageBitmap(bitmap);
            bitmapList.add(bitmap);
            curImageView.setVisibility(View.VISIBLE);

        } else {
            Toast.makeText(this, "Failed to fetch image", Toast.LENGTH_SHORT).show();
        }
    }

    private void resetTags() {
        foodTag.setTextColor(Color.parseColor("#333232"));
        foodTag.setBackground(getResources().getDrawable(R.drawable.grey));

        drinkTag.setTextColor(Color.parseColor("#333232"));
        drinkTag.setBackground(getResources().getDrawable(R.drawable.grey));

        indoorTag.setTextColor(Color.parseColor("#333232"));
        indoorTag.setBackground(getResources().getDrawable(R.drawable.grey));

        outdoorTag.setTextColor(Color.parseColor("#333232"));
        outdoorTag.setBackground(getResources().getDrawable(R.drawable.grey));
    }

    // Start page
    public static void clickStart(Context context, String address, String name, double latitude, double longitude) {
        Intent intent = new Intent(context, AddLocationActivity.class);
        intent.putExtra("address", address);
        intent.putExtra("name", name);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        context.startActivity(intent);

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

    public void onBackPressed() {
        Intent intent = new Intent(AddLocationActivity.this, MainActivity.class);
        intent.putExtra("curUser", HomePageFragment.curUser);
        intent.putExtra("user", HomePageFragment.thisUser);
        intent.putExtra("nickname", HomePageFragment.thisUser.nickname);
        startActivity(intent);
    }



}
