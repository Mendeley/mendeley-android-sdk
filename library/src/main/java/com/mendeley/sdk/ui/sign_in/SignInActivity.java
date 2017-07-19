package com.mendeley.sdk.ui.sign_in;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mendeley.sdk.ClientCredentials;
import com.mendeley.sdk.Mendeley;
import com.mendeley.sdk.R;
import com.mendeley.sdk.Request;
import com.mendeley.sdk.exceptions.MendeleyException;
import com.mendeley.sdk.request.endpoint.OAuthTokenEndpoint;

import java.util.Date;

/**
 * This activity will show the login web interface in a webview.
 * The layout style will depend on the screen size. Full screen for small devices
 * and a dialog view for large ones.
 */
public class SignInActivity extends Activity {

    //  Request code used both by this activity.
    public final static int REQUEST_CODE = 31231;

    private static final double SMALL_SCREEN_SIZE = 6.0;
	private static final String FORGOT_PASSWORD_URL = "http://www.mendeley.com/forgot/";

	private Mendeley mendeley;
	private WebView webView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

		mendeley = Mendeley.getInstance();

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (getScreenSize() <= SMALL_SCREEN_SIZE) {
			super.setTheme(R.style.MendeleySDKTheme);

        } else {
			setFinishOnTouchOutside(false);
		}

        setContentView(R.layout.activity_sign_in);
		webView = (WebView) findViewById(R.id.dialogWebView);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setVerticalScrollBarEnabled(true);
		webView.setHorizontalScrollBarEnabled(true);
		webView.setVerticalScrollBarEnabled(true);
		webView.setHorizontalScrollBarEnabled(true);
		webView.requestFocusFromTouch();
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.getSettings().setUserAgentString("Android " + getPackageName());
		webView.setWebViewClient(new MendeleyWebViewClient());

		if (savedInstanceState != null) {
			webView.restoreState(savedInstanceState);
		} else {
			webView.loadUrl(getOauth2URL(mendeley.getClientCredentials()).toString());
		}

        View dismissButton = findViewById(R.id.dismissButton);
        if (dismissButton != null) {
            dismissButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }
    }

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		webView.saveState(outState);
	}

	/**
     * Finding the screen size in inches
     *
     * @return the screen size
     */
    private double getScreenSize() {
    	DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        double x = Math.pow(dm.widthPixels/dm.xdpi,2);
        double y = Math.pow(dm.heightPixels/dm.ydpi,2);
        double screenInches = Math.sqrt(x+y);
        screenInches =  (double)Math.round(screenInches * 10) / 10;

        return screenInches;
    }

	/**
	 * A WebViewClient that starts the AuthenticationTask when a new url is loaded.
	 */
    private class MendeleyWebViewClient extends WebViewClient {
    	@Override
    	public boolean shouldOverrideUrlLoading (WebView view, String url) {

			if (url.startsWith(OAuthTokenEndpoint.REDIRECT_URI)) {
				onAboutToLoadRedirectionUrl(url);
				return true;
			}

    		if (url.equals(FORGOT_PASSWORD_URL)) {
    			openUrlInBrowser(url);
				return true;
    		}

			return false;
    	}

	}

    /**
     * Opening a web browser to load the given url
     * @param url the url to load
     */
    private void openUrlInBrowser(String url) {
    	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    	startActivity(intent);
    }

   private Uri getOauth2URL(ClientCredentials clientCredentials) {
        return Uri.parse(OAuthTokenEndpoint.AUTHORIZATION_URL).buildUpon()
                .appendQueryParameter("grant_type", "authorization_code")
                .appendQueryParameter("scope", "all")
                .appendQueryParameter("response_type", "code")
                .appendQueryParameter("client_id", clientCredentials.clientId)
                .build();
    }

	private void onAboutToLoadRedirectionUrl(String url) {
		String authorizationCode = null;
		int index = url.indexOf("code=");
		if (index != -1) {
			index += 5;
			authorizationCode = url.substring(index);
		}

		obtainAccessTokenFromAuthorizationCode(authorizationCode);
	}

	private void obtainAccessTokenFromAuthorizationCode(String authorizationCode) {
		final OAuthTokenEndpoint.AccessTokenWithAuthorizationCodeRequest request = new OAuthTokenEndpoint.AccessTokenWithAuthorizationCodeRequest(mendeley.getAuthManager(), mendeley.getClientCredentials(), authorizationCode);
		request.runAsync(new Request.RequestCallback<Void>() {
			@Override
			public void onSuccess(Void aVoid, Uri next, Date serverDate) {
				setResultAndFinish(Activity.RESULT_OK);
			}

			@Override
			public void onFailure(MendeleyException mendeleyException) {
                Log.e(SignInActivity.class.getSimpleName(), "Error obtaining access token", mendeleyException);
				setResultAndFinish(Activity.RESULT_CANCELED);
			}

			@Override
			public void onCancelled() {
				setResultAndFinish(Activity.RESULT_CANCELED);
			}
		});
	}

	private void setResultAndFinish(int result) {
		setResult(result);
		finish();
	}
}
