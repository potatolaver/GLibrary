package org.gg.glibrary;

import android.os.Bundle;
import android.view.TextureView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextureView textureView = findViewById(R.id.textureview);
    }
}
