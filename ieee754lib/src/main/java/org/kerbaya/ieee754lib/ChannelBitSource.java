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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

final class ChannelBitSource extends BitSourceImpl
{
	private final ReadableByteChannel source;
	private final ByteBuffer buffer;

	public ChannelBitSource(ReadableByteChannel source)
	{
		this.source = source;
		buffer = ByteBuffer.allocateDirect(1);
	}

	@Override
	protected byte nextByte()
	{
		if (buffer.hasRemaining())
		{
			int count;
			try
			{
				count = source.read(buffer);
			}
			catch (IOException e)
			{
				throw new IllegalStateException(e);
			}
			if (count < 1)
			{
				throw new IllegalStateException();
			}
		}
		byte r = buffer.get(0);
		buffer.rewind();
		return r;
	}
}
