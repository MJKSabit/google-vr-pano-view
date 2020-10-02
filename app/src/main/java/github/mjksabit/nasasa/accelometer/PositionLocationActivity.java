package github.mjksabit.nasasa.accelometer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class PositionLocationActivity extends AppCompatActivity {

    private static final String TAG = "PositionLocationAct";

    private Rotation rotation;
    private TextView compassText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_position_location);

        compassText = findViewById(R.id.compass_angle);
        setupRotation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "start rotation sensor");
        rotation.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        rotation.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        rotation.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "stop compass");
        rotation.stop();
    }

    private void setupRotation() {
        rotation = new Rotation(this);
        Rotation.RotationListener cl = getRotationListener();
        rotation.setListener(cl);
    }

    private Rotation.RotationListener getRotationListener() {
        return (azimuth, vAngle) -> runOnUiThread(() -> compassText.setText(String.valueOf(vAngle)));
    }
}