package geometryInfo;

import game.Settings;

/**
 * Created by adieling on 21.01.17.
 * jajaaja, blub blub blub
 */
public class Enemy extends GeometryInformationBase {

    public Enemy(long timeBase) {
        super(timeBase);
        
        
    }

    public int GetObjectModelIdentification(int inObjectIndex) {
    	if(inObjectIndex < _positions.size())
    	{
    		return IGeometryInformation.cOBJECTMODELIDENTIFICATION_BRAINMINE;
    	}
        return 0;
    }

    public void SynchronizeState(long currentTime) {
    	

        super.SynchronizeState(currentTime);
    }
}
