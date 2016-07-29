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

public class IEEE754Format
{
	public static final IEEE754Format HALF = new IEEE754Format(5, 10, 15);
	public static final IEEE754Format SINGLE = new IEEE754Format(8, 23, 127);
	public static final IEEE754Format DOUBLE = new IEEE754Format(11, 52, 1023);
	public static final IEEE754Format QUADRUPLE = new IEEE754Format(15, 112, 16383);

	private final int exponentLength;
	private final int mantissaLength;
	private final BigInteger exponentBias;
	
	public IEEE754Format(
			int exponentLength, 
			int mantissaLength,
			BigInteger exponentBias)
	{
		if (exponentLength < 2
				|| mantissaLength < 1
				|| exponentBias == null)
		{
			throw new IllegalArgumentException();
		}
		this.exponentLength = exponentLength;
		this.mantissaLength = mantissaLength;
		this.exponentBias = exponentBias;
	}
	
	private IEEE754Format(
			int exponentLength, 
			int mantissaLength,
			int exponentBias)
	{
		this.exponentLength = exponentLength;
		this.mantissaLength = mantissaLength;
		this.exponentBias = BigInteger.valueOf(exponentBias);
	}
	
	public int getExponentLength()
	{
		return exponentLength;
	}
	
	public int getMantissaLength()
	{
		return mantissaLength;
	}
	
	public BigInteger getExponentBias()
	{
		return exponentBias;
	}
}
