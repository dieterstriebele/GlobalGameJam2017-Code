package util;

public class BufferConvert {
	public static void ConvertFloatToIntAndWriteToBufferAtOffset(float inValue, byte[] buf, int offset) {
		WriteIntToBufferAtOffset(Float.floatToRawIntBits(inValue), buf, offset);
	}
	
	public static void WriteIntToBufferAtOffset(int inValue, byte[] buf, int offset) {
	    buf[offset + 0] = (byte) (inValue >> 24);
	    buf[offset + 1] = (byte) (inValue >> 16) ;
	    buf[offset + 2] = (byte) (inValue >> 8);
	    buf[offset + 3] = (byte) (inValue);		
	}
	
	public static float ReadIntFromBufferAtOffsetAndConvertToFloat(byte[] buffer, int offset) {
		int valueAsFloat = ReadIntFromBufferAtOffset(buffer, offset);		
		return Float.intBitsToFloat(valueAsFloat);
	}
	
	public static int ReadIntFromBufferAtOffset(byte[] buffer, int offset) {
		int value = (buffer[offset + 0]         << 24) |
				   ((buffer[offset + 1] & 0xff) << 16) |
				   ((buffer[offset + 2] & 0xff) <<  8) |
				    (buffer[offset + 3] & 0xff);
		
		return value;
	}

}
