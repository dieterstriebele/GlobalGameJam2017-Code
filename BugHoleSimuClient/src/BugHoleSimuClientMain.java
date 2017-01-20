//http://stackoverflow.com/questions/8780667/socket-setperformancepreferences

public class BugHoleSimuClientMain {
	
	private static SimuClientConnect simuClient;
	
	public static void main(String[] args)
	{
		simuClient = new SimuClientConnect();
		simuClient.Connect();		
		simuClient.StartCommandThread();
		simuClient.StartGeometryThread();
	}
}
