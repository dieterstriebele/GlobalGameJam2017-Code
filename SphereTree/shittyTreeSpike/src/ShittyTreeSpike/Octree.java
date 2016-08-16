package ShittyTreeSpike;

public class Octree
{
	int maxDepth;
	OctreeNode root;
	
	public Octree(int iMaxDepth)
	{
		maxDepth = iMaxDepth;
	}
	
	public void grow(Vector3d center, Vector3d halfDim)
	{
		root = new OctreeNode(center, halfDim, maxDepth, 0);
	}
	
	public int getNumNodes()
	{
		return getNumNodes(root);
	}
	
	public int getNumNodes(OctreeNode iNode)
	{
		int currentNum = 1;
		
		if(iNode.hasChildren())
		{
			for(int i=0; i<8; i++)
			{
				if(iNode.getChild(i) != null)
				{
					currentNum += getNumNodes(iNode.getChild(i));
				}			
			}
		}
		
		return currentNum;
	}
}
