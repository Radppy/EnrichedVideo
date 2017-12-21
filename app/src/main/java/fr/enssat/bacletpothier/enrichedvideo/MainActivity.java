package fr.enssat.bacletpothier.enrichedvideo;

import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    // Vidéo
    private VideoView videoView;
    private int position = 0;
    private MediaController mediaController;
    private String videoUrl="http://download.blender.org/peach/bigbuckbunny_movies/BigBuckBunny_320x180.mp4";
    // Page web
    private WebView webView;
    private String pageURL = "https://developer.android.com/reference/android/webkit/WebView.html";
    //Boutons
    private Button bIntro;
    private Button bTitle;
    private Button bButterflies;
    private Button bAssault;
    private Button bPayback;
    private Button bCredits;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        videoView = findViewById(R.id.videoView);
        webView = findViewById(R.id.webView);
        bIntro = findViewById(R.id.bIntro);
        bTitle = findViewById(R.id.bTitle);
        bButterflies = findViewById(R.id.bButterflies);
        bAssault = findViewById(R.id.bAssault);
        bPayback = findViewById(R.id.bPayback);
        bCredits = findViewById(R.id.bCredits);
        initVideo(this.videoUrl);
        initWebView(this.pageURL);
        initButtons();
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

    /**
     * Initialise la VideoView et le MediaControler.
     * Prépare la vidéo située à l'URL donnée.
     *
     * @param  videoUrl  l'URL de la vidéo à lire
     */
    private void initVideo(String videoUrl){
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
        webView.loadUrl(pageURL);
    }

    private void initButtons(){

    }


}
