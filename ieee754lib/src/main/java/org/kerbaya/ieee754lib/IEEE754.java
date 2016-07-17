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
import java.util.Objects;

public abstract class IEEE754 extends Number
{
	private static final long serialVersionUID = 1036977833646325724L;

	public static final class Constant extends IEEE754
	{
		private static final long serialVersionUID = -947464815030155889L;
		private final boolean negative;
		private final boolean exponent;
		private final boolean mantissa;
		
		public Constant(boolean negative, boolean exponent, boolean mantissa)
		{
			this.negative = negative;
			this.exponent = exponent;
			this.mantissa = mantissa;
		}

		@Override
		protected void toBitsImpl(IEEE754Format format, BitSink out)
		{
			out.write(negative);
			for (int i = 0; i < format.getExponentLength(); i++)
			{
				out.write(exponent);
			}
			out.write(mantissa);
			for (int i = 1; i < format.getMantissaLength(); i++)
			{
				out.write(false);
			}
		}
	}
	
	public static final IEEE754 POSITIVE_ZERO;
	public static final IEEE754 NEGATIVE_ZERO;
	public static final IEEE754 POSITIVE_INFINITY;
	public static final IEEE754 NEGATIVE_INFINITY;
	public static final IEEE754 NaN;
	
	static
	{
		POSITIVE_ZERO = new Constant(false, false, false);
		NEGATIVE_ZERO = new Constant(true, false, false);
		POSITIVE_INFINITY = new Constant(false, true, false);
		NEGATIVE_INFINITY = new Constant(true, true, false);
		NaN = new Constant(false, true, true);
	}
	
	public static final class IEEE754Number extends IEEE754
	{
		private static final long serialVersionUID = -3484143002408507123L;
		private final boolean negative;
		private final BigInteger exponent;
		private final BigInteger mantissa;
		
		public IEEE754Number(
				boolean negative, 
				BigInteger exponent,
				BigInteger mantissa)
		{
			this.negative = negative;
			this.exponent = exponent;
			this.mantissa = mantissa;
		}
		
		public boolean isNegative()
		{
			return negative;
		}
		
		public BigInteger getExponent()
		{
			return exponent;
		}
		
		public BigInteger getMantissa()
		{
			return mantissa;
		}

		@Override
		protected void toBitsImpl(IEEE754Format format, BitSink out)
		{
			int significandLength = mantissa.bitLength() - 1;
			BigInteger exponentBits = exponent.add(format.getExponentBias())
					.add(BigInteger.valueOf(significandLength));
			int mantissaBitIndex;
			if (exponentBits.compareTo(BigInteger.ZERO) <= 0)
			{
				if (BigInteger.valueOf(format.getMantissaLength())
						.add(exponentBits).compareTo(BigInteger.ZERO) <= 0)
				{
					IEEE754 zero = negative ? NEGATIVE_ZERO : POSITIVE_ZERO;
					zero.toBitsImpl(format, out);
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
					infinity.toBitsImpl(format, out);
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
					out.write(mantissa.testBit(mantissaBitIndex));
					mantissaBitIndex--;
				}
			}
		}
		
		@Override
		public int hashCode()
		{
			return Objects.hash(negative, exponent, mantissa);
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
			return negative == other.negative
					&& exponent.equals(other.exponent)
					&& mantissa.equals(other.mantissa);
		}
	}
	
	private IEEE754() {}
	
	protected abstract void toBitsImpl(IEEE754Format format, BitSink out);
	
	public final void toBits(IEEE754Format format, BitSink out)
	{
		toBitsImpl(format, out);
	}
	
	@Override
	public final double doubleValue()
	{
		ByteBuffer buf = ByteBuffer.allocateDirect(8);
		toBitsImpl(IEEE754Standard.DOUBLE, BitUtils.wrapSink(buf));
		buf.rewind();
		return buf.asDoubleBuffer().get();
	}
	
	@Override
	public final float floatValue()
	{
		ByteBuffer buf = ByteBuffer.allocateDirect(4);
		toBitsImpl(IEEE754Standard.SINGLE, BitUtils.wrapSink(buf));
		buf.rewind();
		return buf.asFloatBuffer().get();
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
		boolean negative = in.next();
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
		return new IEEE754Number(negative, exponent, mantissa);
	}
	
	@Override
	public final String toString()
	{
		return Double.toString(doubleValue());
	}
	
}
