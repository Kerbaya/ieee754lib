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
 * Generates random floating point values
 * 
 * Note that there are multiple patterns that represent NaN, so expect NaNs to 
 * occur more frequently than other numbers.
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
		random.nextBytes(da);
		return db.get(0);
	}
	
	public float nextFloat()
	{
		random.nextBytes(fa);
		return fb.get(0);
	}
	
	public IEEE754 nextIee754(IEEE754Format format)
	{
		int bitCount = format.getExponentLength() 
				+ format.getMantissaLength() 
				+ 1;
		byte[] buf = new byte[
				(bitCount >> 3) 
				+ ((bitCount & 0x7) == 0 ? 0 : 1)];
		random.nextBytes(buf);
		return IEEE754.decode(format, BitUtils.wrapSource(buf));
	}
}
