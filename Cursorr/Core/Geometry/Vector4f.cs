﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Cursorr.Core.Geometry
{

	/**
	 * Class to represent a four dimensional vector.
	 *
	 * <p>
	 *     It is defined as a point <i>(x,y,z,w)</i> in four dimensional
	 *     vector space. It is stored as a array of <code>float</code>s.
	 *     The four dimensions are known as X,Y,Z and W.
	 * </p>
	 * <p>
	 *     <b>Dot product of two vectors : </b> <br/>
	 *     <i>
	 *     (a<sub>1</sub>,b<sub>1</sub>,c<sub>1</sub>,d<sub>1</sub>) .
	 *     (a<sub>2</sub>,b<sub>2</sub>,c<sub>2</sub>,d<sub>2</sub>) =
	 *     a<sub>1</sub>a<sub>2</sub> + b<sub>1</sub>b<sub>2</sub> +
	 *     c<sub>1</sub>c<sub>2</sub> + d<sub>1</sub>d<sub>2</sub>
	 *     </i>.
	 * </p>
	 * <p>
	 *     <b>Product of a vector with a scalar : </b> <br/>
	 *     <i>(a, b, c, d) * s = (a * s, b * s, c * s, d * s)</i>.
	 * </p>
	 * <p>
	 *     <b>Normalization of a vector</b> <i>(a,b,c,d)</i> <br/>
	 *     &nbsp;&nbsp;&nbsp;&nbsp;
	 *     = <i>(a/mag, b/mag, c/mag, d/mag)</i>, <br/>
	 *     where, <br/> &nbsp;&nbsp;&nbsp;&nbsp;
	 *     <i>mag =
	 *     &radic;(a<sup>2</sup>+b<sup>2</sup>+c<sup>2</sup>+d<sup>2</sup>)</i>.
	 * </p>
	 *
	 * @see project.pc.sensor.representation.Vector3f
	 * @see project.pc.sensor.representation.Quaternion
	 */
	public class Vector4f
	{

		public float[] mPoints = { 0, 0, 0, 0 };

		/**
		 * Constructor.
		 *
		 * Creates the <code>Vector4f (x,y,z,w)</code>.
		 *
		 * @param x the X-dimension.
		 * @param y the Y-dimension.
		 * @param z the Z-dimension.
		 * @param w the W-dimension.
		 */
		public Vector4f(float x, float y, float z, float w)
		{
			this.mPoints[0] = x;
			this.mPoints[1] = y;
			this.mPoints[2] = z;
			this.mPoints[3] = w;
		}

		/**
		 * Constructor.
		 *
		 * Default constructor. Creates the <code>Vector4f (0,0,0,0)</code>.
		 */
		public Vector4f()
		{
			this.mPoints[0] = 0;
			this.mPoints[1] = 0;
			this.mPoints[2] = 0;
			this.mPoints[3] = 0;
		}

		/**
		 * Constructor.
		 *
		 * Creates the <code>Vector4f (vector3f.getX(), vector3f.getY(),
		 * vector3f.getZ(), w)</code>.
		 *
		 * @param vector3f a {@link Vector3f}.
		 * @param w the w-dimension.
		 */
		public Vector4f(Vector3f vector3f, float w)
		{
			this.mPoints[0] = vector3f.getX();
			this.mPoints[1] = vector3f.getY();
			this.mPoints[2] = vector3f.getZ();
			this.mPoints[3] = w;
		}

		/**
		 * Returns the array representation of this <code>Vector4f</code>.
		 *
		 * @return the array <code>[ X, Y, Z, W ]</code>.
		 */
		public float[] array()
		{
			return mPoints;
		}

		/**
		 * Copies another <code>Vector4f</code> to this one.
		 *
		 * @param vec the <code>Vector4f</code> to be copied.
		 */
		public void copyVec4(Vector4f vec)
		{
			this.mPoints[0] = vec.mPoints[0];
			this.mPoints[1] = vec.mPoints[1];
			this.mPoints[2] = vec.mPoints[2];
			this.mPoints[3] = vec.mPoints[3];
		}

		public void add(Vector4f vector)
		{
			this.mPoints[0] += vector.mPoints[0];
			this.mPoints[1] += vector.mPoints[1];
			this.mPoints[2] += vector.mPoints[2];
			this.mPoints[3] += vector.mPoints[3];
		}

		public void add(Vector3f vector, float w)
		{
			this.mPoints[0] += vector.getX();
			this.mPoints[1] += vector.getY();
			this.mPoints[2] += vector.getZ();
			this.mPoints[3] += w;
		}

		public void subtract(Vector4f vector)
		{
			this.mPoints[0] -= vector.mPoints[0];
			this.mPoints[1] -= vector.mPoints[1];
			this.mPoints[2] -= vector.mPoints[2];
			this.mPoints[3] -= vector.mPoints[3];
		}

		public void subtract(Vector4f vector, Vector4f output)
		{
			output.setXYZW(this.mPoints[0] - vector.mPoints[0], this.mPoints[1] - vector.mPoints[1], this.mPoints[2]
					- vector.mPoints[2], this.mPoints[3] - vector.mPoints[3]);
		}

		public void subdivide(Vector4f vector)
		{
			this.mPoints[0] /= vector.mPoints[0];
			this.mPoints[1] /= vector.mPoints[1];
			this.mPoints[2] /= vector.mPoints[2];
			this.mPoints[3] /= vector.mPoints[3];
		}

		/**
		 * Multiples this <code>Vector4f</code> with a scalar.
		 * .
		 * @param scalar the scalar.
		 */
		public void multiplyByScalar(float scalar)
		{
			this.mPoints[0] *= scalar;
			this.mPoints[1] *= scalar;
			this.mPoints[2] *= scalar;
			this.mPoints[3] *= scalar;
		}

		/**
		 * Performs the dot product of two vectors.
		 *
		 * @param input the <code>Vector4f</code> to be multiplied.
		 * @return result of <code>(this . input)</code>.
		 */
		public float dotProduct(Vector4f input)
		{
			return this.mPoints[0] * input.mPoints[0] + this.mPoints[1] * input.mPoints[1] + this.mPoints[2] * input.mPoints[2]
					+ this.mPoints[3] * input.mPoints[3];
		}

		public void lerp(Vector4f input, Vector4f output, float t)
		{
			output.mPoints[0] = (mPoints[0] * (1.0f * t) + input.mPoints[0] * t);
			output.mPoints[1] = (mPoints[1] * (1.0f * t) + input.mPoints[1] * t);
			output.mPoints[2] = (mPoints[2] * (1.0f * t) + input.mPoints[2] * t);
			output.mPoints[3] = (mPoints[3] * (1.0f * t) + input.mPoints[3] * t);

		}

		/**
		 * Normalizes this <code>Vector4f</code>.
		 */
		public void normalize()
		{
			if (mPoints[3] == 0)
				return;

			mPoints[0] /= mPoints[3];
			mPoints[1] /= mPoints[3];
			mPoints[2] /= mPoints[3];

			double a = Math.Sqrt(this.mPoints[0] * this.mPoints[0] + this.mPoints[1] * this.mPoints[1] + this.mPoints[2]
					* this.mPoints[2]);
			mPoints[0] = (float)(this.mPoints[0] / a);
			mPoints[1] = (float)(this.mPoints[1] / a);
			mPoints[2] = (float)(this.mPoints[2] / a);
		}

		/**
		 * Returns the X-dimension of this <code>Vector4f</code>.
		 *
		 * @return the X-dimension.
		 */
		public float getX()
		{
			return this.mPoints[0];
		}

		/**
		 * Returns the Y-dimension of this <code>Vector4f</code>.
		 *
		 * @return the Y-dimension.
		 */
		public float getY()
		{
			return this.mPoints[1];
		}

		/**
		 * Returns the Z-dimension of this <code>Vector4f</code>.
		 *
		 * @return the Z-dimension.
		 */
		public float getZ()
		{
			return this.mPoints[2];
		}

		/**
		 * Returns the W-dimension of this <code>Vector4f</code>.
		 *
		 * @return the W-dimension.
		 */
		public float getW()
		{
			return this.mPoints[3];
		}

		/**
		 * Sets the X-dimension of this <code>Vector4f</code>.
		 *
		 * @param x the X-dimension.
		 */
		public void setX(float x)
		{
			this.mPoints[0] = x;
		}

		/**
		 * Sets the Y-dimension of this <code>Vector4f</code>.
		 *
		 * @param y the Y-dimension.
		 */
		public void setY(float y)
		{
			this.mPoints[1] = y;
		}

		/**
		 * Sets the Z-dimension of this <code>Vector4f</code>.
		 *
		 * @param z the Z-dimension.
		 */
		public void setZ(float z)
		{
			this.mPoints[2] = z;
		}

		/**
		 * Sets the W-dimension of this <code>Vector4f</code>.
		 *
		 * @param w the W-dimension.
		 */
		public void setW(float w)
		{
			this.mPoints[3] = w;
		}

		/**
		 * Sets all four dimensions of this <code>Vector4f</code>.
		 */
		private void setXYZW(float x, float y, float z, float w)
		{
			this.mPoints[0] = x;
			this.mPoints[1] = y;
			this.mPoints[2] = z;
			this.mPoints[3] = w;
		}

		public bool compareTo(Vector4f rhs)
		{
			bool ret = false;
			if (this.mPoints[0] == rhs.mPoints[0] && this.mPoints[1] == rhs.mPoints[1] && this.mPoints[2] == rhs.mPoints[2]
					&& this.mPoints[3] == rhs.mPoints[3])
				ret = true;
			return ret;
		}


		/**
		 * Copies a {@link Vector3f} to this <code>Vector4f</code> using
		 * a separate value for W-dimension.
		 *
		 * @param input the {@link Vector3f}.
		 * @param w <code>float</code> representing the W-dimension.
		 */
		public void copyFromV3f(Vector3f input, float w)
		{
			mPoints[0] = (input.getX());
			mPoints[1] = (input.getY());
			mPoints[2] = (input.getZ());
			mPoints[3] = (w);
		}

		/**
		 * Returns the <code>String</code> representation of this <code>
		 * Vector4f</code>.
		 *
		 * It overrides the {@link java.lang.Object#toString()} method.
		 *
		 * @return <code>String</code> representing this
		 *         <code>Vector4f</code>.
		 */
	
		public String toString()
		{
			return "X:" + mPoints[0] + " Y:" + mPoints[1] + " Z:" + mPoints[2] + " W:" + mPoints[3];
		}
	}
}
