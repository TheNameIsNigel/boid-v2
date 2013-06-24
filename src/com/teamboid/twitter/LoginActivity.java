package com.teamboid.twitter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import twitter4j.TwitterException;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

@SuppressLint("SetJavaScriptEnabled")
public class LoginActivity extends Activity {

    private void loadWeb() {
        final WebView view = (WebView) findViewById(R.id.webView);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final RequestToken token = BoidApp.get(LoginActivity.this).getClient().getOAuthRequestToken(BoidApp.CALLBACK_URL);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            view.loadUrl(token.getAuthorizationURL());
                        }
                    });
                } catch (TwitterException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            findViewById(R.id.welcomeFrame).setVisibility(View.VISIBLE);
                            findViewById(R.id.webFrame).setVisibility(View.GONE);
                            ((TextView) findViewById(R.id.loginMessage)).setText(R.string.failed_login);
                            ((Button) findViewById(R.id.login)).setText(R.string.retry);
                        }
                    });
                }
            }
        }).start();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        setupWebView();

        findViewById(R.id.login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.welcomeFrame).setVisibility(View.GONE);
                findViewById(R.id.webFrame).setVisibility(View.VISIBLE);
                loadWeb();
            }
        });

        getActionBar().setDisplayShowHomeEnabled(false);
    }

    private void setupWebView() {
        WebView view = (WebView) findViewById(R.id.webView);
        view.getSettings().setJavaScriptEnabled(true);
        view.getSettings().setAppCacheEnabled(false);
        view.getSettings().setSavePassword(false);
        view.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, final String url) {
                if (url.startsWith("boid://")) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                AccessToken token = BoidApp.get(LoginActivity.this).getClient().getOAuthAccessToken(Uri.parse(url).getQueryParameter("oauth_verifier"));
                                User me = BoidApp.get(LoginActivity.this).getClient().verifyCredentials();
                                BoidApp.get(LoginActivity.this).storeToken(token).storeProfile(me);
                            } catch (final TwitterException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        BoidApp.get(LoginActivity.this).clearAccount();
                                        findViewById(R.id.welcomeFrame).setVisibility(View.VISIBLE);
                                        findViewById(R.id.webFrame).setVisibility(View.GONE);
                                        ((TextView) findViewById(R.id.loginMessage)).setText(R.string.failed_login);
                                        ((Button) findViewById(R.id.login)).setText(R.string.retry);
                                    }
                                });
                                return;
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    findViewById(R.id.welcomeFrame).setVisibility(View.VISIBLE);
                                    findViewById(R.id.webFrame).setVisibility(View.GONE);
                                    ((TextView) findViewById(R.id.loginMessage)).setText(R.string.logged_in);
                                    Button login = (Button) findViewById(R.id.login);
                                    login.setText(R.string.finish);
                                    login.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            finish();
                                        }
                                    });

                                }
                            });
                        }
                    }).start();
                    return true;
                } else return false;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Toast.makeText(getApplicationContext(), description, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favIcon) {
                view.setVisibility(View.GONE);
                findViewById(R.id.webProgress).setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                view.setVisibility(View.VISIBLE);
                findViewById(R.id.webProgress).setVisibility(View.GONE);
            }
        });
    }
}
