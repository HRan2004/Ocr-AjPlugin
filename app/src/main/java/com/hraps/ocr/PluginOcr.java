package com.hraps.ocr;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;

import com.hraps.ocr.ncnn.OcrDetector;

import org.autojs.plugin.sdk.Plugin;

import java.util.ArrayList;
import java.util.List;


public class PluginOcr extends Plugin {
    public PluginOcr(Context context, Context selfContext, Object runtime, Object topLevelScope) {
        super(context, selfContext, runtime, topLevelScope);
    }

    OcrDetector ocrDetector = null;

    public List<OcrResult> detect(Bitmap bitmap,float ratio){
        if (ocrDetector==null){
            ocrDetector = new OcrDetector(getSelfContext());
        }
        return ocrDetector.detect(bitmap, ratio);
    }

    public List<OcrResult> filterScore(List<OcrResult> results,float dbnetScore,float angleScore,float crnnScore){
        ArrayList<OcrResult> filterResults = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            OcrResult result = results.get(i);
            if(result.dbScore>dbnetScore&&result.angleScore>angleScore&&getAverage(result.crnnScore)>crnnScore){
                filterResults.add(result);
            }
        }
        return filterResults;
    }

    public static float getAverage(List<Float> nums){
        float sum = 0;
        for (int i = 0; i < nums.size(); i++) {
            sum+=nums.get(i);
        }
        return sum/nums.size();
    }

    @Override
    public String getAssetsScriptDir() {
        return "plugin-ocr";
    }

}
