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

import org.junit.Assert;
import org.junit.Test;

public class DoubleEncodeTest
{
	private static final int RANDOM_COUNT = 100000;
	
	private final byte[] expecteds;
	private final byte[] actuals;
	private final DoubleBuffer buffer;
	
	public DoubleEncodeTest()
	{
		expecteds = new byte[8];
		actuals = new byte[8];
		buffer = ByteBuffer.wrap(expecteds).asDoubleBuffer();
	}
	
	private void test(double expected, IEEE754 actual)
	{
		buffer.put(0, expected);
		actual.toBits(IEEE754Standard.DOUBLE, BitUtils.wrapSink(actuals));
		Assert.assertArrayEquals(expecteds, actuals);
	}
	
	@Test
	public void encodeConstants()
	{
		test(Double.NaN, IEEE754.NaN);
		test(Double.NEGATIVE_INFINITY, IEEE754.NEGATIVE_INFINITY);
		test(Double.POSITIVE_INFINITY, IEEE754.POSITIVE_INFINITY);
		test(0D, IEEE754.POSITIVE_ZERO);
		test(-0D, IEEE754.NEGATIVE_ZERO);
	}
	
	@Test
	public void encodeRandom()
	{
		RandomFp random = new RandomFp();
		for (int i = 0; i < RANDOM_COUNT; i++)
		{
			double d = random.nextDouble();
			test(d, IEEE754.valueOf(d));
		}
	}
}
