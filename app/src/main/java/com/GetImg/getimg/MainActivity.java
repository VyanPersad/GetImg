package com.GetImg.getimg;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableRow;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    Button getImg, save;
    ImageView theImage;
    TableRow tableView;
    //The commented out code is the older depreciated version
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getImg = findViewById(R.id.getImg);
        save = findViewById(R.id.save);
        theImage = findViewById(R.id.theImage);
        tableView = findViewById(R.id.tableView);

        getImg.setOnClickListener(view -> {
            Intent getImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            loadImage.launch(getImage);
            //Intent getImage = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            //startActivityForResult(getImage, 1);
        });

        save.setOnClickListener(view -> {
            View content = findViewById(R.id.tableView);
            Bitmap bitmap = getScreenShot(content);
            String Image = System.currentTimeMillis() + ".png";
            store(bitmap,Image);
        });
    }

    public Bitmap getScreenShot(View view){
            view.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
            view.setDrawingCacheEnabled(false);
            return bitmap;
    }

    public  void store(Bitmap bmp, String filename){
        String dirpath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString();
        File dir = new File(dirpath);
        if (!dir.exists()){
            dir.mkdir();
        }

        File file = new File(dirpath, filename);
        try {
            FileOutputStream fos;
            fos  = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG,100,fos);
            fos.flush();
            fos.close();
            Toast.makeText(this, "Saved" + getFilesDir().getAbsolutePath(), Toast.LENGTH_LONG).show();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,
            filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            ImageView theImage = findViewById(R.id.theImage);
            theImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }*/

    //Glide is the way to go
    ActivityResultLauncher <Intent> loadImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK){
                    Intent data = result.getData();
                    Uri selectedImage = data.getData();
                    ImageView theImage = findViewById(R.id.theImage);
                    Glide.with(this).load(selectedImage).into(theImage);
                    /*
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    cursor.close();*/

                    //theImage.setImageBitmap(BitmapFactory.decodeFile(picturePath));
                    /*BitmapFactory.Options options = new BitmapFactory.Options();
                    Bitmap Image = BitmapFactory.decodeFile(picturePath, options);
                    Image = Bitmap.createScaledBitmap(Image, 25, 25, true);
                    theImage.setImageBitmap(Image);*/
                }
            }
    );

}