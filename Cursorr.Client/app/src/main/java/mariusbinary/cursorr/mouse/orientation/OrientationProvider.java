package mariusbinary.cursorr.mouse.orientation;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mariusbinary.cursorr.mouse.representation.MatrixF4x4;
import mariusbinary.cursorr.mouse.representation.Quaternion;

public abstract class OrientationProvider implements SensorEventListener {

	final Object synchronizationToken = new Object();

	final MatrixF4x4 currentOrientationRotationMatrix;

    final Quaternion currentOrientationQuaternion;

	List<Sensor> mSensorList = new ArrayList<>();

	private SensorManager sensorManager;

    /**
     * Constructor.
     *
     * Initializes this <code>OrientationProvider</code>.
     *
     * @param sensorManager a <code>SensorManager</code> object.
     */
	OrientationProvider(SensorManager sensorManager) {
		this.sensorManager = sensorManager;

		// Initialise with identity
		currentOrientationRotationMatrix = new MatrixF4x4();

		// Initialise with identity
		currentOrientationQuaternion = new Quaternion();
	}

    /**
     * Checks if gyroscope is available in the device.
     *
     * @param activity the current <code>Activity</code>.
     * @return <code>true</code>, if gyroscope is available, <br/>
     *         <code>false</code>, otherwise.
     */
	public static boolean checkGyro(Activity activity) {
		SensorManager sensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);
        return sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE) != null;
    }

    /**
     * Starts the sensor fusion (e.g., when resuming the activity).
     */
	void sensorStart() {
		for (Sensor sensor : mSensorList) {
			sensorManager.registerListener(this, sensor,
					SensorManager.SENSOR_DELAY_GAME);
		}
	}

    /**
     * Stops the sensor fusion (e.g., when pausing/suspending the activity).
     */
	public void sensorStop() {
		for (Sensor sensor : mSensorList) {
			sensorManager.unregisterListener(this, sensor);
		}
	}

	public void getRotationMatrix(MatrixF4x4 matrix) {
		synchronized (synchronizationToken) {
			matrix.set(currentOrientationRotationMatrix);
		}
	}

	public void getQuaternion(Quaternion quaternion) {
		synchronized (synchronizationToken) {
			quaternion.set(currentOrientationQuaternion);
		}
	}

    /**
     * Gets the current rotation of the device.
     *
     * @param angles a float array representing the rotation.
     */
	public void getEulerAngles(float angles[]) {
		synchronized (synchronizationToken) {
			SensorManager.getOrientation(currentOrientationRotationMatrix.matrix, angles);
		}
	}
}