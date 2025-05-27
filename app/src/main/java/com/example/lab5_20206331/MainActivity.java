package com.example.lab5_20206331;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST_CODE = 1001;

    private ImageView ivProfileImage;
    private TextView tvGreeting, tvMotivationalMessage;
    private Button btnViewMedications, btnSettings;

    private UserPreferences userPreferences;

    // Lanzador para pedir permiso galería y abrir galería
    private ActivityResultLauncher<String> requestGalleryPermissionLauncher;
    private ActivityResultLauncher<Intent> pickImageLauncher;

    // Lanzador para pedir permiso notificaciones (Android 13+)
    private ActivityResultLauncher<String> requestNotificationPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userPreferences = new UserPreferences(this);

        ivProfileImage = findViewById(R.id.iv_profile_image);
        tvGreeting = findViewById(R.id.tv_greeting);
        tvMotivationalMessage = findViewById(R.id.tv_motivational_message);
        btnViewMedications = findViewById(R.id.btn_view_medications);
        btnSettings = findViewById(R.id.btn_settings);

        // Mostrar saludo y mensaje
        updateGreetingAndMessage();

        // Cargar imagen
        loadProfileImage();

        // Lanzadores
        setupActivityResultLaunchers();

        // Imagen: al tocar, pide permiso o abre galería
        ivProfileImage.setOnClickListener(v -> {
            if (checkGalleryPermission()) {
                openGallery();
            } else {
                requestGalleryPermissionLauncher.launch(getGalleryPermission());
            }
        });

        btnViewMedications.setOnClickListener(v -> {
            startActivity(new Intent(this, MedicamentosActivity.class));
        });

        btnSettings.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
        });

        // Al inicio, pedir permiso de notificaciones si es necesario
        checkAndRequestNotificationPermission();
    }

    private void updateGreetingAndMessage() {
        String nombre = userPreferences.getUserName();
        String mensaje = userPreferences.getMotivationalMessage();
        tvGreeting.setText("¡Hola, " + nombre + "!");
        tvMotivationalMessage.setText(mensaje);
    }

    private void setupActivityResultLaunchers() {
        // Permiso galería
        requestGalleryPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        openGallery();
                    } else {
                        Toast.makeText(this, "Permiso de galería denegado", Toast.LENGTH_SHORT).show();
                    }
                });

        // Abrir galería para seleccionar imagen
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        if (imageUri != null) {
                            ivProfileImage.setImageURI(imageUri);
                            saveImageToInternalStorage(imageUri);
                        }
                    }
                });

        // Permiso notificaciones (Android 13+)
        requestNotificationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                isGranted -> {
                    if (isGranted) {
                        Toast.makeText(this, "Permiso de notificaciones concedido", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Permiso de notificaciones denegado", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private boolean checkGalleryPermission() {
        return ContextCompat.checkSelfPermission(this, getGalleryPermission()) == PackageManager.PERMISSION_GRANTED;
    }

    private String getGalleryPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return Manifest.permission.READ_MEDIA_IMAGES;  // Android 13+
        } else {
            return Manifest.permission.READ_EXTERNAL_STORAGE; // Android < 13
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private void saveImageToInternalStorage(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            File file = new File(getFilesDir(), "profile_image.jpg");

            FileOutputStream outputStream = new FileOutputStream(file);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();

            userPreferences.saveProfileImagePath(file.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadProfileImage() {
        String path = userPreferences.getProfileImagePath();
        if (path != null) {
            File file = new File(path);
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(path);
                ivProfileImage.setImageBitmap(bitmap);
            }
        }
    }

    private void checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateGreetingAndMessage();
    }
}
