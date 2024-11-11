package com.example.k4mediaassingment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.k4mediaassingment.databinding.ActivityMainBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private final OkHttpClient client = new OkHttpClient();
    private final String gameUrl = "https://github.com/BKcore/HexGL/archive/refs/heads/master.zip";
    private File gameDirectory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();


        // Initialize game directory
        gameDirectory = new File(getExternalFilesDir(null), "hexgl");


        showImages();

        String gameUrl1 = "https://g.h5games.online/stack-jump/";
        String gameUrl2 = "https://g.h5games.online/traffic-racer-2/";
        String gameUrl4 = "https://play.famobi.com/bubble-woods";

        binding.imageGame1.setOnClickListener(v -> loadGame(gameUrl1));
        binding.imageGame2.setOnClickListener(v -> loadGame(gameUrl2));


        binding.imageGame3.setOnClickListener(v -> downloadAndExtractGame());
        binding.imageGame4.setOnClickListener(v -> loadGame(gameUrl4));
    }

    private void showImages() {

        String imgUrl1 = "https://s.h5games.online/images/stack-jump.jpg";
        loadImage(imgUrl1, binding.imageGame1);
        String imgUrl2 = "https://g.h5games.online/traffic-racer-2/";
        loadImage(imgUrl2, binding.imageGame2);
        String imgUrl3 = "https://s.h5games.online/images/candy-forest.jpg";
        loadImage(imgUrl3, binding.imageGame3);
        String imgUrl4 = "https://s.h5games.online/images/downhill-ski.jpg";
        loadImage(imgUrl4, binding.imageGame4);
    }

    private void loadImage(String url, ImageView id) {
        Glide.with(this)
                .load(url)
                .placeholder(R.drawable.game1)
                .into(id);
    }

    private void loadGame(String gameUrl) {
        Intent intent = new Intent(this, WebViewActivity.class);
        intent.putExtra("gameUrl", gameUrl);
        startActivity(intent);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("MainActivity11", "In MainActivity onDestroy");
        binding = null; // Prevent memory leaks
    }

    private void downloadAndExtractGame() {
        binding.progressBar.setVisibility(View.VISIBLE);
        if (!gameDirectory.exists()) {
            gameDirectory.mkdirs();
            Log.d("MainActivity11", "Game directory created");
        }
        Log.d("MainActivity11", "Game directory exists");

        File zipFile = new File(gameDirectory, "hexgl.zip");
        if (zipFile.exists()) zipFile.delete();

        Request request = new Request.Builder().url(gameUrl).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    try (InputStream inputStream = response.body().byteStream()) {
                        saveInputStreamToFile(inputStream, zipFile);
                        unzip(zipFile, gameDirectory);
                        loadGameInWebView();

                    } catch (IOException e) {
                        Log.e("MainActivity11", "Error extracting game", e);
                    }
                } else {

                    Log.e("MainActivity11", "Download failed");
                }
            }

            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Download failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                    binding.progressBar.setVisibility(View.GONE);
                });
                Log.e("MainActivity11", "Download failed", e);
            }
        });
    }

    private void saveInputStreamToFile(InputStream inputStream, File file) throws IOException {
        Log.d("MainActivity11", "Saving game...");
        try (FileOutputStream output = new FileOutputStream(file)) {

            byte[] buffer = new byte[4096];
            int bytesRead;
            Log.d("MainActivity11", "Reading game...");
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
        }
    }

    private void unzip(File zipFile, File targetDirectory) throws IOException {
        Log.d("MainActivity11", "Unzipping game...");
        try (ZipInputStream zipInputStream = new ZipInputStream(zipFile.toURI().toURL().openStream())) {
            ZipEntry entry;
            while ((entry = zipInputStream.getNextEntry()) != null) {
                File file = new File(targetDirectory, entry.getName());
                if (entry.isDirectory()) {
                    Log.d("MainActivity11", "Creating directory: " + file.getAbsolutePath());
                    file.mkdirs();
                } else {
                    Log.d("MainActivity11", "Extracting file: " + file.getAbsolutePath());
                    file.getParentFile().mkdirs();
                    try (FileOutputStream output = new FileOutputStream(file)) {
                        Log.d("MainActivity11", "Writing to file: " + file.getAbsolutePath());
                        byte[] buffer = new byte[4096];
                        int len;
                        while ((len = zipInputStream.read(buffer)) != -1) {
                            output.write(buffer, 0, len);
                        }
                    }
                }

                zipInputStream.closeEntry();
                Log.d("MainActivity11", "Finished processing entry: " + entry.getName());
            }
        }
    }


    private void loadGameInWebView() {
        try {
            runOnUiThread(() -> {
                File indexPath = new File(gameDirectory, "HexGL-master/index.html");
                if (indexPath.exists()) {
                    binding.downloadBtn3.setVisibility(View.GONE);
                    binding.progressBar.setVisibility(View.GONE);
                    binding.playBtn.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this, "Game Downloaded Successfully", Toast.LENGTH_SHORT).show();
                    binding.playBtn.setOnClickListener(v -> {
                        loadGame("file://" + indexPath.getAbsolutePath());
                        Toast.makeText(MainActivity.this, "Loading Game", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    Log.e("MainActivity11", "Game path not found");
                    Toast.makeText(MainActivity.this, "Failed to load game", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("MainActivity11", "Error loading game in WebView", e);
        }

    }


}
