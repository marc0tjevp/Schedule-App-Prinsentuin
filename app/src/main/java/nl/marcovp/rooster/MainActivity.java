package nl.marcovp.rooster;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private ConnectivityManager cm;
    private ProgressBar mPbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        webView = (WebView) findViewById(R.id.webView);
        mPbar = (ProgressBar) findViewById(R.id.progressBar);

        mPbar.setVisibility(View.VISIBLE);
        webView.setVisibility(View.INVISIBLE);

        // Enable Hardware Acceleration if available
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        // Enable Javascript
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);

        // Set scrollbars
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);

        // Cache test
//        cm = (ConnectivityManager) this.getSystemService(Activity.CONNECTIVITY_SERVICE);
//
//        if(cm == null && cm.getActiveNetworkInfo() == null && !cm.getActiveNetworkInfo().isConnected()){
//
//            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//            Context context = getApplicationContext();
//            Toast.makeText(context, "No Connection, loaded older version if available" , Toast.LENGTH_SHORT).show();
//        }

        // Load the WebView
        loadView();

    }

    protected void loadView() {
        // Get the webview by id
        webView = (WebView) findViewById(R.id.webView);

        // Clear cache
        webView.clearCache(true);

        // Load URL
        webView.loadUrl("https://sa-rocwb.xedule.nl/");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(final WebView view, String url)
            {

                Handler handler = new Handler();
                handler.postDelayed(new Runnable(){
                    @Override
                    public void run(){
                        webView.loadUrl("javascript:(function() { " +
                                "document.getElementsByClassName('header')[0].style.display='none';" +
                                "document.getElementsByClassName('xedule-tab-strip nav nav-tabs')[0].style.display='none';" +
                                "document.getElementsByClassName('menu-container')[0].style.display='none';" +
                                "document.getElementsByTagName('body')[0].style.marginTop='0px';" +
                                "document.getElementsByTagName('footer')[0].style.display='none';" +
                                "document.getElementsByClassName('publication-date')[0].style.paddingLeft='0px';" +
                                "document.getElementsByClassName('publication-date')[0].style.cssFloat='none';" +
                                " })()");

                        webView.setVisibility(View.VISIBLE);
                        mPbar.setVisibility(View.INVISIBLE);

                    }
                }, 2000);


            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_refresh) {
            webView.clearCache(true);
            mPbar.setVisibility(View.VISIBLE);
            webView.setVisibility(View.INVISIBLE);
            loadView();
            return true;
        }
        if (item.getItemId() == R.id.action_logout) {
            webView.clearCache(true);
            webView.clearHistory();
            clearCookies(getApplicationContext());
            loadView();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("deprecation")
    public static void clearCookies(Context context)
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else
        {
            CookieSyncManager cookieSyncMngr=CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager=CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

}
