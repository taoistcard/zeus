package zeus.live.data;

public class MutedUser {
	private long tempEndTime;
	
	private MutedTypeEnum mutedType;

	public MutedUser(){
		mutedType = MutedTypeEnum.ALWAYS;
	}
	
	public MutedUser(long tempEndTime){
		this.tempEndTime = tempEndTime;
		mutedType = MutedTypeEnum.TEMP;
	}
	
	public long getTempEndTime() {
		return tempEndTime;
	}



	public void setTempEndTime(long tempEndTime) {
		this.tempEndTime = tempEndTime;
	}



	public MutedTypeEnum getMutedType() {
		return mutedType;
	}



	public void setMutedType(MutedTypeEnum mutedType) {
		this.mutedType = mutedType;
	}



	public enum MutedTypeEnum
	{
		ALWAYS,TEMP
	}
	
	
}
