using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Cursorr.Core.Geometry
{
    /**
     * Class to represent a point in 2-D Cartesian coordinate system.
     */
    public class Cartesian2D
    {
        private double x;
        private double y;

        /**
         * Constructor.
         *
         * Initializes this <code>Cartesian2D</code> point.
         * @param x the x-coordinate.
         * @param y the y-coordinate.
         */
        public Cartesian2D(double x, double y)
        {
            this.x = x;
            this.y = y;
        }

        /**
         * Returns the x-coordinate of this point.
         *
         * @return thw x-coordinate.
         */
        public double getX() { return this.x; }

        /**
         * Returns the y-coordinate of this point.
         *
         * @return the y-coordinate.
         */
        public double getY() { return this.y; }

        /**
         * Sets the x-coordinate of this point.
         *
         * @param x the x-coordinate.
         */
        public void setX(double x) { this.x = x; }

        /**
         * Sets the x-coordinate of this point.
         *
         * @param y the x-coordinate.
         */
        public void setY(double y) { this.y = y; }
    }
}
