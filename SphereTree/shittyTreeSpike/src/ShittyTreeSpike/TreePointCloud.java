package ShittyTreeSpike;

import java.util.Vector;

public class TreePointCloud
{
	Vector<TreePoint> points;
	
	public TreePointCloud()
	{
		points = new Vector<TreePoint>();
	}
	
	public int getSize()
	{
		return points.size();
	}	
}
