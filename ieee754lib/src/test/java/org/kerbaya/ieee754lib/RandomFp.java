/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2016 Glenn Lane
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in 
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.kerbaya.ieee754lib;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.util.Random;

/**
 * Will not return NaN's, infinities, or negative-zero
 */
public class RandomFp
{
	private final Random random;
	private final byte[] da;
	private final byte[] fa;
	private final DoubleBuffer db;
	private final FloatBuffer fb;
	
	public RandomFp()
	{
		random = new Random();
		da = new byte[8];
		fa = new byte[4];
		db = ByteBuffer.wrap(da).asDoubleBuffer();
		fb = ByteBuffer.wrap(fa).asFloatBuffer();
	}
	
	public double nextDouble()
	{
		double v;
		do
		{
			random.nextBytes(da);
			v = db.get(0);
		} while (Double.isNaN(v) || Double.isInfinite(v) || v == -0D);
		return v;
	}
	
	public float nextFloat()
	{
		float v;
		do
		{
			random.nextBytes(fa);
			v = fb.get(0);
		} while (Float.isNaN(v) || Float.isInfinite(v) || v == -0F);
		return v;
	}
	
	public IEEE754 nextIee754(IEEE754Format format)
	{
		int bitCount = format.getExponentLength() 
				+ format.getMantissaLength() 
				+ 1;
		byte[] buf = new byte[
				(bitCount >> 3) 
				+ ((bitCount & 0x7) == 0 ? 0 : 1)];
		IEEE754 v;
		do
		{
			random.nextBytes(buf);
			v = IEEE754.decode(format, BitUtils.wrapSource(buf));
		} while (IEEE754.NaN.equals(v)
				|| IEEE754.POSITIVE_INFINITY.equals(v) 
				|| IEEE754.NEGATIVE_INFINITY.equals(v)
				|| IEEE754.NEGATIVE_ZERO.equals(v));
		return v;
	}
}
