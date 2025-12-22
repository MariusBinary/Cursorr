using System;
using System.Windows;
using System.Runtime.InteropServices;
using Cursorr.Core.Handlers;
using Cursorr.Core.Geometry;

namespace Cursorr.Core.Controllers
{
    public class MouseController
    {
        private SensorDataHandler mSensorDataHandler;
        private static Quaternion sQuaternion = new Quaternion();

        private const int MOUSEEVENTF_LEFTDOWN = 0x0002;
        private const int MOUSEEVENTF_LEFTUP = 0x0004;
        private const int MOUSEEVENTF_RIGHTDOWN = 0x0008;
        private const int MOUSEEVENTF_RIGHTUP = 0x0010;
        private const int MOUSEEVENTF_MIDDLEDOWN = 0x0020;
        private const int MOUSEEVENTF_MIDDLEUP = 0x0040;
        private const int MOUSEEVENTF_WHEEL = 0x0800;
        private const int MOUSEEVENTF_HWHEEL = 0x1000;

        [DllImport("User32.dll")]
        private static extern bool SetCursorPos(int X, int Y);

        [DllImport("user32.dll")]
        [return: MarshalAs(UnmanagedType.Bool)]
        internal static extern bool GetCursorPos(ref Win32Point pt);

        [StructLayout(LayoutKind.Sequential)]
        internal struct Win32Point
        {
            public Int32 X;
            public Int32 Y;
        };

        public static Point GetMousePosition()
        {
            var w32Mouse = new Win32Point();
            GetCursorPos(ref w32Mouse);

            return new Point(w32Mouse.X, w32Mouse.Y);
        }

        [DllImport("user32.dll")]
        public static extern void mouse_event(int dwFlags, int dx, int dy, int dwData, int dwExtraInfo);


        private void ClickLeftMouseButtonMouseEvent()
        {
            mouse_event(MOUSEEVENTF_LEFTDOWN, 0, 0, 0, 0);
            mouse_event(MOUSEEVENTF_LEFTUP, 0, 0, 0, 0);
        }

        private void ClickRightMouseButtonMouseEvent()
        {
            mouse_event(MOUSEEVENTF_RIGHTDOWN, 0, 0, 0, 0);
            mouse_event(MOUSEEVENTF_RIGHTUP, 0, 0, 0, 0);
        }

        private void ClickMiddleMouseButtonMouseEvent()
        {
            mouse_event(MOUSEEVENTF_MIDDLEDOWN, 0, 0, 0, 0);
            mouse_event(MOUSEEVENTF_MIDDLEUP, 0, 0, 0, 0);
        }

        /**
         * Method for performing mouse movement proportional to the
         * screen size. This method is invoked by
         * {@link MouseController#move(int, int, float)}.
         *
         * @param robot the {@link java.awt.Robot} object.
         * @param relativeX displacement along x-axis.
         * @param relativeY displacement along y-axis.
         * @param sensitivity mouse movement sensitivity.
         */
        void moveRelatively(int relativeX, int relativeY, float sensitivity)
        {
            Point mousePosition = GetMousePosition();
            double curX = mousePosition.X;
            double curY = mousePosition.Y;
            int moveX = (int)(curX + (/*ratio*/sensitivity * relativeX));
            int moveY = (int)(curY + (/*ratio*/sensitivity * relativeY));
            moveX = moveX < 0 ? 0 : moveX;
            moveY = moveY < 0 ? 0 : moveY;
            SetCursorPos(moveX, moveY);
        }

        public void scroll(int relativeX, int relativeY, float sensitivity)
        {
            int scrollX = (int)(sensitivity * relativeY);
            int scrollY = (int)(sensitivity * relativeY);

            /*
            if (scrollX != 0) {
                mouse_event(MOUSEEVENTF_HWHEEL, 0, 0, scrollX, 0);
            }
            */

            if(scrollY != 0) {
                mouse_event(MOUSEEVENTF_WHEEL, 0, 0, scrollY, 0);
            }
        }

        /**
         * Method to perform mouse move on the basis of 2-D input from
         * the mobile device.
         *
         * @param relativeX displacement along x-axis.
         * @param relativeY displacement along y-axis.
         */
        public void move(int relativeX, int relativeY, float sensitivity) { 
            moveRelatively(relativeX, relativeY, sensitivity);
        }

        /**
         * Method to perform mouse button click operations.
         * <p>
         *     <i>viz.</i>
         *     <ul>
         *         <li>Left click.</li>
         *         <li>Right click.</li>
         *         <li>Middle click.</li>
         *         <li>Scrolling</li>
         *     </ul>
         * </p>
         *
         * @param operation <code>String</code> representing the type
         *                  of operation.
         */
        public void doOperation(String operation)
        {
            switch (operation)
            {
                case "left":
                    ClickLeftMouseButtonMouseEvent();
                    break;

                case "right":
                    ClickRightMouseButtonMouseEvent();
                    break;

                case "middle":
                    ClickMiddleMouseButtonMouseEvent();
                    break;
            }
        }

        public void move(Quaternion quaternion, bool isInitQuat, float sensitivity)
        {
            sQuaternion = quaternion;
            if (isInitQuat) {
                mSensorDataHandler = new SensorDataHandler(quaternion, sensitivity);
            }

            // ERROR because sensibility values is sended only the first time android app launch.
            if (mSensorDataHandler == null) return;

            Cartesian2D cartesian2D = mSensorDataHandler.pointerUpdate(quaternion);
            Point mousePosition = GetMousePosition();

            var deltaX = cartesian2D.getX();
            var deltaY = cartesian2D.getY();

            double x = mousePosition.X;
            double y = mousePosition.Y;

            x += (Math.Abs(deltaX) >= 0.15) ? deltaX : 0;
            y += (Math.Abs(deltaY) >= 0.15) ? deltaY : 0;

            SetCursorPos((int)x, (int)y);
        }

        public static Quaternion getQuaternion()
        {
            return sQuaternion;
        }
    }
}
