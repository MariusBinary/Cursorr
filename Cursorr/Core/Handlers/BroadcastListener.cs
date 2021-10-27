using System;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using Newtonsoft.Json;

namespace Cursorr.Core.Handlers
{
    public class BroadcastListener
    {
        private int udpPort = 1236;
        private ServerInfo mServerInfo;
        private UdpClient mSocket;
        private CancellationTokenSource mCancellationToken;

        private readonly int APP_VERISON = 1000;

        public BroadcastListener() 
        {
            mServerInfo = new ServerInfo(Environment.MachineName, APP_VERISON);
        }

        public void Run()
        {
            mCancellationToken = new CancellationTokenSource();
            mSocket = new UdpClient(udpPort);
            mSocket.EnableBroadcast = true;
            mSocket.BeginReceive(new AsyncCallback(BroadcastCallback), mSocket);
        }

        private void BroadcastCallback(IAsyncResult result)
        {
            UdpClient socket = result.AsyncState as UdpClient;
            IPEndPoint source = new IPEndPoint(0, 0);

            try
            {
                byte[] rxData = socket.EndReceive(result, ref source);
                var data = Encoding.UTF8.GetString(rxData);

                if (data.Equals("Cursorr:BroadcastRequest")) {
                    byte[] txData = Encoding.ASCII.GetBytes(JsonConvert.SerializeObject(mServerInfo));
                    socket.BeginSend(txData, txData.Length, source, null, socket);
                }
            } catch { }

            if (!mCancellationToken.Token.IsCancellationRequested) {
                socket.BeginReceive(new AsyncCallback(BroadcastCallback), socket);
            }
        }

        public void Close()
        {
            mCancellationToken.Cancel();
            mSocket.Close();
        }
    }
}