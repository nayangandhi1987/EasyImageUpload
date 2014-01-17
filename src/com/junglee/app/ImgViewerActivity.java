package com.junglee.app;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ru.truba.touchgallery.GalleryWidget.GalleryViewPager;
import ru.truba.touchgallery.GalleryWidget.UrlPagerAdapter;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;

import com.example.jungleeclick.R;

public class ImgViewerActivity extends Activity {
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imgviewer);
        
        String urlsString = "";
        
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            urlsString = extras.getString("urls");
            Log.i("JungleeClick", urlsString);
        }
        
        String[] urls = urlsString.split("#");
        		List<String> items = new ArrayList<String>();
        		Collections.addAll(items, urls);
        		UrlPagerAdapter pagerAdapter = new UrlPagerAdapter(this, items);  
        		GalleryViewPager mViewPager = (GalleryViewPager)findViewById(R.id.viewer);
        		mViewPager.setOffscreenPageLimit(3);
        		mViewPager.setAdapter(pagerAdapter);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
}
