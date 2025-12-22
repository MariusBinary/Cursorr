using System;
using System.Net;

namespace Cursorr.Core.Network
{
    public static class NetworkUtils
    {
        public static string GetIPAddress()
        {
            string IPAddress = string.Empty;
            string Hostname = Environment.MachineName;
            IPHostEntry Host = Dns.GetHostEntry(Hostname);
            foreach (IPAddress IP in Host.AddressList)
            {
                if (IP.AddressFamily == System.Net.Sockets.AddressFamily.InterNetwork)
                {
                    IPAddress = Convert.ToString(IP);
                }
            }
            return IPAddress;
        }
    }
}
