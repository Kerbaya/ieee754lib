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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class IEEE754Test
{
	
	private Random random;
	
	@Before
	public void initRandom()
	{
		random = new Random();
	}
	
	@Test
	public void testDoubleConstants()
	{
		Assert.assertEquals(IEEE754.NaN, 
				IEEE754.valueOf(Double.NaN));
		Assert.assertEquals(IEEE754.NEGATIVE_INFINITY, 
				IEEE754.valueOf(Double.NEGATIVE_INFINITY));
		Assert.assertEquals(IEEE754.POSITIVE_INFINITY, 
				IEEE754.valueOf(Double.POSITIVE_INFINITY));
		Assert.assertEquals(IEEE754.NEGATIVE_ZERO, 
				IEEE754.valueOf(-0D));
		Assert.assertEquals(IEEE754.POSITIVE_ZERO, 
				IEEE754.valueOf(0D));
	}
	
	@Test
	public void testFloatConstants()
	{
		Assert.assertEquals(IEEE754.NaN, 
				IEEE754.valueOf(Float.NaN));
		Assert.assertEquals(IEEE754.NEGATIVE_INFINITY, 
				IEEE754.valueOf(Float.NEGATIVE_INFINITY));
		Assert.assertEquals(IEEE754.POSITIVE_INFINITY, 
				IEEE754.valueOf(Float.POSITIVE_INFINITY));
		Assert.assertEquals(IEEE754.NEGATIVE_ZERO, 
				IEEE754.valueOf(-0F));
		Assert.assertEquals(IEEE754.POSITIVE_ZERO, 
				IEEE754.valueOf(0F));
	}
	
	@Test
	public void testDoubleRandoms()
	{
		byte[] bytes = new byte[8];
		DoubleBuffer buf = ByteBuffer.wrap(bytes).asDoubleBuffer();
		for (int i = 0; i < 100000; i++)
		{
			random.nextBytes(bytes);
			double java = buf.get(0);
			IEEE754 ieee = IEEE754.decode(
					IEEE754Standard.DOUBLE, BitUtils.wrapSource(bytes));
			Assert.assertEquals(java, ieee.doubleValue(), 0D);
			float javaFloat = (float) java;
			float ieeeFloat = ieee.floatValue();
			Assert.assertEquals(javaFloat, ieeeFloat, 0F);
		}
	}
	
	@Test
	public void testDoubleFloats()
	{
		byte[] bytes = new byte[4];
		FloatBuffer buf = ByteBuffer.wrap(bytes).asFloatBuffer();
		for (int i = 0; i < 100000; i++)
		{
			random.nextBytes(bytes);
			float java = buf.get(0);
			float ieee = IEEE754.decode(
					IEEE754Standard.SINGLE, BitUtils.wrapSource(bytes))
							.floatValue();
			Assert.assertEquals(java, ieee, 0F);
		}
	}
}
