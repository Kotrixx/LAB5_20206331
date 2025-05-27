package com.example.lab5_20206331;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST_CODE = 1001;

    private ImageView ivProfileImage;
    private UserPreferences userPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ... tu código setupStatusBar, edgeToEdge, etc.
        setContentView(R.layout.activity_main);

        userPreferences = new UserPreferences(this);

        ivProfileImage = findViewById(R.id.iv_profile_image);

        // Carga imagen guardada si existe
        loadProfileImage();

        // Cuando presionen la imagen abrir galería
        ivProfileImage.setOnClickListener(v -> openGalleryForImage());
    }

    private void openGalleryForImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            ivProfileImage.setImageURI(imageUri); // Mostrar imagen seleccionada

            saveImageToInternalStorage(imageUri);
        }
    }

    private void saveImageToInternalStorage(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            File file = new File(getFilesDir(), "profile_image.jpg"); // Nombre fijo para reemplazar

            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            // Guardar ruta en SharedPreferences para cargar luego
            userPreferences.saveProfileImagePath(file.getAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadProfileImage() {
        String imagePath = userPreferences.getProfileImagePath();
        if (imagePath != null) {
            File imgFile = new File(imagePath);
            if (imgFile.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                ivProfileImage.setImageBitmap(bitmap);
            }
        }
    }
}

