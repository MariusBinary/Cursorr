using System.Net;
using System.Text;
using System.Net.Sockets;
using System.Threading;
using Newtonsoft.Json;
using System;
using Cursorr.Core.Interfaces;
using Cursorr.Core.Controllers;
using Cursorr.Core.Models;

namespace Cursorr.Core.Handlers
{
    public class DataListener
    {
        // Informazion sul server di ascolto.
        private UdpClient socket;
        private int udpPort = 5394;
        private MouseController mMouseController;
        private VolumeController mVolumeController;
        private CancellationTokenSource mCancellationToken;
        private Thread listeningThread;
        private IConnectionChanged mServerEvent;
        private Random mRandom;
        private bool mIsRunning;

        // Informazioni sul cliente corrente.
        private int mClientToken;
        private ClientInfo mClientInfo;
        private IPEndPoint mClientEndpoint;
        private long mClientLastSignal;

        public DataListener(IConnectionChanged connectionChanged)
        {
            mMouseController = new MouseController();
            mVolumeController = new VolumeController();
            mServerEvent = connectionChanged;
            mRandom = new Random();
        }

        public void Run()
        {
            IPEndPoint endPoint = new IPEndPoint(IPAddress.Any, udpPort);
            socket = new UdpClient(endPoint);
            socket.Client.ReceiveTimeout = 2000;
            IPEndPoint source = new IPEndPoint(0, 0);
            mCancellationToken = new CancellationTokenSource();
            mClientToken = 0;

            listeningThread = new Thread(() =>
            {
                mIsRunning = true;
                mServerEvent?.OnServerEvent(ServerEvent.STARTED);

                while (!mCancellationToken.Token.IsCancellationRequested)
                {
                    try
                    {
                        byte[] byteData = socket.Receive(ref source);
                        string data = Encoding.ASCII.GetString(byteData, 0, byteData.Length);
                        DataWrapper dataWrapper = JsonConvert.DeserializeObject<DataWrapper>(data);

                        if (dataWrapper != null)
                        {
                            switch (dataWrapper.getOperationType())
                            {
                                case "Stop":
                                    DisconnectClient(mClientInfo);
                                    break;

                                case "Permission":
                                    var client = JsonConvert.DeserializeObject<ClientInfo>(dataWrapper.mData);
                                    client.mAddress = source.Address.ToString();
                                    mServerEvent?.OnClientRequest(client, source);
                                    break;

                                case "Mouse_Move":
                                    if (!source.Address.Equals(mClientEndpoint.Address)) break;
                                    mMouseController.move(
                                        dataWrapper.getQuaternionObject(),
                                        dataWrapper.isInitQuat(),
                                        dataWrapper.getSensitivity());
                                    break;

                                case "Mouse_Touch":
                                    if (!source.Address.Equals(mClientEndpoint.Address)) break;
                                    mMouseController.move(
                                        dataWrapper.getX(),
                                        dataWrapper.getY(),
                                        dataWrapper.getSensitivity());
                                    break;

                                case "Mouse_Scroll":
                                    if (!source.Address.Equals(mClientEndpoint.Address)) break;
                                    mMouseController.scroll(
                                        dataWrapper.getX(),
                                        dataWrapper.getY(),
                                        dataWrapper.getSensitivity());
                                    break;

                                case "Mouse_Button":
                                    if (!source.Address.Equals(mClientEndpoint.Address)) break;
                                    mMouseController.doOperation(
                                        dataWrapper.getData());
                                    break;

                                case "Hardware_Button":
                                    if (!source.Address.Equals(mClientEndpoint.Address)) break;
                                    mVolumeController.doOperation(
                                        dataWrapper.getData());
                                    break;
                            }
                        }
                    }
                    catch (Exception)
                    {
                    }

                    // Send the alive signal.
                    long milliseconds = DateTimeOffset.Now.ToUnixTimeMilliseconds();
                    if (milliseconds - mClientLastSignal > 10000) {
                        if (mClientToken != 0) {
                            SendData(mClientEndpoint, "Alive");
                        }
                        mClientLastSignal = milliseconds;
                    }
                }

                mIsRunning = false;
                mServerEvent?.OnServerEvent(ServerEvent.STOPPED);
            });

            listeningThread.Start();
        }

        public void Close()
        {
            if (mClientToken != 0) {
                SendData(mClientEndpoint, "Stop");
                DisconnectClient(mClientInfo);
            }

            mCancellationToken.Cancel();
            socket.Close();
        }

        public void SetClientPermission(ClientInfo client, IPEndPoint endpoint, bool allowed)
        {
            if (allowed) {
                mClientInfo = client;
                mClientEndpoint = endpoint;
                mClientLastSignal = DateTimeOffset.Now.ToUnixTimeMilliseconds();
                mClientToken = mRandom.Next(1, 16);

                // TODO: Callback client updated.
                mServerEvent?.OnClientConnected(mClientInfo);
            }

            // Invia la risposta di accesso al dispositivo.
            SendData(endpoint, allowed ? "Allowed" : "Denied");
        }

        public void DisconnectClient(String ipAddress, bool alertClient)
        {
            if (mClientInfo == null) return;
            if (!ipAddress.Equals(mClientInfo.mAddress)) return;

            // Alert the client.
            if (alertClient) SendData(mClientEndpoint, "Stop");

            // TODO: Callback client updated.
            mServerEvent?.OnClientDisconnected(mClientInfo);

            mClientInfo = null;
            mClientEndpoint = null;
            mClientLastSignal = 0;
            mClientToken = 0;
        }

        public void DisconnectClient(ClientInfo client)
        {
            DisconnectClient(client.mAddress, false);
        }

        public void SendData(IPEndPoint source, String data)
        {
            byte[] txData = Encoding.ASCII.GetBytes(data);
            socket.Send(txData, txData.Length, source);
        }

        public bool IsRunning()
        {
            return mIsRunning;
        }
    }
}