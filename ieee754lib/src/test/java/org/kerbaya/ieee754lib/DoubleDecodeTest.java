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
import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.kerbaya.ieee754lib.IEEE754.IEEE754Number;

public class DoubleDecodeTest
{
	private void testDecode(double expectedDouble)
	{
		byte[] expectedDoubleBuf = new byte[8];
		ByteBuffer.wrap(expectedDoubleBuf).asDoubleBuffer().put(0, expectedDouble);
		IEEE754 actualIeee = IEEE754.decode(
				IEEE754Standard.DOUBLE, BitUtils.wrapSource(expectedDoubleBuf));
		if (Double.isNaN(expectedDouble))
		{
			Assert.assertSame("decoding NaN", IEEE754.NaN, actualIeee);
		}
		else
		{
			if (expectedDouble == Double.POSITIVE_INFINITY)
			{
				Assert.assertSame("decoding positive infinity", IEEE754.POSITIVE_INFINITY, actualIeee);
			}
			else if (expectedDouble == Double.NEGATIVE_INFINITY)
			{
				Assert.assertSame("decoding negative infinity", IEEE754.NEGATIVE_INFINITY, actualIeee);
			}
			else if (Double.doubleToLongBits(expectedDouble) 
					== Double.doubleToLongBits(-0D))
			{
				Assert.assertSame("decoding negative zero", IEEE754.NEGATIVE_ZERO, actualIeee);
			}
			else if (expectedDouble == 0D)
			{
				Assert.assertSame("decoding positive zero", IEEE754.POSITIVE_ZERO, actualIeee);
			}
			else
			{
				Assert.assertTrue("decoding number", actualIeee instanceof IEEE754Number);
				IEEE754Number in = (IEEE754Number) actualIeee;
				double actualDouble = in.getSignificand().doubleValue() 
						* Math.pow(2D, in.getExponent().doubleValue());
				Assert.assertEquals("correct exponent & significand", expectedDouble, actualDouble, 0D);
			}
			byte[] actualDoubleBuf = new byte[8];
			actualIeee.toBits(IEEE754Standard.DOUBLE, BitUtils.wrapSink(actualDoubleBuf));
			Assert.assertArrayEquals("round-trip encoding", expectedDoubleBuf, actualDoubleBuf);
			byte[] expectedFloatBuf = new byte[4];
			ByteBuffer.wrap(expectedFloatBuf).asFloatBuffer().put(0, (float) expectedDouble);
			byte[] actualFloatBuf = new byte[4];
			actualIeee.toBits(IEEE754Standard.SINGLE, BitUtils.wrapSink(actualFloatBuf));
			if (!Arrays.equals(expectedFloatBuf, actualFloatBuf))
			{
				print(expectedDoubleBuf, IEEE754Standard.DOUBLE);
				print(expectedFloatBuf, IEEE754Standard.SINGLE);
				print(actualFloatBuf, IEEE754Standard.SINGLE);
			}
//			Assert.assertArrayEquals("downcast encoding", expectedFloatBuf, actualFloatBuf);
		}
	}
	
	private static void print(byte[] buf, IEEE754Format format)
	{
		print(buf, 0);
		System.out.print(' ');
		for (int i = 0; i < IEEE754Standard.DOUBLE.getExponentLength() - format.getExponentLength(); i++)
		{
			System.out.print(' ');
		}
		print(buf, 1, format.getExponentLength());
		System.out.print(' ');
		print(buf, format.getExponentLength() + 1, format.getMantissaLength());
		System.out.println();
	}
	
	private static void print(byte[] buf, int off, int len)
	{
		for (int i = off; i < off + len; i++)
		{
			print(buf, i);
		}
	}
	
	private static void print(byte[] buf, int off)
	{
		int idx = off / 8;
		int mask = 0x80 >> (off % 8);
		System.out.print((buf[idx] & mask) == 0 ? '0' : '1');
	}
	
	@Test
	public void decodeConstants()
	{
//		testDecode(Double.POSITIVE_INFINITY);
//		testDecode(Double.NEGATIVE_INFINITY);
//		testDecode(Double.NaN);
//		testDecode(0D);
//		testDecode(-0D);
	}
	
	@Test
	public void decodeRandom()
	{
		testDecode(Double.longBitsToDouble(0b1011111010100101101101110111001100111100111001000001101000010001L));
//		long l = 0b1011011010011011001110011000100011100000010001010100100110010011L;
//		double d = Double.longBitsToDouble(l);
//		byte[] b = new byte[8];
//		ByteBuffer.wrap(b).asDoubleBuffer().put(0, d);
//		print(b);
//		System.out.println(d);
////		System.out.println((float)Double.MAX_VALUE);
//		RandomFp r = new RandomFp();
//		for (int i = 0; i < 100000; i++)
//		{
//			testDecode(r.nextDouble());
//		}
	}
}
