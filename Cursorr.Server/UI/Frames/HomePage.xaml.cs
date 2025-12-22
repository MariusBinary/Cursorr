using System.Windows;
using System.Windows.Controls;
using System.Collections.ObjectModel;
using Cursorr.Core.Interfaces;
using Cursorr.Core.Network;
using Cursorr.UI.Models;
using System.Windows.Media;
using System.Threading.Tasks;
using Cursorr.Pages;
using System.Collections.Generic;

namespace Cursorr.UI.Frames
{
    public partial class HomePage : Page
    {
        private Window mWindow;
        private IPageNavigation navigation;
        private ObservableCollection<DeviceModel> savedDevices;

        public HomePage(Window window)
        {
            InitializeComponent();

            this.mWindow = window;
            this.navigation = (IPageNavigation)window;

            var ipAddress = NetworkUtils.GetIPAddress();
            Tb_IpAddress.Text = $"{ipAddress}";

            savedDevices = new ObservableCollection<DeviceModel>();
            dg_lyrics.ItemsSource = savedDevices;
        }

        private void Btn_Connect_Click(object sender, RoutedEventArgs e)
        {
            ((MainWindow)mWindow).ToggleConnect();
        }

        private void Btn_About_Click(object sender, RoutedEventArgs e)
        {
            navigation.OnPageChanged("about");
        }

        private void Btn_Settings_Click(object sender, RoutedEventArgs e)
        {
            navigation.OnPageChanged("settings");
        }

        private void Btn_DeviceDisconnect_Click(object sender, RoutedEventArgs e)
        {
            Button button = sender as Button;
            DeviceModel device = button.DataContext as DeviceModel;
            ((MainWindow)mWindow).DisconnectDevice(device);
        }

        private void Btn_DeviceRemove_Click(object sender, RoutedEventArgs e)
        {
            Button button = sender as Button;
            DeviceModel device = button.DataContext as DeviceModel;
            ((MainWindow)mWindow).RemoveDevice(device);
            OnClientRemoved(device);
        }

        #region Callbacks

        /// <summary>
        /// Lo stato del server è cambiato.
        /// </summary>
        public void OnServerEvent(ServerEvent action) {
            try
            {
                this.Dispatcher.Invoke(() =>
                {
                    switch (action)
                    {
                        case ServerEvent.STARTED:
                            Tb_ServerStatus.Text = FindResource("homeDeviceStarted") as string;
                            Tb_ServerStatus.Foreground = (SolidColorBrush)new BrushConverter().ConvertFrom("#caffbf");
                            Btn_Connect.Background = this.Resources["DisconnectButtonBrush"] as LinearGradientBrush;
                            ((TextBlock)((Grid)(Btn_Connect.Content)).Children[1]).Text = FindResource("homeButtonStarted") as string;
                            break;
                        case ServerEvent.STOPPED:
                            Tb_ServerStatus.Text = FindResource("homeDeviceStopped") as string;
                            Tb_ServerStatus.Foreground = (SolidColorBrush)new BrushConverter().ConvertFrom("#f28482");
                            Btn_Connect.Background = this.Resources["ConnectButtonBrush"] as LinearGradientBrush;
                            ((TextBlock)((Grid)(Btn_Connect.Content)).Children[1]).Text = FindResource("homeButtonStopped") as string;
                            break;
                    }
                });
            } catch (TaskCanceledException) { }
        }

        /// <summary>
        /// Le informazioni su un dispositivo sono state aggiornate.
        /// </summary>
        public void OnClientUpdate(List<DeviceModel> clients) 
        {
            savedDevices = new ObservableCollection<DeviceModel>(clients);
            dg_lyrics.ItemsSource = savedDevices;
        }

        /// <summary>
        /// Le informazioni su un dispositivo sono state aggiornate.
        /// </summary>
        public void OnClientRemoved(DeviceModel client)
        {
            savedDevices.Remove(client);
            dg_lyrics.ItemsSource = savedDevices;
        }
        #endregion
    }
}
