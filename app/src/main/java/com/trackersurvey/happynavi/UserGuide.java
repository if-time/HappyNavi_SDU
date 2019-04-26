package com.trackersurvey.happynavi;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;

import com.trackersurvey.happynavi.R;
import com.trackersurvey.util.AppManager;
import com.trackersurvey.util.MyWebChromeClient;

public class UserGuide extends Activity {
	private LinearLayout back;
	private WebView webview;
	private Button refresh;
	@SuppressLint("JavascriptInterface")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.userguide);
		AppManager.getAppManager().addActivity(this);
		
		back=(LinearLayout)findViewById(R.id.userguide_back);
		back.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
			
		});	
		init();
	}
	@SuppressLint("JavascriptInterface")
	public void init(){
		webview=(WebView)findViewById(R.id.webview);
		refresh=(Button)findViewById(R.id.userguide_refresh);
		refresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				webview.reload();
			}
		});
		
		WebSettings settings=webview.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		settings.setSupportZoom(true);
		settings.setBuiltInZoomControls(true);
		settings.setDisplayZoomControls(false);
		//settings.setUseWideViewPort(true);   
        //setLoadWithOverviewMode(true); 
		//settings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN); 
		webview.loadUrl("http://219.218.118.176:8089/Admin/SystemIntroduction.aspx");
		//webview.loadUrl("http://wifimap.sinaapp.com/index.html"); 
		webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		webview.setBackgroundColor(Color.WHITE);
		webview.setWebChromeClient(new MyWebChromeClient());
		webview.addJavascriptInterface(this, "userGuide");
		webview.setWebViewClient(new WebViewClient() {
		    public void onPageFinished(WebView view, String url) {//��ҳ�������ɺ��ٵ���js����������ִ�С�
		       /*
		    	 webview.loadUrl("javascript:getUserID('"+Common.userId+"')");
		    	 Log.i("trailadapter", "�����û���");
		    	*/
		super.onPageFinished(view, url);
		}
		});
	}
}
