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

abstract class BitSinkImpl implements BitSink
{
	private static final int MASK_CLOSED = 0;
	private static final int MASK_FIRST_BIT = 0x80;
	private static final int MASK_LAST_BIT = 0x1;
	
	private int mask = MASK_FIRST_BIT;
	private int current;
	
	@Override
	public void close()
	{
		if (mask != MASK_CLOSED)
		{
			if (mask != MASK_FIRST_BIT)
			{
				writeByte((byte) current);
			}
			mask = MASK_CLOSED;
		}
	}

	@Override
	public void write(boolean bit)
	{
		if (mask == MASK_CLOSED)
		{
			throw new IllegalStateException();
		}
		if (bit)
		{
			current |= mask;
		}
		if (mask == MASK_LAST_BIT)
		{
			writeByte((byte) current);
			mask = MASK_FIRST_BIT;
			current = 0;
		}
		else
		{
			mask >>= 1;
		}
	}
	
	protected abstract void writeByte(byte b);
}
