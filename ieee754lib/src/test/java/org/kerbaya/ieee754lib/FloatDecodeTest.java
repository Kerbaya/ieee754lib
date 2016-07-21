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

import org.junit.Assert;
import org.junit.Test;
import org.kerbaya.ieee754lib.IEEE754.IEEE754Number;

public class FloatDecodeTest
{
	private void testDecode(float testValue)
	{
		ByteBuffer buf = ByteBuffer.allocateDirect(8);
		buf.asFloatBuffer().put(0, testValue);
		IEEE754 ieee = IEEE754.decode(
				IEEE754Standard.SINGLE, BitUtils.wrapSource(buf));
		if (testValue == Float.POSITIVE_INFINITY)
		{
			Assert.assertSame(IEEE754.POSITIVE_INFINITY, ieee);
		}
		else if (testValue == Float.NEGATIVE_INFINITY)
		{
			Assert.assertSame(IEEE754.NEGATIVE_INFINITY, ieee);
		}
		else if (Float.floatToIntBits(testValue) 
				== Float.floatToIntBits(-0F))
		{
			Assert.assertSame(IEEE754.NEGATIVE_ZERO, ieee);
		}
		else if (testValue == 0F)
		{
			Assert.assertSame(IEEE754.POSITIVE_ZERO, ieee);
		}
		else if (Float.isNaN(testValue))
		{
			Assert.assertSame(IEEE754.NaN, ieee);
		}
		else
		{
			Assert.assertTrue(ieee instanceof IEEE754Number);
			IEEE754Number in = (IEEE754Number) ieee;
			float actual = in.getSignificand().floatValue() 
					* (float) Math.pow(2D, in.getExponent().doubleValue());
			if (actual != testValue)
			{
				System.out.println(in);
			}
			Assert.assertEquals(testValue, actual, 0F);
		}
		Assert.assertEquals(testValue, ieee.floatValue(), 0F);
	}
	
	@Test
	public void decodeConstants()
	{
		testDecode(Float.POSITIVE_INFINITY);
		testDecode(Float.NEGATIVE_INFINITY);
		testDecode(Float.NaN);
		testDecode(0F);
		testDecode(-0F);
	}
	
	@Test
	public void decodeRandom()
	{
		RandomFp r = new RandomFp();
		for (int i = 0; i < 100000; i++)
		{
			testDecode(r.nextFloat());
		}
	}
}
