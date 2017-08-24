package com.example.android.emojify;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.util.SparseArray;
import android.widget.Toast;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;

import static com.example.android.emojify.Emojifier.Emoji.CLOSED_EYE_FROWN;
import static com.example.android.emojify.Emojifier.Emoji.CLOSED_EYE_SMILE;
import static com.example.android.emojify.Emojifier.Emoji.FROWN;
import static com.example.android.emojify.Emojifier.Emoji.LEFT_WINK;
import static com.example.android.emojify.Emojifier.Emoji.LEFT_WINK_FROWN;
import static com.example.android.emojify.Emojifier.Emoji.RIGHT_WINK;
import static com.example.android.emojify.Emojifier.Emoji.RIGHT_WINK_FROWN;
import static com.example.android.emojify.Emojifier.Emoji.SMILE;

/**
 * Created by ANUBHAV on 8/24/2017.
 */

public class Emojifier {
    private static final String LOG_TAG = Emojifier.class.getSimpleName();

    private static final double SMILING_PROB_THRESHOLD = .15;
    private static final double EYE_OPEN_PROB_THRESHOLD = .3;
    private static final float EMOJI_SCALE_FACTOR = .9f;


    public Emojifier()
    {

    }



    private static Bitmap addBitmapToFace(Bitmap backgroundBitmap, Bitmap emojiBitmap, Face face) {

        // Initialize the results bitmap to be a mutable copy of the original image
        Bitmap resultBitmap = Bitmap.createBitmap(backgroundBitmap.getWidth(),
                backgroundBitmap.getHeight(), backgroundBitmap.getConfig());

        // Scale the emoji so it looks better on the face
        float scaleFactor = EMOJI_SCALE_FACTOR;

        // Determine the size of the emoji to match the width of the face and preserve aspect ratio
        int newEmojiWidth = (int) (face.getWidth() * scaleFactor);
        int newEmojiHeight = (int) (emojiBitmap.getHeight() *
                newEmojiWidth / emojiBitmap.getWidth() * scaleFactor);


        // Scale the emoji
        emojiBitmap = Bitmap.createScaledBitmap(emojiBitmap, newEmojiWidth, newEmojiHeight, false);

        // Determine the emoji position so it best lines up with the face
        float emojiPositionX =
                (face.getPosition().x + face.getWidth() / 2) - emojiBitmap.getWidth() / 2;
        float emojiPositionY =
                (face.getPosition().y + face.getHeight() / 2) - emojiBitmap.getHeight() / 3;

        // Create the canvas and draw the bitmaps to it
        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(backgroundBitmap, 0, 0, null);
        canvas.drawBitmap(emojiBitmap, emojiPositionX, emojiPositionY, null);

        return resultBitmap;
    }
   Bitmap detectFacesAndOverlayEmoji(Context context, Bitmap bitmap)
    {
        FaceDetector detector = new FaceDetector.Builder(context)
                .setTrackingEnabled(false)
                .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                .build();


        Frame frame=new Frame.Builder().setBitmap(bitmap).build();

        SparseArray<Face> faces=detector.detect(frame);
        Toast.makeText(context,faces.size()+" ",Toast.LENGTH_LONG).show();
        Bitmap resultBitmap = bitmap;
        if(faces.size()==0)
        {
            Toast.makeText(context,"NO faces Detect",Toast.LENGTH_LONG).show();
        }

        int i;
        for(i=0;i<faces.size();i++)

        {

            Bitmap emojiBitmap;


            Face face=faces.valueAt(i);
            switch (whichEmoji(face)) {
                case SMILE:
                    emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.smile);
                    break;
                case FROWN:
                    emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.frown);
                    break;
                case LEFT_WINK:
                    emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.leftwink);
                    break;
                case RIGHT_WINK:
                    emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.rightwink);
                    break;
                case LEFT_WINK_FROWN:
                    emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.leftwinkfrown);
                    break;
                case RIGHT_WINK_FROWN:
                    emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.rightwinkfrown);
                    break;
                case CLOSED_EYE_SMILE:
                    emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.closed_smile);
                    break;
                case CLOSED_EYE_FROWN:
                    emojiBitmap = BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.closed_frown);
                    break;
                default:
                    emojiBitmap = null;
                    Toast.makeText(context, R.string.no_emoji, Toast.LENGTH_SHORT).show();
            }



            resultBitmap = addBitmapToFace(resultBitmap, emojiBitmap, face);


        }
        detector.release();


        return resultBitmap;

    }
    static void getClassifaction(Face face)
    {


        Log.d("Hello",face.getIsSmilingProbability()+" "+face.getIsLeftEyeOpenProbability()+" "+face.getIsRightEyeOpenProbability());


    }
    private static Emoji whichEmoji(Face face) {

        // Log all the probabilities
        Log.d(LOG_TAG, "whichEmoji: smilingProb = " + face.getIsSmilingProbability());
        Log.d(LOG_TAG, "whichEmoji: leftEyeOpenProb = "
                + face.getIsLeftEyeOpenProbability());
        Log.d(LOG_TAG, "whichEmoji: rightEyeOpenProb = "
                + face.getIsRightEyeOpenProbability());


        boolean smiling = face.getIsSmilingProbability() > SMILING_PROB_THRESHOLD;

        boolean leftEyeClosed = face.getIsLeftEyeOpenProbability() < EYE_OPEN_PROB_THRESHOLD;
        boolean rightEyeClosed = face.getIsRightEyeOpenProbability() < EYE_OPEN_PROB_THRESHOLD;


        // Determine and log the appropriate emoji
        Emoji emoji;
        if(smiling) {
            if (leftEyeClosed && !rightEyeClosed) {
                emoji = LEFT_WINK;
            }  else if(rightEyeClosed && !leftEyeClosed){
                emoji = RIGHT_WINK;
            } else if (leftEyeClosed){
                emoji = CLOSED_EYE_SMILE;
            } else {
                emoji = SMILE;
            }
        } else {
            if (leftEyeClosed && !rightEyeClosed) {
                emoji = LEFT_WINK_FROWN;
            }  else if(rightEyeClosed && !leftEyeClosed){
                emoji = RIGHT_WINK_FROWN;
            } else if (leftEyeClosed){
                emoji = CLOSED_EYE_FROWN;
            } else {
                emoji = FROWN;
            }
        }
        Log.d("hello", "whichEmoji: " + emoji.name());
        return emoji;
        // Log the chosen Emoji

    }



    enum Emoji {
        SMILE,
        FROWN,
        LEFT_WINK,
        RIGHT_WINK,
        LEFT_WINK_FROWN,
        RIGHT_WINK_FROWN,
        CLOSED_EYE_SMILE,
        CLOSED_EYE_FROWN
    }






}
