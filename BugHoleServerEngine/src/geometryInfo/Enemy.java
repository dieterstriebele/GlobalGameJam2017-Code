package geometryInfo;

import game.Settings;

/**
 * Created by adieling on 21.01.17.
 */
public class Enemy extends GeometryInformationBase {


    private static final int NumberOfCameraSegments = 6;

    public float m_IntestineScrollingOffset = 0f;

    public Enemy(long timeBase) {
        super(timeBase);

    }

    public int GetObjectModelIdentification(int inObjectIndex) {

        return 0;
    }

    public void SynchronizeState(long currentTime) {

        super.SynchronizeState(currentTime);
    }


//    @Override
//    public boolean IsFinished(long currentTime) {
//        return false;
//    }
}
