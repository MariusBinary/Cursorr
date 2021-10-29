using System;
using System.Net;
using System.Reflection;
using System.Windows;
using System.Windows.Controls;
using Cursorr.Core.Interfaces;
using Newtonsoft.Json.Linq;

namespace Cursorr.UI.Frames
{
    public partial class AboutPage : Page
    {
        private string downloadUrl = "https://cursorrapp.com/";

        public AboutPage(IPageNavigation navigation)
        {
            InitializeComponent();

            Btn_DownloadUpdate.Visibility = Visibility.Collapsed;

            string version = Assembly.GetExecutingAssembly().GetName().Version.ToString();
            Tx_AppVersion.Text = version;

            // TODO: Check for server start error, if port is used.
        }

        private async void Btn_CheckForUpdates_Click(object sender, RoutedEventArgs e)
        {
            try
            {
                using (WebClient wc = new WebClient())
                {
                    string updateResponse = await wc.DownloadStringTaskAsync("https://mariusbinary.altervista.org/updates?app=cursorr");
                    string currVersion = Assembly.GetExecutingAssembly().GetName().Version.ToString();
                    string newVersion = JObject.Parse(updateResponse)["version"].Value<string>();

                    if (!currVersion.Equals(newVersion))
                    {
                        Btn_DownloadUpdate.Visibility = Visibility.Visible;
                        Tx_NewVersion.Text = $"Download version {newVersion}";
                        downloadUrl = JObject.Parse(updateResponse)["link"].Value<string>();
                    }
                    else
                    {
                        MessageBox.Show("No updates available!");
                    }
                }
            } catch
            {
                MessageBox.Show("No updates available!");
            }
        }

        private void Btn_DownloadUpdate_Click(object sender, RoutedEventArgs e)
        {
            System.Diagnostics.Process.Start(downloadUrl);
        }
    }
}
