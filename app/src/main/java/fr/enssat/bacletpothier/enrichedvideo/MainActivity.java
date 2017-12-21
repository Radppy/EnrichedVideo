/**
 * application EnrichedVideo
 * Module Activity
 * authors F. Pothier, A.C. Baclet
 */
package fr.enssat.bacletpothier.enrichedvideo;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;


import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    // Vidéo
    private VideoView videoView;
    private int position = 0;
    private MediaController mediaController;
    private String videoUrl="http://download.blender.org/peach/bigbuckbunny_movies/BigBuckBunny_320x180.mp4";
    // Page web
    private WebView webView;
    private String pageURL = "https://en.wikipedia.org/wiki/Bird";
    private LinkedHashMap<Integer,String> metas;
    private Handler wHandler;
    private static final int UPDATE_FREQ = 500;


    private View.OnClickListener bListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int position = (int)v.getTag();
            videoView.seekTo(position);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoView = findViewById(R.id.videoView);
        webView = findViewById(R.id.webView);
        wHandler = new Handler();
        initWebView(this.pageURL);
        initVideo(this.videoUrl);

    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // Stockage de la position courante
        savedInstanceState.putInt("CurrentPosition", videoView.getCurrentPosition());
        videoView.pause();
    }


    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Récupétration de la position stockée
        position = savedInstanceState.getInt("CurrentPosition");
        videoView.seekTo(position);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        wHandler.removeCallbacks(checkMetas);
    }

    /**
     * Initialise la VideoView et le MediaControler.
     * Prépare la vidéo située à l'URL donnée.
     *
     * @param  videoUrl  l'URL de la vidéo à lire
     */
    private void initVideo(String videoUrl){
        Log.d("initVideo","init video start");
        // MediaController
        if (mediaController == null) {
            mediaController = new MediaController(MainActivity.this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);
        }
        // Ouverture de la vidéo
        try {
            Uri video = Uri.parse(videoUrl);
            videoView.setVideoURI(video);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        videoView.requestFocus();
        // Quand la vidéo est prête
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            public void onPrepared(MediaPlayer mediaPlayer) {
                videoView.seekTo(position);
                if (position == 0) {
                    videoView.start();
                }
                // Si changement de la taille de la vidéo
                mediaPlayer.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {
                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
                        // Re-set de la VideoView
                        mediaController.setAnchorView(videoView);
                        initMetatags();
                        initButtons();
                    }
                });
            }
        });

    }

    /**
     * Initialise la WebView sur laquelle on affiche les pages web
     * en lien avec la partie de la vidéo en cours de lecture.
     *
     * @param  pageURL  l'URL de la page initilement affichée
     */
    private void initWebView(String pageURL){
        Log.d("initWebView","init web view start");
        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return false;
            }
        });
        webView.loadUrl(pageURL);
    }

    private void initMetatags(){
        Log.d("initMetatags","init start");
        metas = JsonParser.parseDatas(
                getResources().openRawResource(R.raw.webpages),"WebPages","pos", "url"
                );

        wHandler.post(checkMetas);
    }


    private final Runnable checkMetas = new Runnable() {
        @Override
        public void run() {
            final int position = videoView.getCurrentPosition();
            final String url = getClosestUrl(position);
            if(webView.getUrl()!=url){
                webView.loadUrl(url);
            }
            wHandler.postDelayed(this,UPDATE_FREQ);
        }
    };



    private String getClosestUrl(int position){
        String result = null;
        for(Map.Entry<Integer,String> meta : metas.entrySet()){
            Log.d("getClosestUrl","meta-"+meta.getKey()+"-"+meta.getValue());
            if(meta.getKey()<position){
                result = meta.getValue();
            }
            if(meta.getKey()>position){
                break;
            }
        }
        return result;
    }

    private void initButtons(){
        Log.d("initButtons","init buttons start");
        InputStream inputStream = getResources().openRawResource(R.raw.chap);
        LinkedHashMap<Integer,String> datas = JsonParser.parseDatas(inputStream,"Chapters","pos","title");

        int pos = 0;
        String title = "";
        LinearLayout chapters = (LinearLayout)findViewById(R.id.bLayout);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        for(Map.Entry<Integer,String> data : datas.entrySet()){
            Log.d("initButtons","data-"+data.getKey()+"-"+data.getValue());
            pos=data.getKey();
            title=data.getValue();
            Button button = new Button(this);
            button.setTag(pos);
            button.setText(title);
            button.setLayoutParams(layoutParams);
            button.setOnClickListener(bListener);
            chapters.addView(button);
        }
    }


}
