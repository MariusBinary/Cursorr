package mariusbinary.cursorr.network.models;

public class ServerInfo {
    private String mId;
    private String mServerInfo;
    private String mAddress;
    private int mVersion;
    private boolean mHasVersion;
    private boolean mStopFlag;
    private boolean mAlive;
    private boolean mMemorized;

    public static final int SERVER_INFO_LENGTH = 600;

    // TODO: Change all references to mID instead of mAddress
    public ServerInfo(String serverInfo) {
        mServerInfo = serverInfo;
        mStopFlag = false;
        mHasVersion = false;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)    return true;
        if (obj == null)    return false;
        if (getClass() != obj.getClass())   return false;
        ServerInfo other = (ServerInfo) obj;
        return mAddress.equals(other.mAddress);
    }

    public void setServerAddress(String address) { mAddress = address; }

    public void setServerInfo(String mServerInfo) {
        this.mServerInfo = mServerInfo;
    }

    public boolean getStopFlag() { return mStopFlag; }

    public String getServerInfo() { return mServerInfo; }

    public String getAddress() { return mAddress; }

    public String getId() {
        return mId;
    }

    public int getVersion() {
        return mVersion;
    }

    public boolean hasVersion() {
        return mHasVersion;
    }

    public void setMemorized(boolean mMemorized) {
        this.mMemorized = mMemorized;
    }

    public void setAlive(boolean mAlive) {
        this.mAlive = mAlive;
    }

    public boolean isAlive() {
        return mAlive;
    }

    public boolean isMemorized() {
        return mMemorized;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public void setServerVersion(int mVersion) {
        this.mVersion = mVersion;
    }

    public void setServerHasVersion(boolean mHasVersion) {
        this.mHasVersion = mHasVersion;
    }
}