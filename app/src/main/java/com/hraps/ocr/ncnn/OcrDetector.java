package com.hraps.ocr.ncnn;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.benjaminwan.ocrlibrary.OcrEngine;
import com.benjaminwan.ocrlibrary.Point;
import com.benjaminwan.ocrlibrary.TextBlock;
import com.hraps.ocr.OcrResult;

import java.util.ArrayList;
import java.util.List;

public class OcrDetector {
    OcrEngine ocrEngine;
    Context context;
    String TAG = "OcrDetector";

    public OcrDetector(Context context) {
        this.context = context;
        this.ocrEngine = new OcrEngine(context);
    }

    public List<OcrResult> detect(Bitmap bitmap, float ratio){
        try {
            com.benjaminwan.ocrlibrary.OcrResult ocrResult = null;
            int maxxSize = Math.max(bitmap.getWidth(),bitmap.getHeight());
            int maxSideLen = (int)(ratio*maxxSize);
            Log.i(TAG, "detect: "+maxxSize);Bitmap boxImg = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            ocrResult = ocrEngine.detect(bitmap, boxImg, maxSideLen);
            Log.i(TAG, "detect: "+ocrResult.getStrRes());
            ArrayList<OcrResult> results = new ArrayList<>();
            for(int i = 0;i<ocrResult.getTextBlocks().size();i++){
                TextBlock tb = ocrResult.getTextBlocks().get(i);
                ArrayList<Point> ps = tb.getBoxPoint();
                ArrayList<Integer> parr = new ArrayList<>();
                for(int l = 0;l<ps.size();l++){
                    parr.add(ps.get(l).getX());
                    parr.add(ps.get(l).getY());
                }
                results.add(new OcrResult(parr,tb.getText(),tb.getAngleIndex(),tb.getBoxScore(),tb.getAngleScore(),tb.getCharScores()));
                Log.i(TAG, "detect: "+tb.getText());
            }
            return results;
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public List<OcrResult> detect(Bitmap bitmap){
        return detect(bitmap,100);
    }

}
