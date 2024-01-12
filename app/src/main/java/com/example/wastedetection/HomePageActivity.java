package com.example.wastedetection;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.etebarian.meowbottomnavigation.MeowBottomNavigation;
import com.example.wastedetection.ml.May31Model;
import com.example.wastedetection.ml.TfliteModelSchool;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.support.common.ops.NormalizeOp;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.Rot90Op;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;


public class    HomePageActivity extends AppCompatActivity {

    ScrollView home, image_search,info;
    Button scanbutton, selectbutton, predictbutton;
    ImageView imageView;
    Bitmap bitmap;
    TextView textView, welcome_text, home_text, info_text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        MeowBottomNavigation bottomNavigation = findViewById(R.id.bottomNavigation);
        home = findViewById(R.id.content_home);
        image_search = findViewById(R.id.content_image);
        info = findViewById(R.id.content_info);
        welcome_text = findViewById(R.id.welcome_text);
        home_text = findViewById(R.id.home_text);
        info_text = findViewById(R.id.info_text);

        scanbutton = findViewById(R.id.scanbutton);
        selectbutton = findViewById(R.id.selectbutton);
        predictbutton = findViewById(R.id.predictbutton);
        imageView = findViewById(R.id.predict);
        textView = findViewById(R.id.result_view);


        bottomNavigation.show(1,true);

        bottomNavigation.add(new MeowBottomNavigation.Model(1,R.drawable.baseline_home_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(2,R.drawable.baseline_image_search_24));
        bottomNavigation.add(new MeowBottomNavigation.Model(3,R.drawable.baseline_info_24));

        bottomNavigation.setOnClickMenuListener(model -> {
            // YOUR CODES

            switch (model.getId()){

                case 1:
                    home.setVisibility(View.VISIBLE);
                    image_search.setVisibility(View.GONE);
                    info.setVisibility(View.GONE);
                    break;
                case 2:
                    home.setVisibility(View.GONE);
                    image_search.setVisibility(View.VISIBLE);
                    info.setVisibility(View.GONE);
                    break;
                case 3:
                    home.setVisibility(View.GONE);
                    image_search.setVisibility(View.GONE);
                    info.setVisibility(View.VISIBLE);
                    break;
            }
            switch (model.getId()){

                case 1:
                    welcome_text.setVisibility(View.VISIBLE);
                    home_text.setVisibility(View.GONE);
                    info_text.setVisibility(View.GONE);
                    break;
                case 2:
                    welcome_text.setVisibility(View.GONE);
                    home_text.setVisibility(View.VISIBLE);
                    info_text.setVisibility(View.GONE);
                    break;
                case 3:
                    welcome_text.setVisibility(View.GONE);
                    home_text.setVisibility(View.GONE);
                    info_text.setVisibility(View.VISIBLE);
                    break;
            }
            return null;
        });

        bottomNavigation.setOnShowListener(model -> {
            // YOUR CODES

            if (model.getId() == 1) {
                home.setVisibility(View.VISIBLE);
                image_search.setVisibility(View.GONE);
                info.setVisibility(View.GONE);
            }
            if (model.getId() == 1) {
                welcome_text.setVisibility(View.VISIBLE);
                home_text.setVisibility(View.GONE);
                info_text.setVisibility(View.GONE);
            }
            return null;
        });

        bottomNavigation.setOnShowListener(model -> {
            // YOUR CODES

            if (model.getId() == 2) {
                home.setVisibility(View.GONE);
                image_search.setVisibility(View.VISIBLE);
                info.setVisibility(View.GONE);
            }
            if (model.getId() == 2) {
                welcome_text.setVisibility(View.GONE);
                home_text.setVisibility(View.VISIBLE);
                info_text.setVisibility(View.GONE);
            }
            return null;
        });

        bottomNavigation.setOnShowListener(model -> {
            // YOUR CODES

            if (model.getId() == 3) {
                home.setVisibility(View.GONE);
                image_search.setVisibility(View.GONE);
                info.setVisibility(View.VISIBLE);
            }
            if (model.getId() == 3) {
                welcome_text.setVisibility(View.GONE);
                home_text.setVisibility(View.GONE);
                info_text.setVisibility(View.VISIBLE);
            }
            return null;
        });


        String[] labels = new String[5];
        int cnt=0;
        try {
            BufferedReader bufferedReader =  new BufferedReader( new InputStreamReader(getAssets().open("labels.txt")));
            String line = bufferedReader.readLine();
            while(line!=null){
                labels[cnt]=line;
                cnt++;
                line= bufferedReader.readLine();
            }
        }
        catch(IOException e){
            e.printStackTrace();
        }



        scanbutton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, 100);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        selectbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
                    intent1.setType("image/");
                    startActivityForResult(intent1, 101);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        predictbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    May31Model model = May31Model.newInstance(getApplicationContext());

                    // Creates inputs for reference.
                    TensorBuffer inputFeature0 = TensorBuffer.createFixedSize(new int[]{1, 224, 224, 3}, DataType.FLOAT32);

                    TensorImage tensorImage = new TensorImage(DataType.FLOAT32);
                    tensorImage.load(bitmap);

                    ImageProcessor processor = new ImageProcessor.Builder()
                            .add(new ResizeOp(224, 224, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                            .add(new NormalizeOp(255.0f, 255.0f))
                            .build();
                    TensorImage anotherTensorImage = processor.process(tensorImage);
                    ByteBuffer byteBuffer = anotherTensorImage.getBuffer();

                    inputFeature0.loadBuffer(byteBuffer);

                    // Runs model inference and gets result.
                    May31Model.Outputs outputs = model.process(inputFeature0);
                    TensorBuffer outputFeature0 = outputs.getOutputFeature0AsTensorBuffer();

                    // Releases model resources if no longer used.
                    model.close();
                    textView.setText("The above image is classified as a " + labels[getMax(outputFeature0.getFloatArray())]+".");


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 100){
            bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
        }
        else if(requestCode == 101){
            imageView.setImageURI(data.getData());

            Uri uri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),uri);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    int getMax(float[] arr) {
        int max = 0;
        for(int i = 0;i<arr.length; i++){
            if(arr[i] > arr[max]) max = i;
        }
//        System.out.println(arr);
        return max;
    }
}