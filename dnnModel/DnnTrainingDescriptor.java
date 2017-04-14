package dnnUtil.dnnModel;

public class DnnTrainingDescriptor {
	
	public DnnTrainingDescriptor(int beginning, int end){
		setBeginning(beginning);
		setEnd(end);
	}
	public int getBeginning() {
		return mBeginning;
	}
	public void setBeginning(int mBeginning) {
		this.mBeginning = mBeginning;
	}
	public int getEnd() {
		return mEnd;
	}
	public void setEnd(int mEnd) {
		this.mEnd = mEnd;
	}
	private int mBeginning;
	private int mEnd;

}
