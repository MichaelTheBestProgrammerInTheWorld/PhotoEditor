package com.michaelmagdy.photoeditor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import jp.wasabeef.glide.transformations.gpu.BrightnessFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.ContrastFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.InvertFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.KuwaharaFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.PixelationFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.SepiaFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.SketchFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.SwirlFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.ToonFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.VignetteFilterTransformation;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView imageView, test;
    Button chooseBtn, saveBtn, toonBtn, sepiaBtn, contrastBtn, invertBtn,
            pixelBtn, sketchBtn, swirlBtn, brightnessBtn, kuwaharaBtn,
            vignetteBtn;
    LinearLayout filtersContainer;
    public static final int REQUEST_CODE = 100;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        test = findViewById(R.id.image_view_test);
    }

    private void initViews() {

        imageView = findViewById(R.id.image_view);
        chooseBtn = findViewById(R.id.choose_img_btn);
        saveBtn = findViewById(R.id.save_img_btn);
        toonBtn = findViewById(R.id.toon_filter_btn);
        sepiaBtn = findViewById(R.id.sepia_filter_btn);
        contrastBtn = findViewById(R.id.contrast_filter_btn);
        invertBtn = findViewById(R.id.invert_filter_btn);
        pixelBtn = findViewById(R.id.pixel_filter_btn);
        sketchBtn = findViewById(R.id.sketch_filter_btn);
        swirlBtn = findViewById(R.id.swirl_filter_btn);
        kuwaharaBtn = findViewById(R.id.kuwahara_filter_btn);
        vignetteBtn = findViewById(R.id.vignette_filter_btn);
        filtersContainer = findViewById(R.id.filters_container);
        filtersContainer.setVisibility(View.INVISIBLE);
        saveBtn.setEnabled(false);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){

            case R.id.choose_img_btn:
                selectPhoto();
                break;
            case R.id.save_img_btn:

                try {
                    saveImageToGallery();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("lastError", e.getMessage());
                }
                if (isPermissionGranted()) {

                }

                break;

            case R.id.toon_filter_btn:
                applyTransformation(new ToonFilterTransformation());
                break;
            case R.id.sepia_filter_btn:
                applyTransformation(new SepiaFilterTransformation());
                break;
            case R.id.contrast_filter_btn:
                applyTransformation(new ContrastFilterTransformation());
                break;
            case R.id.invert_filter_btn:
                applyTransformation(new InvertFilterTransformation());
                break;
            case R.id.pixel_filter_btn:
                applyTransformation(new PixelationFilterTransformation());
                break;
            case R.id.sketch_filter_btn:
                applyTransformation(new SketchFilterTransformation());
                break;
            case R.id.swirl_filter_btn:
                applyTransformation(new SwirlFilterTransformation());
                break;
            case R.id.brightness_filter_btn:
                applyTransformation(new BrightnessFilterTransformation());
                break;
            case R.id.kuwahara_filter_btn:
                applyTransformation(new KuwaharaFilterTransformation());
                break;
            case R.id.vignette_filter_btn:
                applyTransformation(new VignetteFilterTransformation());
                break;

        }
    }

    private void applyTransformation(Transformation<Bitmap> filterTransformation) {

        Glide.with(this).load(bitmap)
                .apply(RequestOptions.bitmapTransform(filterTransformation))
                .into(imageView);
    }

    private void selectPhoto() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK &&
        data != null) {

            Uri uri = data.getData();
            try {
                ParcelFileDescriptor parcelFileDescriptor =
                        getContentResolver().openFileDescriptor(uri, "r");
                FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
                bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);
                parcelFileDescriptor.close();
                imageView.setImageBitmap(bitmap);
                filtersContainer.setVisibility(View.VISIBLE);
                saveBtn.setEnabled(true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isPermissionGranted() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
        return true;
    }

    private void saveImageToGallery() throws IOException {

        BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();
        Bitmap bitmap = bitmapDrawable.getBitmap();
        //File filepath = Environment.getExternalStorageDirectory();
        File filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        //File filepath = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File dir = new File(filepath.getAbsolutePath()+"/" + getString(R.string.app_name)
        + "/");
        dir.mkdir();
        File file = new File(dir, System.currentTimeMillis() + ".jpg");
        FileOutputStream outputStream = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        Toast.makeText(this, "Image saved", Toast.LENGTH_SHORT).show();
        outputStream.flush();
        outputStream.close();

        //gallery
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(Uri.fromFile(file));
        sendBroadcast(intent);

        // Tell the media scanner about the new file so that it is
// immediately available to the user.
//        MediaScannerConnection.scanFile(this, new String[]{file.toString()}, null,
//                new MediaScannerConnection.OnScanCompletedListener() {
//                    public void onScanCompleted(String path, Uri uri) {
//                        Log.i("ExternalStorage", "Scanned " + path + ":");
//                        Log.i("ExternalStorage", "-> uri=" + uri);
//                    }
//                });
    }

}
