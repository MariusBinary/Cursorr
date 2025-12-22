package mariusbinary.cursorr.network.models;

import com.google.gson.Gson;

import mariusbinary.cursorr.mouse.representation.Quaternion;

public class DataWrapper {

    private String mOperationType;
    private String mData;
    private Quaternion mQuaternion;

    private boolean mIsInitQuat;
    private int mMoveX;
    private int mMoveY;
    private float mSensitivity;

    public DataWrapper(String operationType, String data) {
        mOperationType = operationType;
        mData = data;
    }

    public DataWrapper(int x, int y, float sensitivity) {
        mMoveX = x;
        mMoveY = y;
        mSensitivity = sensitivity;
        mOperationType = "Mouse_Touch";
    }

    public DataWrapper(int x, int y, float sensitivity, boolean scroll) {
        mMoveX = x;
        mMoveY = y;
        mSensitivity = sensitivity;
        mOperationType = "Mouse_Scroll";
    }

    public DataWrapper(Quaternion quaternionObject, boolean isInitQuat, float sensitivity) {
        mOperationType = "Mouse_Move";
        mSensitivity = sensitivity;
        mIsInitQuat = isInitQuat;
        mQuaternion = quaternionObject;
    }

    public static String getGsonString(Object object) {
        return new Gson().toJson(object);
    }

    public String getOperationType() { return mOperationType; }

    public String getData() { return mData; }

    public boolean isInitQuat() { return mIsInitQuat; }

    public Quaternion getQuaternionObject() { return mQuaternion; }

    public int getX() { return mMoveX; }

    public int getY() { return mMoveY; }

    public float getSensitivity() { return mSensitivity; }
}