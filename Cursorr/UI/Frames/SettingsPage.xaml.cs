using Cursorr.Core.Interfaces;
using ImmersiveLights.Core;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace Cursorr.UI.Frames
{
    public partial class SettingsPage : Page
    {
        IPageNavigation navigation;
        private bool loadingPreferences;

        public SettingsPage(IPageNavigation navigation)
        {
            InitializeComponent();
            this.navigation = navigation;

            loadingPreferences = true;

            CBox_StartWithWindows.IsChecked = Registry.IsAddedToStartup();
            UpdateStartAsIcon(CBox_StartWithWindows.IsChecked.Value);
            CBox_StartAsIcon.IsChecked = Properties.Settings.Default.StartReducedToIcon;
            CBox_ReduceAsIcon.IsChecked = Properties.Settings.Default.WorkInBackground;

            loadingPreferences = false;
        }

        private void Tab_StartWithWindows_MouseLeftButtonUp(object sender, MouseButtonEventArgs e)
        {
            CBox_StartWithWindows.IsChecked = !CBox_StartWithWindows.IsChecked;
        }

        private void Tab_ReduceAsIcon_MouseLeftButtonUp(object sender, MouseButtonEventArgs e)
        {
            CBox_ReduceAsIcon.IsChecked = !CBox_ReduceAsIcon.IsChecked;
        }

        private void CBox_StartWithWindows_Checked(object sender, RoutedEventArgs e)
        {
            if (loadingPreferences) return;

            Registry.AddToStartup();
            UpdateStartAsIcon(true);
        }

        private void CBox_ReduceAsIcon_Checked(object sender, RoutedEventArgs e)
        {
            if (loadingPreferences) return;

            Properties.Settings.Default.WorkInBackground = CBox_ReduceAsIcon.IsChecked.Value;
            Properties.Settings.Default.Save();
        }

        private void CBox_StartWithWindows_Unchecked(object sender, RoutedEventArgs e)
        {
            if (loadingPreferences) return;

            Registry.RemoveFromStartup();
            UpdateStartAsIcon(false);
        }

        private void UpdateStartAsIcon(bool enabled)
        {
            Tab_StartAsIcon.IsEnabled = enabled;
            Tab_StartAsIcon.Opacity = enabled ? 1.0 : 0.6;
        }

        private void CBox_ReduceAsIcon_Unchecked(object sender, RoutedEventArgs e)
        {
            if (loadingPreferences) return;

            Properties.Settings.Default.WorkInBackground = false;
            Properties.Settings.Default.Save();
        }

        private void Tab_StartAsIcon_MouseLeftButtonUp(object sender, MouseButtonEventArgs e)
        {
            CBox_StartAsIcon.IsChecked = !CBox_StartAsIcon.IsChecked;
        }

        private void CBox_StartAsIcon_Unchecked(object sender, RoutedEventArgs e)
        {
            Properties.Settings.Default.StartReducedToIcon = false;
            Properties.Settings.Default.Save();
        }

        private void CBox_StartAsIcon_Checked(object sender, RoutedEventArgs e)
        {
            Properties.Settings.Default.StartReducedToIcon = true;
            Properties.Settings.Default.Save();
        }
    }
}
