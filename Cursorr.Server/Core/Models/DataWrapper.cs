using System;
using Cursorr.Core.Geometry;
using Newtonsoft.Json;

namespace Cursorr.Core.Models
{
    public class DataWrapper
    {
        public int mPacketCount = 0;

        public String mOperationType { get; set; }
        public String mData { get; set; }
        public Quaternion mQuaternion { get; set; }

        public bool mIsInitQuat { get; set; }
        public int mMoveX { get; set; }
        public int mMoveY { get; set; }
        public float mSensitivity { get; set; }

        /**
         * Returns a JSon-string of an object.
         *
         * @param object the input {@link java.lang.Object}.
         * @return a JSon-string
         * @see com.google.gson.Gson
         */
        public String getGsonString(object obj)
        {
            return JsonConvert.SerializeObject(obj);
        }

        /**
         * Returns the <i>type of operation</i> field of this
         * <code>DataWrapper</code>.
         *
         * @return the <i>type of operation</i> field.
         */
        public String getOperationType() { return mOperationType; }

        /**
         * Returns the <i>data</i> field of this <code>DataWrapper</code>
         * for keyboard and mouse button click operations.
         *
         * @return the <i>data</i> field.
         */
        public String getData() { return mData; }

        /**
         * Checks if the stored {@link project.pc.sensor.representation.Quaternion}
         * object is the initial one.
         *
         * @return <code>true</code>, if it is initial, <br/>
         *         <code>false</code>, otherwise.
         */
        public bool isInitQuat() { return mIsInitQuat; }

        /**
         * Returns the {@link project.pc.sensor.representation.Quaternion}
         * object stored in this <code>DataWrapper</code>.
         *
         * @return the {@link project.pc.sensor.representation.Quaternion}
         *         object.
         */
        public Quaternion getQuaternionObject() { return mQuaternion; }

        /**
         * Returns the displacement value along x-axis stored in this
         * <code>DataWrapper</code>.
         *
         * @return the displacement along x-axis.
         */
        public int getX() { return mMoveX; }

        /**
         * Returns the displacement value along y-axis stored in this
         * <code>DataWrapper</code>.
         *
         * @return the displacement along y-axis.
         */
        public int getY() { return mMoveY; }

        /**
         * Returns the mouse movement sensitivity value stored in this
         * <code>DataWrapper</code>.
         *
         * @return the mouse movement sensitivity.
         */
        public float getSensitivity() { return mSensitivity; }
    }
}
