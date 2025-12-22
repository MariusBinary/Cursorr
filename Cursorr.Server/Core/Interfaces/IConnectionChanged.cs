using Cursorr.Core.Models;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;

namespace Cursorr.Core.Interfaces
{
    public enum ServerEvent
    {
        STARTED,
        STOPPED,
    }

    public interface IConnectionChanged
    {
        void OnServerEvent(ServerEvent action);
        void OnClientRequest(ClientInfo client, IPEndPoint source);
        void OnClientConnected(ClientInfo client);
        void OnClientDisconnected(ClientInfo client);
    }
}
