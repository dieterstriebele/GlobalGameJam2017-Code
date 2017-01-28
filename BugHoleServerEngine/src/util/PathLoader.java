package util;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class PathLoader {
	public static float[] LoadPathFromFile(String inputPath)
	{
		float[] positions = null;
		
		try {
			DataInputStream inputStream = new DataInputStream(new BufferedInputStream(new FileInputStream(inputPath)));
			
			int num_positions = (int)inputStream.readFloat();
			positions = new float[num_positions * 3];
			
			for(int i=0; i<positions.length; i++)
			{
				positions[i] = inputStream.readFloat();
			}
			
			inputStream.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return positions;
	}
}
