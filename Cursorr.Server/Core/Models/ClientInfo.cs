using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Cursorr.Core.Models
{
    public class ClientInfo
    {
        public String mName{ get; set; }
        public String mAddress { get; set; }
        public String mId { get; set; }

        public bool mAllowed { get; set; }

        public String getName() { return mName; }
        public String getId() { return mId; }
        public String getAddress() { return mAddress; }
    }
}