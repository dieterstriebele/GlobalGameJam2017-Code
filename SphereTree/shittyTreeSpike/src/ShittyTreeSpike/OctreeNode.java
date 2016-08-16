package ShittyTreeSpike;

public class OctreeNode
{	
	enum NodeType
	{
		Empty,
		Branch,
		Leaf
	}

	NodeType nodeType; //0 = empty node, 1 = branch, 2 = leaf
	int	   nodeIndex;
	int	   nodeLevel;
	int    maxDepth;
	
	Vector3d   center;
	Vector3d   halfDimension;
	
	boolean hasChildren;
	
	OctreeNode[] children;
	TreePoint data;

	public OctreeNode(Vector3d iCenter, Vector3d iHalfDimension, int iMaxDepth, int iLevel)
	{
		nodeLevel = iLevel;
		center = iCenter;
		halfDimension = iHalfDimension;
		maxDepth = iMaxDepth;
		hasChildren = false;
		nodeType = NodeType.Empty;
		data = null;
		
		children = new OctreeNode[8];
		
		for(int n=0; n<children.length; n++)
		{
			children[n] = null;
		}
		
		populate();
	}	
	
	public void populate()
	{
		// TODO: we need another break condition to decide whether we need to create more children or not
		if(this.nodeLevel < maxDepth && hasData())
		{
			hasChildren = true;
			children = new OctreeNode[8];
			
			for(int n=0; n<children.length; n++)
			{
				children[n] = new OctreeNode(new Vector3d(), new Vector3d(), maxDepth, this.nodeLevel+1);
			}
			
			// since this node has children, it cannot be a leaf and must be a branch
			this.nodeType = NodeType.Branch;
		}
		else if(this.nodeLevel == maxDepth && hasData())
		{
			// this node does not have children but it has data, so it must be a leaf
			this.nodeType = NodeType.Leaf;
		}
		else
		{
			// this node has neither children nor data so it is empty
			this.nodeType = NodeType.Empty;		
		}
	}
	
	public boolean hasData()
	{
		return true;
	}
	
	public boolean hasChildren()
	{
		return this.hasChildren;
	}
	
	public OctreeNode getChild(int index)
	{
		return children[index];
	}
}