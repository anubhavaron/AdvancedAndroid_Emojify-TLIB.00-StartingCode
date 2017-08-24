package com.example.android.emojify;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

/**
 * Created by ANUBHAV on 8/24/2017.
 */

public class Emojifier {


    public Emojifier()
    {

    }
    static void detectFaces(Context context, Bitmap bitmap)
    {
        FaceDetector detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();


        Frame frame=new Frame.Builder().setBitmap(bitmap).build();

        SparseArray<Face> faces=detector.detect(frame);
        Toast.makeText(context,faces.size()+" ",Toast.LENGTH_LONG).show();

        if(faces.size()==0)
        {
            Toast.makeText(context,"NO faces Detect",Toast.LENGTH_LONG).show();
        }


    }



}
