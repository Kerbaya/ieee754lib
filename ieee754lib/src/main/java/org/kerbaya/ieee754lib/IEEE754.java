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

import java.math.BigInteger;
import java.nio.ByteBuffer;

public abstract class IEEE754 extends Number
{
	private static final long serialVersionUID = 1036977833646325724L;
	private static final int HC_PRIME = 31;

	private static final class Constant extends IEEE754
	{
		private static final long serialVersionUID = -947464815030155889L;
		private final boolean negative;
		private final boolean exponent;
		private final boolean significand;
		
		public Constant(boolean negative, boolean exponent, boolean significand)
		{
			this.negative = negative;
			this.exponent = exponent;
			this.significand = significand;
		}

		@Override
		public void toBits(IEEE754Format format, BitSink out)
		{
			out.write(negative);
			for (int i = 0; i < format.getExponentLength(); i++)
			{
				out.write(exponent);
			}
			out.write(significand);
			for (int i = 1; i < format.getMantissaLength(); i++)
			{
				out.write(false);
			}
		}
		
		@Override
		public int hashCode()
		{
			int hc = 1;
			hc = hc * HC_PRIME + Boolean.valueOf(negative).hashCode();
			hc = hc * HC_PRIME + Boolean.valueOf(exponent).hashCode();
			hc = hc * HC_PRIME + Boolean.valueOf(significand).hashCode();
			return hc;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if (obj == this)
			{
				return true;
			}
			if (!(obj instanceof Constant))
			{
				return false;
			}
			Constant other = (Constant) obj;
			return negative == other.negative
					&& exponent == other.exponent
					&& significand == other.significand;
		}
	}
	
	public static final IEEE754 POSITIVE_ZERO = 
			new Constant(false, false, false);
	public static final IEEE754 NEGATIVE_ZERO = 
			new Constant(true, false, false);
	public static final IEEE754 POSITIVE_INFINITY =
			new Constant(false, true, false);
	public static final IEEE754 NEGATIVE_INFINITY =
			new Constant(true, true, false);
	public static final IEEE754 NaN = new Constant(false, true, true);
	
	public static final class IEEE754Number extends IEEE754
	{
		private static final long serialVersionUID = -3484143002408507123L;
		private final BigInteger exponent;
		private final BigInteger significand;
		
		public IEEE754Number(
				BigInteger exponent,
				BigInteger significand)
		{
			if (exponent == null)
			{
				throw new NullPointerException();
			}
			this.exponent = exponent;
			if (significand == null)
			{
				throw new NullPointerException();
			}
			this.significand = significand;
		}
		
		public BigInteger getExponent()
		{
			return exponent;
		}
		
		public BigInteger getSignificand()
		{
			return significand;
		}

		@Override
		public void toBits(IEEE754Format format, BitSink out)
		{
			final boolean negative;
			BigInteger roundedSignificand;
			BigInteger adjustedExponent = this.exponent;
			if (this.significand.signum() == -1)
			{
				negative = true;
				roundedSignificand = significand.negate();
			}
			else
			{
				negative = false;
				roundedSignificand = significand;
			}
			while (roundedSignificand.bitLength() > 
					format.getMantissaLength() + 1)
			{
				boolean carry = roundedSignificand.testBit(0);
				roundedSignificand = roundedSignificand.shiftRight(1);
				adjustedExponent = adjustedExponent.add(BigInteger.ONE);
				if (carry)
				{
					int bitIndex = 0;
					while (roundedSignificand.testBit(bitIndex))
					{
						roundedSignificand = 
								roundedSignificand.clearBit(bitIndex);
						bitIndex++;
					}
					roundedSignificand = roundedSignificand.setBit(bitIndex);
				}
			}
			int significandLength = roundedSignificand.bitLength() - 1;
			BigInteger exponentBits = 
					adjustedExponent.add(format.getExponentBias())
							.add(BigInteger.valueOf(significandLength));
			int mantissaBitIndex;
			if (exponentBits.compareTo(BigInteger.ZERO) <= 0)
			{
				if (BigInteger.valueOf(format.getMantissaLength())
						.add(exponentBits).compareTo(BigInteger.ZERO) <= 0)
				{
					IEEE754 zero = negative ? NEGATIVE_ZERO : POSITIVE_ZERO;
					zero.toBits(format, out);
					return;
				}
				mantissaBitIndex = significandLength - exponentBits.intValue();
				exponentBits = BigInteger.ZERO;
			}
			else
			{
				int exponentBitsLength = exponentBits.bitLength();
				if (exponentBitsLength > format.getExponentLength() || (
						exponentBitsLength == format.getExponentLength()
						&& exponentBitsLength == exponentBits.bitCount()))
				{
					IEEE754 infinity = negative ? 
							NEGATIVE_INFINITY : POSITIVE_INFINITY;
					infinity.toBits(format, out);
					return;
				}
				mantissaBitIndex = significandLength - 1;
			}
			
			out.write(negative);
			for (int i = format.getExponentLength() - 1; i >= 0; i--)
			{
				out.write(exponentBits.testBit(i));
			}
			for (int i = 0; i < format.getMantissaLength(); i++)
			{
				if (mantissaBitIndex < 0)
				{
					out.write(false);
				}
				else
				{
					out.write(roundedSignificand.testBit(mantissaBitIndex));
					mantissaBitIndex--;
				}
			}
		}
		
		@Override
		public int hashCode()
		{
			int hc = HC_PRIME;
			hc = hc * HC_PRIME + exponent.hashCode();
			hc = hc * HC_PRIME + significand.hashCode();
			return hc;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if (obj == this)
			{
				return true;
			}
			if (!(obj instanceof IEEE754Number))
			{
				return false;
			}
			IEEE754Number other = (IEEE754Number) obj;
			return exponent.equals(other.exponent)
					&& significand.equals(other.significand);
		}
	}
	
	private IEEE754() {}
	
	public abstract void toBits(IEEE754Format format, BitSink out);
	
	@Override
	public final double doubleValue()
	{
		ByteBuffer buf = ByteBuffer.allocateDirect(8);
		toBits(IEEE754Standard.DOUBLE, BitUtils.wrapSink(buf));
		buf.rewind();
		return buf.asDoubleBuffer().get();
	}
	
	@Override
	public final float floatValue()
	{
		return (float) doubleValue();
	}
	
	@Override
	public final int intValue()
	{
		return (int) doubleValue();
	}
	
	@Override
	public final long longValue()
	{
		return (long) doubleValue();
	}
	
	public static IEEE754 valueOf(double value)
	{
		ByteBuffer buf = ByteBuffer.allocateDirect(8);
		buf.asDoubleBuffer().put(value);
		return decode(IEEE754Standard.DOUBLE, BitUtils.wrapSource(buf));
	}
	
	public static IEEE754 valueOf(float value)
	{
		ByteBuffer buf = ByteBuffer.allocateDirect(4);
		buf.asFloatBuffer().put(value);
		return decode(IEEE754Standard.SINGLE, BitUtils.wrapSource(buf));
	}
	
	public static IEEE754 decode(IEEE754Format format, BitSource in)
	{
		final boolean negative = in.next();
		BigInteger exponentBits = BigInteger.ZERO;
		for (int i = format.getExponentLength() - 1; i >= 0; i--)
		{
			if (in.next())
			{
				exponentBits = exponentBits.setBit(i);
			}
		}
		int exponentBitCount = exponentBits.bitCount();
		if (exponentBitCount == format.getExponentLength())
		{
			boolean nan = false;
			for (int i = 0; i < format.getMantissaLength(); i++)
			{
				if (in.next())
				{
					nan = true;
				}
			}
			return nan ? NaN : negative ? 
					NEGATIVE_INFINITY : POSITIVE_INFINITY;
		}
		
		BigInteger mantissa = BigInteger.ZERO;
		for (int i = format.getMantissaLength() - 1; i >= 0; i--)
		{
			if (in.next())
			{
				mantissa = mantissa.setBit(i);
			}
		}
		
		if (exponentBitCount == 0 && mantissa.bitCount() == 0)
		{
			return negative ? NEGATIVE_ZERO : POSITIVE_ZERO;
		}
		
		BigInteger exponent = BigInteger.valueOf(format.getMantissaLength())
				.negate();
		if (exponentBitCount == 0)
		{
			exponent = exponent.add(BigInteger.ONE);
		}
		else
		{
			mantissa = mantissa.setBit(format.getMantissaLength());
		}
		
		int mantissaRightTrimLength = mantissa.getLowestSetBit();
		if (mantissaRightTrimLength > 0)
		{
			mantissa = mantissa.shiftRight(mantissaRightTrimLength);
			exponent = exponent.add(
					BigInteger.valueOf(mantissaRightTrimLength));
		}
		
		exponent = exponent.add(exponentBits)
				.subtract(format.getExponentBias());
		return new IEEE754Number(
				exponent, 
				negative ? mantissa.negate() : mantissa);
	}
	
	@Override
	public final String toString()
	{
		return Double.toString(doubleValue());
	}
	
}
