using System;
using System.Windows;

namespace Cursorr.UI.Controls
{
    public partial class WpfInputBox : Window
    {
        private WpfInputBox()
        {
            InitializeComponent();
        }

        static WpfInputBox _messageBox;
        static string _result;

        public static string Show(string caption, string text)
        {
            _messageBox = new WpfInputBox
            {
                txtInput = { Text = text }, 
                Title = $"Cursorr", 
                MessageTitle = { Text = caption } 
            };

            _messageBox.ShowDialog();
            return _result;
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            if (sender == btnOk)
            {
                if (String.IsNullOrEmpty(txtInput.Text)) {
                    txtError.Visibility = Visibility.Visible;
                } else {
                    _result = txtInput.Text;
                    _messageBox.Close();
                    _messageBox = null;
                }
            }
            else if (sender == btnCancel)
            {
                _result = string.Empty;
                _messageBox.Close();
                _messageBox = null;
            }
        }

        private void txtInput_TextChanged(object sender, System.Windows.Controls.TextChangedEventArgs e)
        {
            if (txtError.Visibility == Visibility.Visible) {
                txtError.Visibility = Visibility.Collapsed;
            }
        }
    }
}
