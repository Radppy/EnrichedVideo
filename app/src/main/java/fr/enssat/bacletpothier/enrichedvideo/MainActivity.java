package fr.enssat.bacletpothier.enrichedvideo;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    // Vidéo
    private VideoView videoView;
    private int position = 0;
    private MediaController mediaController;
    // Page web
    private WebView webView;
    //Spinner
    private Spinner spinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoView = findViewById(R.id.videoView);
        webView = findViewById(R.id.webView);
        initVideo();
        initWebView();

    }


    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        // Staockage de la position courante
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


    private void initVideo(){
        // MediaController
        if (mediaController == null) {
            mediaController = new MediaController(MainActivity.this);
            mediaController.setAnchorView(videoView);
            videoView.setMediaController(mediaController);
        }
        // Ouverture de la vidéo
        try {
            String videoUrl="http://download.blender.org/peach/bigbuckbunny_movies/BigBuckBunny_320x180.mp4";
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
                    }
                });
            }
        });
    }

    private void initWebView(){
        //WebView
        webView.loadUrl("https://developer.android.com/reference/android/webkit/WebView.html");
    }



}
