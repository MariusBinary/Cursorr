using System;
using System.Collections.Generic;
using System.Net;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using Cursorr.Core.Handlers;
using Cursorr.Core.Interfaces;
using Cursorr.Core.Models;
using Cursorr.UI.Frames;
using Cursorr.UI.Controls;
using Cursorr.UI.Models;
using ImmersiveLights.Core;

namespace Cursorr.Pages
{
	public partial class MainWindow : Window, IPageNavigation, IConnectionChanged
    {
        public static DataListener sDataListener;
        public static BroadcastListener sBroadcastListener;
        public static AuthManager sAuthManager;

        private HomePage mHomeFrame;
        private bool canClose;
        private bool tryToClose;

        #region Window

        public MainWindow()
        {
            InitializeComponent();

            // Ottiene i parametri di avvio.
            string[] args = Environment.GetCommandLineArgs();

            // Verifica sono presenti parametri di avvio validi.
            if (args.Length > 1)
            {
                if (args[1].Equals("-autorun"))
                {
                    // Spostare il programma in background in fase di avvio automatico se necessario.
                    bool shouldWindowBeMinimized = Properties.Settings.Default.StartReducedToIcon;
                    if (shouldWindowBeMinimized)
                    {
                        this.ShowInTaskbar = false;
                        this.Hide();
                    }
                }
            }

            // Aggiungere l'applicazione affinchè venga eseguita all'avvio di Windows.
            if (Properties.Settings.Default.FirstRun)
            {
                Registry.AddToStartup();
                Properties.Settings.Default.FirstRun = false;
                Properties.Settings.Default.Save();
            }

            sAuthManager = new AuthManager();
            sBroadcastListener = new BroadcastListener();
            sDataListener = new DataListener(this);
            mHomeFrame = new HomePage(this);

            sDataListener.Run();
            sBroadcastListener.Run();

            this.OnPageChanged("home");

            List<DeviceModel> models = new List<DeviceModel>();
            foreach (ClientInfo info in sAuthManager.GetDevices())
            {
                models.Add(new DeviceModel()
                {
                    Name = info.mName,
                    Address = info.mAddress,
                    Status = FindResource("homeAuthorizedCellDisconnected") as string
                });
            }
            mHomeFrame.OnClientUpdate(models);
        }

        private void Window_MouseDown(object sender, MouseButtonEventArgs e)
        {
            if (e.ChangedButton == MouseButton.Left) {
                DragMove();
            }
        }

        private void Window_Activated(object sender, EventArgs e)
        {
            Window_Border.BorderBrush = (SolidColorBrush)new BrushConverter().ConvertFrom("#F7971E");
        }

        private void Window_Deactivated(object sender, EventArgs e)
        {
            Window_Border.BorderBrush = (SolidColorBrush)new BrushConverter().ConvertFrom("#613B0B");
        }

        private void Window_Closing(object sender, System.ComponentModel.CancelEventArgs e)
        {
            e.Cancel = !canClose;

            // Verifica l'azione da eseguire alla pressione del pulsante di chiusura
            var shouldReduceAsIconTry = Properties.Settings.Default.WorkInBackground;
            if (shouldReduceAsIconTry && tryToClose != true)
            {
                this.ShowInTaskbar = false;
                this.Hide();
                return;
            }

            if (!canClose)
            {
                tryToClose = true;

                sBroadcastListener.Close();
                sDataListener.Close();
                e.Cancel = false;
            }
        }

        private void Btn_Minimize_Click(object sender, RoutedEventArgs e)
        {
            this.WindowState = WindowState.Minimized;
        }

        private void Btn_Close_Click(object sender, RoutedEventArgs e)
        {
            this.Close();
        }

        private void Btn_Back_Click(object sender, RoutedEventArgs e)
        {
            OnPageChanged("home");
        }

        public void OnPageChanged(string page)
        {
            switch(page)
            {
                case "home":
                    Frm_Content.Navigate(mHomeFrame);
                    Btn_Back.Visibility = Visibility.Collapsed;
                    Img_NavLogo.Visibility = Visibility.Visible;
                    break;
                case "settings":
                    Frm_Content.Navigate(new SettingsPage(this));
                    Btn_Back.Visibility = Visibility.Visible;
                    Img_NavLogo.Visibility = Visibility.Collapsed;
                    break;
                case "about":
                    Frm_Content.Navigate(new AboutPage(this));
                    Btn_Back.Visibility = Visibility.Visible;
                    Img_NavLogo.Visibility = Visibility.Collapsed;
                    break;
            }
        }
        #endregion

        #region Server

        public void OnServerEvent(ServerEvent action)
        {
            try
            {
                this.Dispatcher.Invoke(() =>
                {
                    switch (action)
                    {
                        case ServerEvent.STARTED:
                            TBar_Icon.IconSource = new BitmapImage(
                                new Uri("pack://application:,,,/Cursorr;component/Assets/ic_device_online.ico"));
                            break;
                        case ServerEvent.STOPPED:
                            TBar_Icon.IconSource = new BitmapImage(
                                new Uri("pack://application:,,,/Cursorr;component/Assets/ic_device_offline.ico"));
                            break;
                    }

                    mHomeFrame.OnServerEvent(action);
                });
            } catch (TaskCanceledException) { }
        }

        public void OnClientRequest(ClientInfo client, IPEndPoint source)
        {
            // Accetta subito il dispositivo se è già nella lista dei consentiti.
            var device = sAuthManager.GetDevice(source.Address.ToString());
            if (device != null) {
                sDataListener.SetClientPermission(client, source, true);
                return;
            }

            // Richiedi all'utente il permesso di connessione.
            this.Dispatcher.Invoke(() =>
            {
                var response = WpfMessageBox.Show(
                    FindResource("alertConnectionRequestTitle") as string,
                    string.Format(FindResource("alertConnectionRequestDescription") as string, client.mName),
                    MessageBoxButton.YesNo);

                if (response == MessageBoxResult.Yes) {
                    sAuthManager.AddDevice(client);
                    sDataListener.SetClientPermission(client, source, true);
                } else {
                    sDataListener.SetClientPermission(client, source, false);
                }
            });
        }

        public void OnClientConnected(ClientInfo client)
        {
            this.Dispatcher.Invoke(() => {
                List<DeviceModel> models = new List<DeviceModel>();
                foreach(ClientInfo info in sAuthManager.GetDevices())
                {
                    models.Add(new DeviceModel() { 
                        Name = info.mName,
                        Address = info.mAddress,
                        Status = info.mAddress == client.mAddress ?
                        FindResource("homeAuthorizedCellConnected") as string :
                        FindResource("homeAuthorizedCellDisconnected") as string
                    });
                }
                mHomeFrame.OnClientUpdate(models);
            });
        }

        public void OnClientDisconnected(ClientInfo client)
        {
            this.Dispatcher.Invoke(() => {
                List<DeviceModel> models = new List<DeviceModel>();
                foreach (ClientInfo info in sAuthManager.GetDevices())
                {
                    models.Add(new DeviceModel()
                    {
                        Name = info.mName,
                        Address = info.mAddress,
                        Status = FindResource("homeAuthorizedCellDisconnected") as string
                    });
                }
                mHomeFrame.OnClientUpdate(models);
            });
        }

        public void ToggleConnect()
        {
            if (sDataListener.IsRunning()) {
                sBroadcastListener.Close();
                sDataListener.Close();
            } else {
                sBroadcastListener.Run();
                sDataListener.Run();
            }
        }

        public void DisconnectDevice(DeviceModel model)
        {
            if (sDataListener.IsRunning()) {
                sDataListener.DisconnectClient(model.Address, true);
            }
        }

        public void RemoveDevice(DeviceModel model)
        {
            if (sDataListener.IsRunning()) {
                sDataListener.DisconnectClient(model.Address, true);
                sAuthManager.RemoveDevice(model.Address);
            }
        }

        #endregion

        #region Taskbar

        private void TBar_Icon_TrayMouseDoubleClick(object sender, RoutedEventArgs e)
        {
            this.ShowInTaskbar = true;
            this.Show();
        }

        private void Menu_Open_Click(object sender, RoutedEventArgs e)
        {
            this.ShowInTaskbar = true;
            this.Show();
        }

        private void Menu_Settings_Click(object sender, RoutedEventArgs e)
        {
            Menu_Open_Click(null, null);
            OnPageChanged("settings");
        }

        private void Menu_About_Click(object sender, RoutedEventArgs e)
        {
            Menu_Open_Click(null, null);
            OnPageChanged("about");
        }

        private void Menu_Ouit_Click(object sender, RoutedEventArgs e)
        {
            tryToClose = true;
            this.Close();
        }

        #endregion

        private void TBar_Icon_TrayLeftMouseUp(object sender, RoutedEventArgs e)
        {
            this.ShowInTaskbar = true;
            this.Show();
        }
    }
}
