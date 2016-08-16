package ShittyTreeSpike;

public class SimpleTree
{
	public static void main(String[] args)
	{
		int numMaxLevels = 3;
		
		System.out.println(String.format("Constructing tree with %d levels", numMaxLevels));		
		
		TreePointCloud somePoints = new TreePointCloud();
		somePoints.points.add(new TreePoint(new Vector3d(10.0, 25.0, 33.0)));
		somePoints.points.add(new TreePoint(new Vector3d(22.0, 38.0, 99.1)));
		somePoints.points.add(new TreePoint(new Vector3d(77.2, 88.3, 42.0)));
		somePoints.points.add(new TreePoint(new Vector3d(1.23, 45.2, 18.6)));
		somePoints.points.add(new TreePoint(new Vector3d(16.6, 69.9, 66.6)));
		somePoints.points.add(new TreePoint(new Vector3d(205.0, 1.5, 69.69)));
		somePoints.points.add(new TreePoint(new Vector3d(98.23, 23.98, 82.93)));
		
		BoundingBox myBox = CalcBoundsForPointCloud(somePoints);
		
		System.out.println(String.format("Bounds: x-min: %.2f, x-max: %.2f, y-min: %.2f, y-max: %.2f, z-min: %.2f, z-max: %.2f",
				myBox.x.data[0], myBox.x.data[1], myBox.y.data[0], myBox.y.data[1], myBox.z.data[0], myBox.z.data[1]));
		
		Vector3d center = new Vector3d();		
		center.data[0] = (myBox.x.data[0] + myBox.x.data[1])/2.0;
		center.data[1] = (myBox.y.data[0] + myBox.y.data[1])/2.0;
		center.data[2] = (myBox.z.data[0] + myBox.z.data[1])/2.0;
		
		Vector3d halfDim = new Vector3d();
		halfDim.data[0] = Math.abs(myBox.x.data[1] - myBox.x.data[0]) / 2.0;
		halfDim.data[1] = Math.abs(myBox.y.data[1] - myBox.y.data[0]) / 2.0;
		halfDim.data[2] = Math.abs(myBox.z.data[1] - myBox.z.data[0]) / 2.0;
		
		System.out.println(String.format("Center: %.2f/%.2f/%.2f, HalfDim: %.2f/%.2f/%.2f",
						   center.data[0], center.data[1], center.data[2],
						   halfDim.data[0], halfDim.data[1], halfDim.data[2]));
		
		//grow tree
		//TODO: pass data points and do something with them
		Octree tree = new Octree(numMaxLevels);
		tree.grow(center, halfDim);
		
		System.out.println(String.format("Constructed tree with %d nodes", tree.getNumNodes()));
	}
	
	public static BoundingBox CalcBoundsForPointCloud(TreePointCloud iPoints)
	{
		BoundingBox bb = new BoundingBox();
		
		double x_min = Double.MAX_VALUE;
		double x_max = Double.MIN_VALUE;
		double y_min = Double.MAX_VALUE;
		double y_max = Double.MIN_VALUE;
		double z_min = Double.MAX_VALUE;
		double z_max = Double.MIN_VALUE;
		
		for(int i=0; i<iPoints.points.size(); i++)
		{
			//update min values
			if(iPoints.points.get(i).pointData.data[0] < x_min)
				x_min = iPoints.points.get(i).pointData.data[0];
			if(iPoints.points.get(i).pointData.data[1] < y_min)
				y_min = iPoints.points.get(i).pointData.data[1];
			if(iPoints.points.get(i).pointData.data[2] < z_min)
				z_min = iPoints.points.get(i).pointData.data[2];
			
			//update max values
			if(iPoints.points.get(i).pointData.data[0] > x_max)
				x_max = iPoints.points.get(i).pointData.data[0];
			if(iPoints.points.get(i).pointData.data[1] > y_max)
				y_max = iPoints.points.get(i).pointData.data[1];
			if(iPoints.points.get(i).pointData.data[2] > z_max)
				z_max = iPoints.points.get(i).pointData.data[2];			
		}
		
		bb.x.data[0] = x_min;
		bb.x.data[1] = x_max;
		bb.y.data[0] = y_min;
		bb.y.data[1] = y_max;
		bb.z.data[0] = z_min;
		bb.z.data[1] = z_max;		
		
		return bb;
	}
}
