package mariusbinary.cursorr.network.models;

import java.util.UUID;

public class ClientInfo {

    private String mId;
    private String mName;

    public ClientInfo(String name) {
        mId = UUID.randomUUID().toString().replaceAll("-", "").toUpperCase();
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public String getId() {
        return mId;
    }
}
