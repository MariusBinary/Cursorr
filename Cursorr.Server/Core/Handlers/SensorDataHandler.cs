using System;
using Cursorr.Core.Geometry;

namespace Cursorr.Core.Handlers
{
	public class SensorDataHandler
	{
		private float mSensitivity;
		private double mHcos;
		private double mHsin;
		private double mBli;

		/**
		 * Constructor.
		 *
		 * Initializes this <code>SensorDataHandler</code>.
		 *
		 * @param q initial <code>Quaternion</code> object.
		 * @param sensitivity sensitivity co-efficient of the mouse movement.
		 */
		public SensorDataHandler(Quaternion q, float sensitivity)
		{
			mSensitivity = sensitivity;
			initFix(q);
		}

		//Baseline Initialization
		private void initFix(Quaternion quaternion)
		{
			Vector3f xvec = quaternion.rotateVector(new Vector3f(1.0f, 0.0f, 0.0f));
			Vector3f zvec = quaternion.rotateVector(new Vector3f(0.0f, 0.0f, 1.0f));
			double blh = -1.0f * Math.Atan2(xvec.getY(), xvec.getX());
			mHcos = Math.Cos(blh);
			mHsin = Math.Sin(blh);
			mBli = Math.Asin(zvec.getY());
		}

		/**
		 *
		 * Determines pointer location from current <code>Quaternion</code>
		 * object.
		 * 
		 * @param currentQuaternion current <code>Quaternion</code> object acquired
		 *                          after filtering.
		 * @return an object of {@link project.pc.sensor.representation.Cartesian2D}
		 *         containing current pointer location.
		 */
		public Cartesian2D pointerUpdate(Quaternion currentQuaternion)
		{
			Vector3f cxv = currentQuaternion.rotateVector(new Vector3f(1.0f, 0.0f, 0.0f));
			Vector3f czv = currentQuaternion.rotateVector(new Vector3f(0.0f, 0.0f, 1.0f));
			double xeff = (mHcos * cxv.getX()) - (mHsin * cxv.getY());
			double yeff = (mHsin * cxv.getX()) + (mHcos * cxv.getY());
			double hdel = Math.Atan2(yeff, xeff);
			double cli = Math.Asin(czv.getY());
			double yEst = mSensitivity * Math.Tan(mBli - cli);
			double xEst = mSensitivity * Math.Tan(hdel);
			initFix(currentQuaternion);
			return new Cartesian2D(xEst, yEst);
		}
	}
}
