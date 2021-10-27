using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Media;

namespace Cursorr.UI.Models
{
    public class DeviceModel
    {
        public string Name {  get; set; }
        public string Address { get; set; }
        public string Status { get; set; }

        public SolidColorBrush StatusColor { 
            get
            {
                return (SolidColorBrush)new BrushConverter().ConvertFrom(
                    Status.Equals("Connected") ? "#56ab2f" : "#D54936");
            }
        }

        public bool Enabled
        {
            get
            {
                return Status.Equals("Connected");
            }
        }
    }
}
