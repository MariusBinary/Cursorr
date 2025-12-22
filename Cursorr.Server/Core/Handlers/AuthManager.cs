using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using Cursorr.Core.Models;

namespace Cursorr.Core.Handlers
{
    public class AuthManager
    {
        private List<ClientInfo> mClients;

        public AuthManager()
        {
            string serializedClients = Properties.Settings.Default.AllowedClients;

            if (!String.IsNullOrEmpty(serializedClients)) {
                mClients = JsonConvert.DeserializeObject<List<ClientInfo>>(serializedClients);
            } else {
                mClients = new List<ClientInfo>();
            }
        }
        
        public void AddDevice(ClientInfo client)
        {
            mClients.Add(client);

            Properties.Settings.Default.AllowedClients = JsonConvert.SerializeObject(mClients);
            Properties.Settings.Default.Save();
        }

        public List<ClientInfo> GetDevices()
        {
            return mClients;
        }

        public ClientInfo GetDevice(string address)
        {
            foreach (ClientInfo client in mClients) {
                if (client.mAddress.Equals(address)) {
                    return client;
                }
            }

            return null;
        }

        public void RemoveDevice(ClientInfo client)
        {
            mClients.Remove(client);

            Properties.Settings.Default.AllowedClients = JsonConvert.SerializeObject(mClients);
            Properties.Settings.Default.Save();
        }

        public void RemoveDevice(String address)
        {
            foreach (ClientInfo client in mClients) {
                if (client.mAddress.Equals(address)) {
                    RemoveDevice(client);
                    break;
                }
            }
        }
    }
}
