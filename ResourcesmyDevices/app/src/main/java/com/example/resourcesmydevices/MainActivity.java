package com.example.resourcesmydevices;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private BluetoothAdapter bluetoothAdapter;
    private Activity activity;
    private TextView VersionAndroid;
    private int versionSDK;
    private ProgressBar pbLevelBaterry;
    private TextView tvLevelBaterry;
    IntentFilter batteryFilter;
    CameraManager cameraManager;
    String cameraId;
    private EditText namefile;
    private Archivo archivo;
    private TextView tvConexion;
    ConnectivityManager conexion;
    private ImageButton btnSaveFile;
    private Button btnOnLight;
    private Button btnOffLight;
    private Button btnOnBlu;
    private Button btnOffBlu;

    private static final int REQUEST_CODE_BLUETOOTH_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_CODE_BLUETOOTH_PERMISSION);
            }
        }
        setContentView(R.layout.activity_main);
        archivo = new Archivo(this, this);
        begin();
        batteryFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(broadcastReceiver, batteryFilter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        btnSaveFile = findViewById(R.id.btnSaveFile);
        btnOnLight = findViewById(R.id.btnOn);
        btnOffLight = findViewById(R.id.btnOff);
        btnOnBlu = findViewById(R.id.btnOn2);
        btnOffBlu = findViewById(R.id.btnOff2);

        btnSaveFile.setOnClickListener(this::onSaveFileClick);
        btnOffLight.setOnClickListener(this::offLight);
        btnOnLight.setOnClickListener(this::onLight);
        btnOffBlu.setOnClickListener(this::turnOffBluetooth);
        btnOnBlu.setOnClickListener(this::turnOnBluetooth);
    }

    private void begin() {
        this.context = getApplicationContext();
        this.activity = this;
        this.VersionAndroid = findViewById(R.id.tvVersionAndroid);
        this.pbLevelBaterry = findViewById(R.id.pbLevelBatery);
        this.tvLevelBaterry = findViewById(R.id.tvLevelBaterryLB);
        this.namefile = findViewById(R.id.etNameFile);
        this.tvConexion = findViewById(R.id.tvConexion);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String versionSO = Build.VERSION.RELEASE;
        versionSDK = Build.VERSION.SDK_INT;
        VersionAndroid.setText(("versionSO:" + versionSO + "/SDK:" + versionSDK));
        checkConnection();
    }

    private void onLight(View view) {
        cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            cameraId = cameraManager.getCameraIdList()[0];
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
        try {
            cameraManager.setTorchMode(cameraId, true);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    private void offLight(View view) {
        try {
            cameraManager.setTorchMode(cameraId, false);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int levelBattery = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            pbLevelBaterry.setProgress(levelBattery);
            tvLevelBaterry.setText("Nivel de la Batería:" + levelBattery + " %");
        }
    };

    private void checkConnection() {
        conexion = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = conexion.getActiveNetworkInfo();
        boolean stateNet = network != null && network.isConnectedOrConnecting();
        if (stateNet) tvConexion.setText(" state ON");
        else tvConexion.setText(" state OFF");
    }

    private void onSaveFileClick(View view) {
        String fileName = namefile.getText().toString();
        String fileContent = namefile.getText().toString(); // Obtener el contenido del EditText

        if (!fileName.isEmpty() && !fileContent.isEmpty()) {
            archivo.guardarArchivo(fileName, fileContent);
        } else {
            Toast.makeText(this, "El nombre del archivo y el contenido no pueden estar vacíos", Toast.LENGTH_SHORT).show();
        }
    }

    private void turnOffBluetooth(View view) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            if (mBluetoothAdapter.isEnabled()) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, REQUEST_CODE_BLUETOOTH_PERMISSION);
                    return;
                }
                mBluetoothAdapter.disable();
                Toast.makeText(this, "Bluetooth apagado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Bluetooth ya está apagado", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "El dispositivo no admite Bluetooth", Toast.LENGTH_SHORT).show();
        }
    }

    private void turnOnBluetooth(View view) {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled()) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, REQUEST_CODE_BLUETOOTH_PERMISSION);
                    return;
                }
                mBluetoothAdapter.enable();
                Toast.makeText(this, "Bluetooth encendido", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Bluetooth ya está encendido", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "El dispositivo no admite Bluetooth", Toast.LENGTH_SHORT).show();
        }
    }
}