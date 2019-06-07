/*******************************************************************************
 * Copyright 2019 Brandon
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.ocelot.api.utils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.LongBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.BufferUtils;

/**
 * 
 * Allows the ability to easily store data in the buffers for the default data
 * types.
 * 
 * @author Ocelot5836
 * @since Jun 6, 2019
 *
 */
public class Buffers
{

	/**
	 * Stores the data in a new byte buffer.
	 * 
	 * @param data The data to put in a new buffer
	 * @return The buffer with the data inside
	 */
	public static ByteBuffer storeDataInByteBuffer(byte[] data)
	{
		ByteBuffer buffer = BufferUtils.createByteBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	/**
	 * Stores the data in a new short buffer.
	 * 
	 * @param data The data to put in a new buffer
	 * @return The buffer with the data inside
	 */
	public static ShortBuffer storeDataInShortBuffer(short[] data)
	{
		ShortBuffer buffer = BufferUtils.createShortBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	/**
	 * Stores the data in a new char buffer.
	 * 
	 * @param data The data to put in a new buffer
	 * @return The buffer with the data inside
	 */
	public static CharBuffer storeDataInCharBuffer(char[] data)
	{
		CharBuffer buffer = BufferUtils.createCharBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	/**
	 * Stores the data in a new int buffer.
	 * 
	 * @param data The data to put in a new buffer
	 * @return The buffer with the data inside
	 */
	public static IntBuffer storeDataInIntBuffer(int[] data)
	{
		IntBuffer buffer = BufferUtils.createIntBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	/**
	 * Stores the data in a new long buffer.
	 * 
	 * @param data The data to put in a new buffer
	 * @return The buffer with the data inside
	 */
	public static LongBuffer storeDataInLongBuffer(long[] data)
	{
		LongBuffer buffer = BufferUtils.createLongBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	/**
	 * Stores the data in a new float buffer.
	 * 
	 * @param data The data to put in a new buffer
	 * @return The buffer with the data inside
	 */
	public static FloatBuffer storeDataInFloatBuffer(float[] data)
	{
		FloatBuffer buffer = BufferUtils.createFloatBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}

	/**
	 * Stores the data in a new double buffer.
	 * 
	 * @param data The data to put in a new buffer
	 * @return The buffer with the data inside
	 */
	public static DoubleBuffer storeDataInDoubleBuffer(double[] data)
	{
		DoubleBuffer buffer = BufferUtils.createDoubleBuffer(data.length);
		buffer.put(data);
		buffer.flip();
		return buffer;
	}
}
