﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Cursorr.Core.Geometry
{

	/**
	 * Matrix math utilities.
	 *
	 * <p>
	 *     These methods operate on OpenGL ES format matrices and vectors
	 *     stored in float arrays. Matrices are 4 x 4 column-vector(float)
	 *     matrices stored in column-major order:
	 *
	 *     <pre>
	 *         m[offset +  0]    m[offset +  4]    m[offset +  8]    m[offset + 12]
	 *         m[offset +  1]    m[offset +  5]    m[offset +  9]    m[offset + 13]
	 *         m[offset +  2]    m[offset +  6]    m[offset + 10]    m[offset + 14]
	 *         m[offset +  3]    m[offset +  7]    m[offset + 11]    m[offset + 15]
	 *     </pre>
	 * </p>
	 * <p>
	 *     Vectors are 4 row x 1 column column-vectors stored in order:
	 *
	 *     <pre>
	 *         v[offset + 0]
	 *         v[offset + 1]
	 *         v[offset + 2]
	 *         v[offset + 3]
	 *     </pre>
	 * </p>
	 *
	 * @see project.pc.sensor.representation.MatrixF4x4
	 */
	public class Matrix
	{

		private static  float[] TEMP_MATRIX_ARRAY = new float[32];

		private static void multiplyMM(float[] output, int outputOffset, float[] lhs, int lhsOffset, float[] rhs, int rhsOffset)
		{
			output[outputOffset] = lhs[lhsOffset] * rhs[rhsOffset] + lhs[lhsOffset + 4] * rhs[rhsOffset + 1] + lhs[lhsOffset + 8] * rhs[rhsOffset + 2] + lhs[lhsOffset + 12] * rhs[rhsOffset + 3];
			output[outputOffset + 1] = lhs[lhsOffset + 1] * rhs[rhsOffset] + lhs[lhsOffset + 5] * rhs[rhsOffset + 1] + lhs[lhsOffset + 9] * rhs[rhsOffset + 2] + lhs[lhsOffset + 13] * rhs[rhsOffset + 3];
			output[outputOffset + 2] = lhs[lhsOffset + 2] * rhs[rhsOffset] + lhs[lhsOffset + 6] * rhs[rhsOffset + 1] + lhs[lhsOffset + 10] * rhs[rhsOffset + 2] + lhs[lhsOffset + 14] * rhs[rhsOffset + 3];
			output[outputOffset + 3] = lhs[lhsOffset + 3] * rhs[rhsOffset] + lhs[lhsOffset + 7] * rhs[rhsOffset + 1] + lhs[lhsOffset + 11] * rhs[rhsOffset + 2] + lhs[lhsOffset + 15] * rhs[rhsOffset + 3];

			output[outputOffset + 4] = lhs[lhsOffset] * rhs[rhsOffset + 4] + lhs[lhsOffset + 4] * rhs[rhsOffset + 5] + lhs[lhsOffset + 8] * rhs[rhsOffset + 6] + lhs[lhsOffset + 12] * rhs[rhsOffset + 7];
			output[outputOffset + 5] = lhs[lhsOffset + 1] * rhs[rhsOffset + 4] + lhs[lhsOffset + 5] * rhs[rhsOffset + 5] + lhs[lhsOffset + 9] * rhs[rhsOffset + 6] + lhs[lhsOffset + 13] * rhs[rhsOffset + 7];
			output[outputOffset + 6] = lhs[lhsOffset + 2] * rhs[rhsOffset + 4] + lhs[lhsOffset + 6] * rhs[rhsOffset + 5] + lhs[lhsOffset + 10] * rhs[rhsOffset + 6] + lhs[lhsOffset + 14] * rhs[rhsOffset + 7];
			output[outputOffset + 7] = lhs[lhsOffset + 3] * rhs[rhsOffset + 4] + lhs[lhsOffset + 7] * rhs[rhsOffset + 5] + lhs[lhsOffset + 11] * rhs[rhsOffset + 6] + lhs[lhsOffset + 15] * rhs[rhsOffset + 7];

			output[outputOffset + 8] = lhs[lhsOffset] * rhs[rhsOffset + 8] + lhs[lhsOffset + 4] * rhs[rhsOffset + 9] + lhs[lhsOffset + 8] * rhs[rhsOffset + 10] + lhs[lhsOffset + 12] * rhs[rhsOffset + 11];
			output[outputOffset + 9] = lhs[lhsOffset + 1] * rhs[rhsOffset + 8] + lhs[lhsOffset + 5] * rhs[rhsOffset + 9] + lhs[lhsOffset + 9] * rhs[rhsOffset + 10] + lhs[lhsOffset + 13] * rhs[rhsOffset + 11];
			output[outputOffset + 10] = lhs[lhsOffset + 2] * rhs[rhsOffset + 8] + lhs[lhsOffset + 6] * rhs[rhsOffset + 9] + lhs[lhsOffset + 10] * rhs[rhsOffset + 10] + lhs[lhsOffset + 14] * rhs[rhsOffset + 11];
			output[outputOffset + 11] = lhs[lhsOffset + 3] * rhs[rhsOffset + 8] + lhs[lhsOffset + 7] * rhs[rhsOffset + 9] + lhs[lhsOffset + 11] * rhs[rhsOffset + 10] + lhs[lhsOffset + 15] * rhs[rhsOffset + 11];

			output[outputOffset + 12] = lhs[lhsOffset] * rhs[rhsOffset + 12] + lhs[lhsOffset + 4] * rhs[rhsOffset + 13] + lhs[lhsOffset + 8] * rhs[rhsOffset + 14] + lhs[lhsOffset + 12] * rhs[rhsOffset + 15];
			output[outputOffset + 13] = lhs[lhsOffset + 1] * rhs[rhsOffset + 12] + lhs[lhsOffset + 5] * rhs[rhsOffset + 13] + lhs[lhsOffset + 9] * rhs[rhsOffset + 14] + lhs[lhsOffset + 13] * rhs[rhsOffset + 15];
			output[outputOffset + 14] = lhs[lhsOffset + 2] * rhs[rhsOffset + 12] + lhs[lhsOffset + 6] * rhs[rhsOffset + 13] + lhs[lhsOffset + 10] * rhs[rhsOffset + 14] + lhs[lhsOffset + 14] * rhs[rhsOffset + 15];
			output[outputOffset + 15] = lhs[lhsOffset + 3] * rhs[rhsOffset + 12] + lhs[lhsOffset + 7] * rhs[rhsOffset + 13] + lhs[lhsOffset + 11] * rhs[rhsOffset + 14] + lhs[lhsOffset + 15] * rhs[rhsOffset + 15];
		}

		/**
		 * Multiplies two 4x4 matrices.
		 *
		 * @param output the float array that holds the result.
		 * @param lhs the float array that holds the left-hand-side matrix.
		 * @param rhs the float array that holds the right-hand-side matrix.
		 *
		 * @throws IllegalArgumentException
		 */
		public static void multiplyMM(float[] output, float[] lhs, float[] rhs)
		{
			output[0] = lhs[0] * rhs[0] + lhs[4] * rhs[1] + lhs[8] * rhs[2] + lhs[12] * rhs[3];
			output[1] = lhs[1] * rhs[0] + lhs[5] * rhs[1] + lhs[9] * rhs[2] + lhs[13] * rhs[3];
			output[2] = lhs[2] * rhs[0] + lhs[6] * rhs[1] + lhs[10] * rhs[2] + lhs[14] * rhs[3];
			output[3] = lhs[3] * rhs[0] + lhs[7] * rhs[1] + lhs[11] * rhs[2] + lhs[15] * rhs[3];

			output[4] = lhs[0] * rhs[4] + lhs[4] * rhs[5] + lhs[8] * rhs[6] + lhs[12] * rhs[7];
			output[5] = lhs[1] * rhs[4] + lhs[5] * rhs[5] + lhs[9] * rhs[6] + lhs[13] * rhs[7];
			output[6] = lhs[2] * rhs[4] + lhs[6] * rhs[5] + lhs[10] * rhs[6] + lhs[14] * rhs[7];
			output[7] = lhs[3] * rhs[4] + lhs[7] * rhs[5] + lhs[11] * rhs[6] + lhs[15] * rhs[7];

			output[8] = lhs[0] * rhs[8] + lhs[4] * rhs[9] + lhs[8] * rhs[10] + lhs[12] * rhs[11];
			output[9] = lhs[1] * rhs[8] + lhs[5] * rhs[9] + lhs[9] * rhs[10] + lhs[13] * rhs[11];
			output[10] = lhs[2] * rhs[8] + lhs[6] * rhs[9] + lhs[10] * rhs[10] + lhs[14] * rhs[11];
			output[11] = lhs[3] * rhs[8] + lhs[7] * rhs[9] + lhs[11] * rhs[10] + lhs[15] * rhs[11];

			output[12] = lhs[0] * rhs[12] + lhs[4] * rhs[13] + lhs[8] * rhs[14] + lhs[12] * rhs[15];
			output[13] = lhs[1] * rhs[12] + lhs[5] * rhs[13] + lhs[9] * rhs[14] + lhs[13] * rhs[15];
			output[14] = lhs[2] * rhs[12] + lhs[6] * rhs[13] + lhs[10] * rhs[14] + lhs[14] * rhs[15];
			output[15] = lhs[3] * rhs[12] + lhs[7] * rhs[13] + lhs[11] * rhs[14] + lhs[15] * rhs[15];
		}

		/**
		 * Multiplies a 4 element vector by a 4x4 matrix.
		 *
		 * <p>
		 *     It stores the result in a 4 element column vector. In matrix notation:
		 *     <br/> &nbsp;&nbsp;&nbsp;&nbsp;
		 *     <i>output = lhs x rhs</i>
		 * </p>
		 *
		 * @param output the float array that holds the result vector.
		 * @param outputOffset the offset into the result array where the result vector is stored.
		 * @param lhs the float array that holds the left-hand-side matrix.
		 * @param lhsOffset the offset into the <code>lhs</code> array.
		 * @param rhs the float array that holds the right-hand-side vector.
		 * @param rhsOffset the offset into the <code>rhs</code> array.
		 * @throws IllegalArgumentException
		 */
		public static void multiplyMV(float[] output, int outputOffset, float[] lhs, int lhsOffset, float[] rhs, int rhsOffset)
		{
			output[outputOffset] = lhs[lhsOffset] * rhs[rhsOffset] + lhs[lhsOffset + 4] * rhs[rhsOffset + 1] + lhs[lhsOffset + 8] * rhs[rhsOffset + 2] + lhs[lhsOffset + 12] * rhs[rhsOffset + 3];
			output[outputOffset + 1] = lhs[lhsOffset + 1] * rhs[rhsOffset] + lhs[lhsOffset + 5] * rhs[rhsOffset + 1] + lhs[lhsOffset + 9] * rhs[rhsOffset + 2] + lhs[lhsOffset + 13] * rhs[rhsOffset + 3];
			output[outputOffset + 2] = lhs[lhsOffset + 2] * rhs[rhsOffset] + lhs[lhsOffset + 6] * rhs[rhsOffset + 1] + lhs[lhsOffset + 10] * rhs[rhsOffset + 2] + lhs[lhsOffset + 14] * rhs[rhsOffset + 3];
			output[outputOffset + 3] = lhs[lhsOffset + 3] * rhs[rhsOffset] + lhs[lhsOffset + 7] * rhs[rhsOffset + 1] + lhs[lhsOffset + 11] * rhs[rhsOffset + 2] + lhs[lhsOffset + 15] * rhs[rhsOffset + 3];

		}

		public static void multiplyMV(float[] outputV, float[] inputM, float[] inputV)
		{
			outputV[0] = inputM[0] * inputV[0] + inputM[4] * inputV[1] + inputM[8] * inputV[2] + inputM[12] * inputV[3];
			outputV[1] = inputM[1] * inputV[0] + inputM[5] * inputV[1] + inputM[9] * inputV[2] + inputM[13] * inputV[3];
			outputV[2] = inputM[2] * inputV[0] + inputM[6] * inputV[1] + inputM[10] * inputV[2] + inputM[14] * inputV[3];
			outputV[3] = inputM[3] * inputV[0] + inputM[7] * inputV[1] + inputM[11] * inputV[2] + inputM[15] * inputV[3];
		}

		public static void multiplyMV3(float[] outputV, float[] inputM, float[] inputV, float w)
		{
			outputV[0] = inputM[0] * inputV[0] + inputM[4] * inputV[1] + inputM[8] * inputV[2] + inputM[12] * w;
			outputV[1] = inputM[1] * inputV[0] + inputM[5] * inputV[1] + inputM[9] * inputV[2] + inputM[13] * w;
			outputV[2] = inputM[2] * inputV[0] + inputM[6] * inputV[1] + inputM[10] * inputV[2] + inputM[14] * w;
		}

		/**
		 * Transposes a 4 x 4 matrix.
		 *
		 * @param mTrans the array that holds the output inverted matrix.
		 * @param mTransOffset an offset where the inverted matrix is stored.
		 * @param m the input array.
		 * @param mOffset an offset into <code>m</code> where the matrix is stored.
		 */
		public static void transposeM(float[] mTrans, int mTransOffset, float[] m, int mOffset)
		{
			for (int i = 0; i < 4; i++)
			{
				int mBase = i * 4 + mOffset;
				mTrans[i + mTransOffset] = m[mBase];
				mTrans[i + 4 + mTransOffset] = m[mBase + 1];
				mTrans[i + 8 + mTransOffset] = m[mBase + 2];
				mTrans[i + 12 + mTransOffset] = m[mBase + 3];
			}
		}

		/**
		 * Inverts a 4 x 4 matrix.
		 *
		 * @param mInv the array that holds the output inverted matrix.
		 * @param mInvOffset an offset into <code>mInv</code> where the inverted
		 *                   matrix is stored.
		 * @param m the input array.
		 * @param mOffset an offset into <code>m</code> where the matrix is stored.
		 * @return <code>true</code>, if the matrix could be inverted, <br/>
		 *         <code>false</code>, otherwise.
		 */
		public static bool invertM(float[] mInv, int mInvOffset, float[] m, int mOffset)
		{
			// Invert a 4 x 4 matrix using Cramer's Rule

			// transpose matrix
			float src0 = m[mOffset];
			float src4 = m[mOffset + 1];
			float src8 = m[mOffset + 2];
			float src12 = m[mOffset + 3];

			float src1 = m[mOffset + 4];
			float src5 = m[mOffset + 5];
			float src9 = m[mOffset + 6];
			float src13 = m[mOffset + 7];

			float src2 = m[mOffset + 8];
			float src6 = m[mOffset + 9];
			float src10 = m[mOffset + 10];
			float src14 = m[mOffset + 11];

			float src3 = m[mOffset + 12];
			float src7 = m[mOffset + 13];
			float src11 = m[mOffset + 14];
			float src15 = m[mOffset + 15];

			// calculate pairs for first 8 elements (cofactors)
			float atmp0 = src10 * src15;
			float atmp1 = src11 * src14;
			float atmp2 = src9 * src15;
			float atmp3 = src11 * src13;
			float atmp4 = src9 * src14;
			float atmp5 = src10 * src13;
			float atmp6 = src8 * src15;
			float atmp7 = src11 * src12;
			float atmp8 = src8 * src14;
			float atmp9 = src10 * src12;
			float atmp10 = src8 * src13;
			float atmp11 = src9 * src12;

			// calculate first 8 elements (cofactors)
			float dst0 = (atmp0 * src5 + atmp3 * src6 + atmp4 * src7) - (atmp1 * src5 + atmp2 * src6 + atmp5 * src7);
			float dst1 = (atmp1 * src4 + atmp6 * src6 + atmp9 * src7) - (atmp0 * src4 + atmp7 * src6 + atmp8 * src7);
			float dst2 = (atmp2 * src4 + atmp7 * src5 + atmp10 * src7)
					- (atmp3 * src4 + atmp6 * src5 + atmp11 * src7);
			float dst3 = (atmp5 * src4 + atmp8 * src5 + atmp11 * src6)
					- (atmp4 * src4 + atmp9 * src5 + atmp10 * src6);
			float dst4 = (atmp1 * src1 + atmp2 * src2 + atmp5 * src3) - (atmp0 * src1 + atmp3 * src2 + atmp4 * src3);
			float dst5 = (atmp0 * src0 + atmp7 * src2 + atmp8 * src3) - (atmp1 * src0 + atmp6 * src2 + atmp9 * src3);
			float dst6 = (atmp3 * src0 + atmp6 * src1 + atmp11 * src3)
					- (atmp2 * src0 + atmp7 * src1 + atmp10 * src3);
			float dst7 = (atmp4 * src0 + atmp9 * src1 + atmp10 * src2)
					- (atmp5 * src0 + atmp8 * src1 + atmp11 * src2);

			// calculate pairs for second 8 elements (cofactors)
			float btmp0 = src2 * src7;
			float btmp1 = src3 * src6;
			float btmp2 = src1 * src7;
			float btmp3 = src3 * src5;
			float btmp4 = src1 * src6;
			float btmp5 = src2 * src5;
			float btmp6 = src0 * src7;
			float btmp7 = src3 * src4;
			float btmp8 = src0 * src6;
			float btmp9 = src2 * src4;
			float btmp10 = src0 * src5;
			float btmp11 = src1 * src4;

			// calculate second 8 elements (cofactors)
			float dst8 = (btmp0 * src13 + btmp3 * src14 + btmp4 * src15)
					- (btmp1 * src13 + btmp2 * src14 + btmp5 * src15);
			float dst9 = (btmp1 * src12 + btmp6 * src14 + btmp9 * src15)
					- (btmp0 * src12 + btmp7 * src14 + btmp8 * src15);
			float dst10 = (btmp2 * src12 + btmp7 * src13 + btmp10 * src15)
					- (btmp3 * src12 + btmp6 * src13 + btmp11 * src15);
			float dst11 = (btmp5 * src12 + btmp8 * src13 + btmp11 * src14)
					- (btmp4 * src12 + btmp9 * src13 + btmp10 * src14);
			float dst12 = (btmp2 * src10 + btmp5 * src11 + btmp1 * src9)
					- (btmp4 * src11 + btmp0 * src9 + btmp3 * src10);
			float dst13 = (btmp8 * src11 + btmp0 * src8 + btmp7 * src10)
					- (btmp6 * src10 + btmp9 * src11 + btmp1 * src8);
			float dst14 = (btmp6 * src9 + btmp11 * src11 + btmp3 * src8)
					- (btmp10 * src11 + btmp2 * src8 + btmp7 * src9);
			float dst15 = (btmp10 * src10 + btmp4 * src8 + btmp9 * src9)
					- (btmp8 * src9 + btmp11 * src10 + btmp5 * src8);

			// calculate determinant
			float det = src0 * dst0 + src1 * dst1 + src2 * dst2 + src3 * dst3;

			if (det == 0.0f)
			{
				return false;
			}

			// calculate matrix inverse
			float invdet = 1.0f / det;
			mInv[mInvOffset] = dst0 * invdet;
			mInv[1 + mInvOffset] = dst1 * invdet;
			mInv[2 + mInvOffset] = dst2 * invdet;
			mInv[3 + mInvOffset] = dst3 * invdet;

			mInv[4 + mInvOffset] = dst4 * invdet;
			mInv[5 + mInvOffset] = dst5 * invdet;
			mInv[6 + mInvOffset] = dst6 * invdet;
			mInv[7 + mInvOffset] = dst7 * invdet;

			mInv[8 + mInvOffset] = dst8 * invdet;
			mInv[9 + mInvOffset] = dst9 * invdet;
			mInv[10 + mInvOffset] = dst10 * invdet;
			mInv[11 + mInvOffset] = dst11 * invdet;

			mInv[12 + mInvOffset] = dst12 * invdet;
			mInv[13 + mInvOffset] = dst13 * invdet;
			mInv[14 + mInvOffset] = dst14 * invdet;
			mInv[15 + mInvOffset] = dst15 * invdet;

			return true;
		}

		/**
		 * Computes an orthographic projection matrix.
		 *
		 * @param m returns the result.
		 * @param mOffset offset value.
		 * @param left left clipping value.
		 * @param right right clipping value.
		 * @param bottom bottom clipping value.
		 * @param top top clipping value.
		 * @param near nearer depth clipping value.
		 * @param far farther depth clipping value.
		 */
		public static void orthoM(float[] m, int mOffset, float left, float right, float bottom, float top, float near, float far)
		{
			if (left == right)
			{
				throw new Exception("left == right");
			}
			if (bottom == top)
			{
				throw new Exception("bottom == top");
			}
			if (near == far)
			{
				throw new Exception("near == far");
			}

			float r_width = 1.0f / (right - left);
			float r_height = 1.0f / (top - bottom);
			float r_depth = 1.0f / (far - near);
			float x = 2.0f * (r_width);
			float y = 2.0f * (r_height);
			float z = -2.0f * (r_depth);
			float tx = -(right + left) * r_width;
			float ty = -(top + bottom) * r_height;
			float tz = -(far + near) * r_depth;
			m[mOffset] = x;
			m[mOffset + 5] = y;
			m[mOffset + 10] = z;
			m[mOffset + 12] = tx;
			m[mOffset + 13] = ty;
			m[mOffset + 14] = tz;
			m[mOffset + 15] = 1.0f;
			m[mOffset + 1] = 0.0f;
			m[mOffset + 2] = 0.0f;
			m[mOffset + 3] = 0.0f;
			m[mOffset + 4] = 0.0f;
			m[mOffset + 6] = 0.0f;
			m[mOffset + 7] = 0.0f;
			m[mOffset + 8] = 0.0f;
			m[mOffset + 9] = 0.0f;
			m[mOffset + 11] = 0.0f;
		}

		/**
		 * Defines a projection matrix in terms of six clip planes.
		 *
		 * @param m the float array that holds the perspective matrix.
		 * @param offset the offset into <code>m</code> where the perspective
		 *            matrix data is written.
		 * @param left left clipping value.
		 * @param right right clipping value.
		 * @param bottom bottom clipping value.
		 * @param top top clipping value.
		 * @param near nearer depth clipping value.
		 * @param far farther depth clipping value.
		 */
		public static void frustumM(float[] m, int offset, float left, float right, float bottom, float top, float near,
									float far)
		{
			if (left == right)
			{
				throw new Exception("left == right");
			}
			if (top == bottom)
			{
				throw new Exception("top == bottom");
			}
			if (near == far)
			{
				throw new Exception("near == far");
			}
			if (near <= 0.0f)
			{
				throw new Exception("near <= 0.0f");
			}
			if (far <= 0.0f)
			{
				throw new Exception("far <= 0.0f");
			}
			float r_width = 1.0f / (right - left);
			float r_height = 1.0f / (top - bottom);
			float r_depth = 1.0f / (near - far);
			float x = 2.0f * (near * r_width);
			float y = 2.0f * (near * r_height);
			float A = 2.0f * ((right + left) * r_width);
			float B = (top + bottom) * r_height;
			float C = (far + near) * r_depth;
			float D = 2.0f * (far * near * r_depth);
			m[offset] = x;
			m[offset + 5] = y;
			m[offset + 8] = A;
			m[offset + 9] = B;
			m[offset + 10] = C;
			m[offset + 14] = D;
			m[offset + 11] = -1.0f;
			m[offset + 1] = 0.0f;
			m[offset + 2] = 0.0f;
			m[offset + 3] = 0.0f;
			m[offset + 4] = 0.0f;
			m[offset + 6] = 0.0f;
			m[offset + 7] = 0.0f;
			m[offset + 12] = 0.0f;
			m[offset + 13] = 0.0f;
			m[offset + 15] = 0.0f;
		}

		/**
		 * Defines a projection matrix in terms of a field of view angle, an
		 * aspect ratio, and Z clip planes.
		 *
		 * @param m the float array that holds the perspective matrix.
		 * @param offset the offset into <code>m</code> where the perspective matrix
		 *               data is written.
		 * @param fovy field of view in y direction, in degrees.
		 * @param aspect width to height aspect ratio of the viewport.
		 * @param zNear nearer depth clipping value on Z-plane.
		 * @param zFar farther  depth clipping value on Z-plane.
		 *
		 */
		public static void perspectiveM(float[] m, int offset, float fovy, float aspect, float zNear, float zFar)
		{
			float f = 1.0f / (float)Math.Tan(fovy * (Math.PI / 360.0));
			float rangeReciprocal = 1.0f / (zNear - zFar);

			m[offset] = f / aspect;
			m[offset + 1] = 0.0f;
			m[offset + 2] = 0.0f;
			m[offset + 3] = 0.0f;

			m[offset + 4] = 0.0f;
			m[offset + 5] = f;
			m[offset + 6] = 0.0f;
			m[offset + 7] = 0.0f;

			m[offset + 8] = 0.0f;
			m[offset + 9] = 0.0f;
			m[offset + 10] = (zFar + zNear) * rangeReciprocal;
			m[offset + 11] = -1.0f;

			m[offset + 12] = 0.0f;
			m[offset + 13] = 0.0f;
			m[offset + 14] = 2.0f * zFar * zNear * rangeReciprocal;
			m[offset + 15] = 0.0f;
		}

		private static float length(float x, float y, float z)
		{
			return (float)Math.Sqrt(x * x + y * y + z * z);
		}

		/**
		 * Sets matrix to the identity matrix.
		 *
		 * @param sm the result.
		 * @param smOffset index into <code>sm</code> where the result matrix starts.
		 */
		public static void setIdentityM(float[] sm, int smOffset)
		{
			for (int i = 0; i < 16; i++)
			{
				sm[smOffset + i] = 0;
			}
			for (int i = 0; i < 16; i += 5)
			{
				sm[smOffset + i] = 1.0f;
			}
		}

		/**
		 * Scales matrix <code>m</code> by <i>x</i>, <i>y</i>, and <i>z</i>.
		 *
		 * @param sm the result.
		 * @param smOffset index into <code>sm</code> where the result matrix starts.
		 * @param m source matrix.
		 * @param mOffset index into <code>m</code> where the source matrix starts.
		 * @param x scale factor x.
		 * @param y scale factor y.
		 * @param z scale factor z.
		 */
		public static void scaleM(float[] sm, int smOffset, float[] m, int mOffset, float x, float y, float z)
		{
			for (int i = 0; i < 4; i++)
			{
				int smi = smOffset + i;
				int mi = mOffset + i;
				sm[smi] = m[mi] * x;
				sm[4 + smi] = m[4 + mi] * y;
				sm[8 + smi] = m[8 + mi] * z;
				sm[12 + smi] = m[12 + mi];
			}
		}

		public static void scaleM(float[] m, int mOffset, float x, float y, float z)
		{
			for (int i = 0; i < 4; i++)
			{
				int mi = mOffset + i;
				m[mi] *= x;
				m[4 + mi] *= y;
				m[8 + mi] *= z;
			}
		}

		/**
		 * Translates matrix <code>m</code> by <i>x</i>, <i>y</i>, and <i>z</i>.
		 *
		 * @param tm the result.
		 * @param tmOffset index into <code>sm</code> where the result matrix starts.
		 * @param m source matrix.
		 * @param mOffset index into <code>m</code> where the source matrix starts.
		 * @param x translation factor x.
		 * @param y translation factor y.
		 * @param z translation factor z.
		 */
		public static void translateM(float[] tm, int tmOffset, float[] m, int mOffset, float x, float y, float z)
		{
			Array.Copy(m, mOffset, tm, tmOffset, 12);
			for (int i = 0; i < 4; i++)
			{
				int tmi = tmOffset + i;
				int mi = mOffset + i;
				tm[12 + tmi] = m[mi] * x + m[4 + mi] * y + m[8 + mi] * z + m[12 + mi];
			}
		}

		private static void translateM(float[] m, int mOffset, float x, float y, float z)
		{
			for (int i = 0; i < 4; i++)
			{
				int mi = mOffset + i;
				m[12 + mi] += m[mi] * x + m[4 + mi] * y + m[8 + mi] * z;
			}
		}

		/**
		 * Rotates matrix m by angle a (in degrees) around the axis (x, y, z).
		 *
		 * @param rm returns the result.
		 * @param rmOffset index into rm where the result matrix starts.
		 * @param m source matrix.
		 * @param mOffset index into m where the source matrix starts.
		 * @param a angle to rotate in degrees.
		 * @param x scale factor x.
		 * @param y scale factor y.
		 * @param z scale factor z.
		 */
		public static void rotateM(float[] rm, int rmOffset, float[] m, int mOffset, float a, float x, float y, float z)
		{
			lock (TEMP_MATRIX_ARRAY) {
				setRotateM(TEMP_MATRIX_ARRAY, 0, a, x, y, z);
				multiplyMM(rm, rmOffset, m, mOffset, TEMP_MATRIX_ARRAY, 0);
			}
		}

		/**
		 * Rotates matrix m in place by angle a (in degrees)
		 * around the axis (x, y, z).
		 *
		 * @param m source matrix.
		 * @param mOffset index into m where the matrix starts.
		 * @param a angle to rotate in degrees.
		 * @param x scale factor x.
		 * @param y scale factor y.
		 * @param z scale factor z.
		 */
		public static void rotateM(float[] m, int mOffset, float a, float x, float y, float z)
		{
			lock (TEMP_MATRIX_ARRAY) {
				setRotateM(TEMP_MATRIX_ARRAY, 0, a, x, y, z);
				multiplyMM(TEMP_MATRIX_ARRAY, 16, m, mOffset, TEMP_MATRIX_ARRAY, 0);
				Array.Copy(TEMP_MATRIX_ARRAY, 16, m, mOffset, 16);
			}
		}

		private static void setRotateM(float[] rm, int rmOffset, float a, float x, float y, float z)
		{
			rm[rmOffset + 3] = 0;
			rm[rmOffset + 7] = 0;
			rm[rmOffset + 11] = 0;
			rm[rmOffset + 12] = 0;
			rm[rmOffset + 13] = 0;
			rm[rmOffset + 14] = 0;
			rm[rmOffset + 15] = 1;
			a *= (float)(Math.PI / 180.0f);
			float s = (float)Math.Sin(a);
			float c = (float)Math.Cos(a);
			if (1.0f == x && 0.0f == y && 0.0f == z)
			{
				rm[rmOffset + 5] = c;
				rm[rmOffset + 10] = c;
				rm[rmOffset + 6] = s;
				rm[rmOffset + 9] = -s;
				rm[rmOffset + 1] = 0;
				rm[rmOffset + 2] = 0;
				rm[rmOffset + 4] = 0;
				rm[rmOffset + 8] = 0;
				rm[rmOffset] = 1;
			}
			else if (0.0f == x && 1.0f == y && 0.0f == z)
			{
				rm[rmOffset] = c;
				rm[rmOffset + 10] = c;
				rm[rmOffset + 8] = s;
				rm[rmOffset + 2] = -s;
				rm[rmOffset + 1] = 0;
				rm[rmOffset + 4] = 0;
				rm[rmOffset + 6] = 0;
				rm[rmOffset + 9] = 0;
				rm[rmOffset + 5] = 1;
			}
			else if (0.0f == x && 0.0f == y && 1.0f == z)
			{
				rm[rmOffset] = c;
				rm[rmOffset + 5] = c;
				rm[rmOffset + 1] = s;
				rm[rmOffset + 4] = -s;
				rm[rmOffset + 2] = 0;
				rm[rmOffset + 6] = 0;
				rm[rmOffset + 8] = 0;
				rm[rmOffset + 9] = 0;
				rm[rmOffset + 10] = 1;
			}
			else
			{
				float len = length(x, y, z);
				if (1.0f != len)
				{
					float recipLen = 1.0f / len;
					x *= recipLen;
					y *= recipLen;
					z *= recipLen;
				}
				float nc = 1.0f - c;
				float xy = x * y;
				float yz = y * z;
				float zx = z * x;
				float xs = x * s;
				float ys = y * s;
				float zs = z * s;
				rm[rmOffset] = x * x * nc + c;
				rm[rmOffset + 4] = xy * nc - zs;
				rm[rmOffset + 8] = zx * nc + ys;
				rm[rmOffset + 1] = xy * nc + zs;
				rm[rmOffset + 5] = y * y * nc + c;
				rm[rmOffset + 9] = yz * nc - xs;
				rm[rmOffset + 2] = zx * nc - ys;
				rm[rmOffset + 6] = yz * nc + xs;
				rm[rmOffset + 10] = z * z * nc + c;
			}
		}

		/**
		 * Converts Euler angles to a rotation matrix.
		 *
		 * @param rm the result.
		 * @param rmOffset index into <code>rm</code> where the result matrix starts.
		 * @param x angle of rotation, in degrees.
		 * @param y angle of rotation, in degrees.
		 * @param z angle of rotation, in degrees.
		 */
		public static void setRotateEulerM(float[] rm, int rmOffset, float x, float y, float z)
		{
			x *= (float)(Math.PI / 180.0f);
			y *= (float)(Math.PI / 180.0f);
			z *= (float)(Math.PI / 180.0f);
			float cx = (float)Math.Cos(x);
			float sx = (float)Math.Sin(x);
			float cy = (float)Math.Cos(y);
			float sy = (float)Math.Sin(y);
			float cz = (float)Math.Cos(z);
			float sz = (float)Math.Sin(z);
			float cxsy = cx * sy;
			float sxsy = sx * sy;

			rm[rmOffset] = cy * cz;
			rm[rmOffset + 1] = -cy * sz;
			rm[rmOffset + 2] = sy;
			rm[rmOffset + 3] = 0.0f;

			rm[rmOffset + 4] = cxsy * cz + cx * sz;
			rm[rmOffset + 5] = -cxsy * sz + cx * cz;
			rm[rmOffset + 6] = -sx * cy;
			rm[rmOffset + 7] = 0.0f;

			rm[rmOffset + 8] = -sxsy * cz + sx * sz;
			rm[rmOffset + 9] = sxsy * sz + sx * cz;
			rm[rmOffset + 10] = cx * cy;
			rm[rmOffset + 11] = 0.0f;

			rm[rmOffset + 12] = 0.0f;
			rm[rmOffset + 13] = 0.0f;
			rm[rmOffset + 14] = 0.0f;
			rm[rmOffset + 15] = 1.0f;
		}

		public static void setLookAtM(float[] rm, int rmOffset, float eyeX, float eyeY, float eyeZ, float centerX,
									  float centerY, float centerZ, float upX, float upY, float upZ)
		{

			float fx = centerX - eyeX;
			float fy = centerY - eyeY;
			float fz = centerZ - eyeZ;

			// Normalize f
			float rlf = 1.0f / Matrix.length(fx, fy, fz);
			fx *= rlf;
			fy *= rlf;
			fz *= rlf;

			// compute s = f x up (x means "cross product")
			float sx = fy * upZ - fz * upY;
			float sy = fz * upX - fx * upZ;
			float sz = fx * upY - fy * upX;

			// and normalize s
			float rls = 1.0f / Matrix.length(sx, sy, sz);
			sx *= rls;
			sy *= rls;
			sz *= rls;

			// compute u = s x f
			float ux = sy * fz - sz * fy;
			float uy = sz * fx - sx * fz;
			float uz = sx * fy - sy * fx;

			rm[rmOffset] = sx;
			rm[rmOffset + 1] = ux;
			rm[rmOffset + 2] = -fx;
			rm[rmOffset + 3] = 0.0f;

			rm[rmOffset + 4] = sy;
			rm[rmOffset + 5] = uy;
			rm[rmOffset + 6] = -fy;
			rm[rmOffset + 7] = 0.0f;

			rm[rmOffset + 8] = sz;
			rm[rmOffset + 9] = uz;
			rm[rmOffset + 10] = -fz;
			rm[rmOffset + 11] = 0.0f;

			rm[rmOffset + 12] = 0.0f;
			rm[rmOffset + 13] = 0.0f;
			rm[rmOffset + 14] = 0.0f;
			rm[rmOffset + 15] = 1.0f;

			translateM(rm, rmOffset, -eyeX, -eyeY, -eyeZ);
		}
	}
}
