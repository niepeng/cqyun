package com.baibutao.app.waibao.yun.android.activites;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.baibutao.app.waibao.yun.android.R;
import com.baibutao.app.waibao.yun.android.activites.common.BaseActivity;

public class SetupNewProductActivity extends BaseActivity {

	private WebView mWebView;

	private ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.setup_new_product);

		mWebView = (WebView) findViewById(R.id.setup_new_product_webview);
		mWebView.getSettings().setJavaScriptEnabled(true);  
		
		progressBar = (ProgressBar) findViewById(R.id.setup_new_product_progress_bar);

		mWebView.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return true;
			}

			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				progressBar.setVisibility(View.GONE);
			}

			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				super.onPageStarted(view, url, favicon);
				progressBar.setVisibility(View.VISIBLE);
			}

		});

		mWebView.loadUrl("http://shop60000498.m.taobao.com");

	}

	public void handleBack(View v) {
		this.finish();
	}

}
