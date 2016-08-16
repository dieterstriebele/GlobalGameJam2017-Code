import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class OBJ2BinaryConverter
{
	public static ArrayList<ArrayList<String>> all_lines_v_vt_vn_f;
	public static ArrayList<ArrayList<String>> sorted_lines_v_vt_vn;
	public static BufferedReader buffered_reader;
	
	public static void main(String[] args)
	{
		System.out.print("Please enter path to OBJ file: ");
				
		InputStreamReader input_stream = new InputStreamReader(System.in);
		BufferedReader reader = new BufferedReader(input_stream);
		
		try {
			String path_to_file = (String) reader.readLine();			
			//just for debug purposes ...
			//String path_to_file = "c:\\GlobalGameJam2015\\Spikes\\OBJ2Java\\bin\\sphere.obj";
			//String path_to_file = "D:\\Repos\\GGJ2015\\Spikes\\Assets3D\\gun_round.obj";
			File some_file = new File(path_to_file);		
				
			if(!some_file.exists()) {
				System.out.println("Invalid path entered.");
			} else if(path_to_file.lastIndexOf(".obj") == -1) {
				System.out.println("Invalid file extension.");
			} else {
				convertOBJToJavaClass(path_to_file);
			}
			System.out.println("Success!");
		} catch (Exception e) {
			System.out.println("Failed!");
			e.printStackTrace();			
		}
		return;
	}

	private static void convertOBJToJavaClass(String path_to_file)
	{
		//Perform conversion from OBJ to Java class here			
		String path_to_new_file = path_to_file.split("\\.")[0] + ".ofb";			
		
		String[] split_file = path_to_file.split("\\\\");
		int split_size = split_file.length;
		String class_name = split_file[split_size-1];
		class_name = class_name.split("\\.")[0];
				
		try
		{
			//Open File Writer
			DataOutputStream out_datastream = new DataOutputStream(new FileOutputStream(path_to_new_file));			
			FileReader file_reader = new FileReader(new File(path_to_file));
			buffered_reader 	   = new BufferedReader(file_reader);			
			
			all_lines_v_vt_vn_f  = new ArrayList<ArrayList<String>>();
			sorted_lines_v_vt_vn = new ArrayList<ArrayList<String>>();
			
			//read lines and split them into arrays
			getAllLinesAndSplitToArrays();
			
			//interpret "f" lines and group "v/vt/vn" lines accordingly
			interpretAndGroupLines();
			
			//write lines to file
			writeOBJDataAsBinary(sorted_lines_v_vt_vn, out_datastream);			
			
			//Don't forget to close files
			out_datastream.close();
			buffered_reader.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	private static void getAllLinesAndSplitToArrays() throws IOException
	{
		all_lines_v_vt_vn_f.add(new ArrayList<String>());
		all_lines_v_vt_vn_f.add(new ArrayList<String>());
		all_lines_v_vt_vn_f.add(new ArrayList<String>());
		all_lines_v_vt_vn_f.add(new ArrayList<String>());				
		
		String line = null;
		while((line = buffered_reader.readLine()) != null)
		{
			if(line.startsWith("v "))
				all_lines_v_vt_vn_f.get(0).add(line);
			
			if(line.startsWith("vt "))
				all_lines_v_vt_vn_f.get(1).add(line);
			
			if(line.startsWith("vn "))
				all_lines_v_vt_vn_f.get(2).add(line);
			
			if(line.startsWith("f "))
				all_lines_v_vt_vn_f.get(3).add(line);
		}
	}
	
	private static void interpretAndGroupLines()
	{
		sorted_lines_v_vt_vn.add(new ArrayList<String>());
		sorted_lines_v_vt_vn.add(new ArrayList<String>());
		sorted_lines_v_vt_vn.add(new ArrayList<String>());
		
		for(int i=0; i<all_lines_v_vt_vn_f.get(3).size(); i++)
		{
			String f_line = all_lines_v_vt_vn_f.get(3).get(i);

			String[] split_line = f_line.split("\\s+");
			
			for(int j=1; j<split_line.length; j++)
			{
				String[] split_by_slash = split_line[j].split("/");
				
				for(int k=0; k<split_by_slash.length; k++)
				{
					int f_index = Integer.parseInt(split_by_slash[k]);
					
					if(k == 0)
						sorted_lines_v_vt_vn.get(0).add(all_lines_v_vt_vn_f.get(0).get(f_index-1));
					if(k == 1)
						sorted_lines_v_vt_vn.get(1).add(all_lines_v_vt_vn_f.get(1).get(f_index-1));
					if(k == 2)
						sorted_lines_v_vt_vn.get(2).add(all_lines_v_vt_vn_f.get(2).get(f_index-1));
				}
			}				
		}
	}

	private static void writeOBJDataAsBinary(ArrayList<ArrayList<String>> lines_from_file, DataOutputStream out_datastream)
	{
		//Write number of vertex positions as the first float
		int number_of_vertices = (int)lines_from_file.get(0).size();
		System.out.println("Writing .obj as binary #Vertices: "+number_of_vertices);
		try {
			out_datastream.writeFloat((float)number_of_vertices);	
		} catch (IOException e) {
			System.out.println("Error writing header float with number of vertices!");
			e.printStackTrace();
		}	
		//Vertex Attributes	
		writeVerticesAttributes(out_datastream, sorted_lines_v_vt_vn);
	}

	private static void writeVerticesAttributes(DataOutputStream out_datastream, ArrayList<ArrayList<String>> lines_from_file)
	{
		writeVertexPositions(out_datastream, lines_from_file.get(0));			
		writeVertexTextureCoordinates(out_datastream, lines_from_file.get(1));
		writeVertexNormalsCoordinates(out_datastream, lines_from_file.get(2));	
	}
	
	
	private static void writeVertexPositions(DataOutputStream out_datastream, ArrayList<String> lines_from_file)
	{
		System.out.println("Writing VertexPositions (3xfloat) ...");
		for(int i=0; i<lines_from_file.size(); i++)
		{
			String current_line = lines_from_file.get(i);
			writeFloatValues(out_datastream, current_line);
		}
	}

	private static void writeVertexTextureCoordinates(DataOutputStream out_datastream, ArrayList<String> lines_from_file)
	{
		System.out.println("Writing TextureCoordinates (2xfloat) ...");
		for(int i=0; i<lines_from_file.size(); i++)
		{
			String current_line = lines_from_file.get(i);
			writeFloatValues(out_datastream, current_line);
		}
	}
	
	private static void writeVertexNormalsCoordinates(DataOutputStream out_datastream, ArrayList<String> lines_from_file)
	{
		System.out.println("Writing VertexNormals (3xfloat) ...");
		for(int i=0; i<lines_from_file.size(); i++)
		{
			String current_line = lines_from_file.get(i);
			writeFloatValues(out_datastream, current_line);
		}
	}
	
	private static void writeFloatValues(DataOutputStream out_datastream, String current_line)
	{
		String[] split_line = current_line.split("\\s+");
		try {
			for(int j=1; j<split_line.length; j++)
			{
				String parseable_line = String.format("%sf, ", split_line[j]).replace(',', ' ').trim();
				float float_attribute = Float.parseFloat(parseable_line);
				out_datastream.writeFloat(float_attribute);
			}
		} catch (IOException e) {
			System.out.println("Error writing float vertex attribute!");
			e.printStackTrace();
		}		
	}
	
}

