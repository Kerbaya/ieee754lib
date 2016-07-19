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
import java.nio.FloatBuffer;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

public class FloatEncodeTest
{
	private static final int RANDOM_COUNT = 100000;
	
	private final byte[] expecteds;
	private final byte[] actuals;
	private final FloatBuffer buffer;
	
	public FloatEncodeTest()
	{
		expecteds = new byte[4];
		actuals = new byte[4];
		buffer = ByteBuffer.wrap(expecteds).asFloatBuffer();
	}
	
	private boolean testEquals(float expected, IEEE754 actual)
	{
		buffer.put(0, expected);
		actual.toBits(IEEE754Standard.SINGLE, BitUtils.wrapSink(actuals));
		return Arrays.equals(expecteds,  actuals);
	}
	
	private void assertEquals(float expected, IEEE754 actual)
	{
		buffer.put(0, expected);
		actual.toBits(IEEE754Standard.SINGLE, BitUtils.wrapSink(actuals));
		Assert.assertArrayEquals(expecteds, actuals);
	}
	
	@Test
	public void encodeConstants()
	{
		assertEquals(Float.NaN, IEEE754.NaN);
		assertEquals(Float.NEGATIVE_INFINITY, IEEE754.NEGATIVE_INFINITY);
		assertEquals(Float.POSITIVE_INFINITY, IEEE754.POSITIVE_INFINITY);
		assertEquals(0F, IEEE754.POSITIVE_ZERO);
		assertEquals(-0F, IEEE754.NEGATIVE_ZERO);
	}
	
	@Test
	public void encodeRandom()
	{
		RandomFp random = new RandomFp();
		for (int i = 0; i < RANDOM_COUNT; i++)
		{
			float f = random.nextFloat();
			assertEquals(f, IEEE754.valueOf(f));
		}
	}
	
	private static void print(byte[] ba)
	{
		for (byte b: ba)
		{
			for (int m = 0x80; m != 0; m >>= 1)
			{
				System.out.print((m & b) == 0 ? '0' : '1');
			}
		}
		System.out.println();
	}
	
	@Test
	public void randomDownCast()
	{
		RandomFp random = new RandomFp();
		for (int i = 0; i < RANDOM_COUNT; i++)
		{
			double d = random.nextDouble();
			float f = (float) d;
			IEEE754 ie = IEEE754.valueOf(d);
			if (!testEquals(f, ie))
			{
				System.out.println(d);
				byte[] ba = new byte[8];
				ByteBuffer.wrap(ba).asDoubleBuffer().put(0, d);
				print(ba);

				System.out.println(f);
				print(expecteds);
				print(actuals);
				Assert.fail();
			}
		}
	}
}
