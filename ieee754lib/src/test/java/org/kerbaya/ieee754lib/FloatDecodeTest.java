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
	private void testDecode(float expectedFloat)
	{
		byte[] expectedFloatBuf = new byte[4];
		ByteBuffer.wrap(expectedFloatBuf).asFloatBuffer().put(0, expectedFloat);
		IEEE754 actualIeee = IEEE754.decode(
				IEEE754Format.SINGLE, BitUtils.wrapSource(expectedFloatBuf));
		if (Float.isNaN(expectedFloat))
		{
			Assert.assertSame("decoding NaN", IEEE754.NaN, actualIeee);
			/*
			 * Our library doesn't retain NaN values, so we'll narrow the value 
			 * and buffer to the NaN constant (only the first mantissa bit is 
			 * set)
			 */
			expectedFloat = Float.NaN;
			ByteBuffer.wrap(expectedFloatBuf).asFloatBuffer().put(
					0, expectedFloat);
		}
		else if (expectedFloat == Float.POSITIVE_INFINITY)
		{
			Assert.assertSame("decoding positive infinity", IEEE754.POSITIVE_INFINITY, actualIeee);
		}
		else if (expectedFloat == Float.NEGATIVE_INFINITY)
		{
			Assert.assertSame("decoding negative infinity", IEEE754.NEGATIVE_INFINITY, actualIeee);
		}
		else if (Float.floatToIntBits(expectedFloat)
				== Float.floatToIntBits(-0F))
		{
			Assert.assertSame("decoding negative zero", IEEE754.NEGATIVE_ZERO, actualIeee);
		}
		else if (expectedFloat == 0F)
		{
			Assert.assertSame("decoding positive zero", IEEE754.POSITIVE_ZERO, actualIeee);
		}
		else
		{
			Assert.assertTrue("decoding number", actualIeee instanceof IEEE754Number);
			IEEE754Number in = (IEEE754Number) actualIeee;
			float actualFloat = (float) (in.getSignificand().doubleValue() 
					* Math.pow(2D, in.getExponent().doubleValue()));
			Assert.assertEquals("correct exponent & significand", expectedFloat, actualFloat, 0F);
		}
		
		byte[] actualFloatBuf = new byte[4];
		actualIeee.toBits(IEEE754Format.SINGLE, BitUtils.wrapSink(actualFloatBuf));
		Assert.assertArrayEquals("round-trip encoding", expectedFloatBuf, actualFloatBuf);
		
		byte[] expectedDoubleBuf = new byte[8];
		ByteBuffer.wrap(expectedDoubleBuf).asDoubleBuffer().put(0, expectedFloat);
		byte[] actualDoubleBuf = new byte[8];
		actualIeee.toBits(IEEE754Format.DOUBLE, BitUtils.wrapSink(actualDoubleBuf));
		Assert.assertArrayEquals("upcast encoding", expectedDoubleBuf, actualDoubleBuf);
	}
	
	@Test
	public void decodeConstants()
	{
		testDecode(Float.POSITIVE_INFINITY);
		testDecode(Float.NEGATIVE_INFINITY);
		testDecode(Float.NaN);
		testDecode(0F);
		testDecode(-0F);
		testDecode(Float.MAX_VALUE);
		testDecode(Float.MIN_VALUE);
		testDecode(Float.MIN_NORMAL);
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
