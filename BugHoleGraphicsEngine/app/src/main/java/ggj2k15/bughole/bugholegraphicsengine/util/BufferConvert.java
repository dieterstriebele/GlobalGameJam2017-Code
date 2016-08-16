package ggj2k15.bughole.bugholegraphicsengine.util;

/**
 * Created by AHoppe on 07.02.2016.
 */
public class BufferConvert {

        public static int ConvertBytesAtBufferOffsetToInt(byte[] buf, int offset) {
            int intValue =
                    (buf[offset + 0] << 24) |
                            ((buf[offset + 1] & 0xff) << 16) |
                            ((buf[offset + 2] & 0xff) << 8) |
                            (buf[offset + 3] & 0xff);
            return intValue;
        }

        public static float ConvertBytesAtBufferOffsetToIntAndThenFloat(byte[] buf, int offset) {
            int rawValue =
                    (buf[offset + 0] << 24) |
                            ((buf[offset + 1] & 0xff) << 16) |
                            ((buf[offset + 2] & 0xff) << 8) |
                            (buf[offset + 3] & 0xff);
            float convertedToFloat = Float.intBitsToFloat(rawValue);
            return convertedToFloat;
        }

}
