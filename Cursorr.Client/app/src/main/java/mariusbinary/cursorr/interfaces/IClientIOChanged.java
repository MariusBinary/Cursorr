package mariusbinary.cursorr.interfaces;

public interface IClientIOChanged {
  void onDeviceDisconnected();
  void onDeviceConnected();
  void onDeviceRefused();
}