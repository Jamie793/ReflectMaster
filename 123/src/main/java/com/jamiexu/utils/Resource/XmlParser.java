package com.jamiexu.utils.Resource;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

public class XmlParser {
    private static final String TAG = "XmlParser";


    public static void parse(XmlPullParser xmlPullParser) {
        try {
            int eventType;
            while ((eventType = xmlPullParser.next()) != XmlPullParser.END_DOCUMENT) {

                if (eventType != XmlPullParser.START_TAG) {
                    continue;
                }
                Log.d(TAG, eventType + "");
                Log.d(TAG, xmlPullParser.getName());
                Log.d(TAG, xmlPullParser.getText());
            }
        } catch (
                XmlPullParserException e) {
            e.printStackTrace();
        } catch (
                IOException e) {
            e.printStackTrace();
        }
    }

}
