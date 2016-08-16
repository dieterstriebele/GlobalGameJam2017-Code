import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class BinaryFloatValidator {

	public static void main(String[] args) {
		System.out.print("Please enter path to binary file: ");
		
		InputStreamReader input_stream = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input_stream);
		
		try
		{
			String path_to_file = (String) reader.readLine();
			File some_file = new File(path_to_file);		
			
			if(!some_file.exists())
				System.out.println("Invalid path entered.");
			else
			{
				DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(path_to_file)));
				
				try {
					while(in.available() > 0)
					{
						float floatData = in.readFloat();
						System.out.println(String.format("%f", floatData));					
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

}
