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

public enum IEEE754Standard implements IEEE754Format
{
	HALF(5, 10, 15),
	SINGLE(8, 23, 127),
	DOUBLE(11, 52, 1023),
	QUADRUPLE(15, 112, 16383),
	;
	
	private final int exponentLength;
	private final int mantissaLength;
	private final BigInteger exponentBias;
	
	private IEEE754Standard(
			int exponentLength, 
			int mantissaLength,
			int exponentBias)
	{
		this.exponentLength = exponentLength;
		this.mantissaLength = mantissaLength;
		this.exponentBias = BigInteger.valueOf(exponentBias);
	}

	@Override
	public int getExponentLength()
	{
		return exponentLength;
	}

	@Override
	public int getMantissaLength()
	{
		return mantissaLength;
	}

	@Override
	public BigInteger getExponentBias()
	{
		return exponentBias;
	}
}