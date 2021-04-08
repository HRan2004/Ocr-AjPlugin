package com.hraps.ocr;

import java.util.ArrayList;
import java.util.Arrays;

public class OcrResult {
    public ArrayList<Integer> frame;
    public String text;
    public int angleType;

    public float dbScore;
    public float angleScore;
    public ArrayList<Float> crnnScore;

    public OcrResult(ArrayList<Integer> frame, String text, int angleType, float dbScore, float angleScore, float[] crnnScore) {
        this.frame = frame;
        this.text = text;
        this.angleType = angleType;
        this.dbScore = dbScore;
        this.angleScore = angleScore;
        this.crnnScore = new ArrayList<Float>();
        for(int i=0;i<crnnScore.length;i++){
            this.crnnScore.add(crnnScore[i]);
        }
    }
}
