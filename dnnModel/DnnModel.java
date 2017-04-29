package dnnUtil.dnnModel;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


public class DnnModel implements Serializable {
	static final long serialVersionUID = 1L;

	static{
		System.loadLibrary("native-dnn-model");
	}

	private static enum W_Type {
		WEIGHT, BIAS
	}

	// DnnModel members ============================================================================
	private DnnModelDescriptor mModelDescriptor;
	private DnnTrainingData mTrainingData;
	private Map<Integer,Map<W_Type,float[]>> mWeightsData;
	private int mNumberOfTrainingObjects;

	// DnnModel constructors =======================================================================
	public DnnModel(DnnModelParameters modelParameters){
		mWeightsData = new HashMap<>();
		mNumberOfTrainingObjects = loadTrainingData();
		createModel(modelParameters);
	}

	public DnnModel(DnnModelDescriptor modelDescriptor){
		mWeightsData = new HashMap<>();
		mModelDescriptor = modelDescriptor;
		loadModel(mModelDescriptor);
	}

	// DnnModel Methods ============================================================================
	private void createModel(DnnModelParameters modelParameters){
		byte[] binaryData = jniCreateModel();
		mModelDescriptor = new DnnModelDescriptor(binaryData);

	}
	private void loadModel(DnnModelDescriptor modelDescriptor){
		jniLoadModel(mModelDescriptor.getBinaryData());
	}
	public void updateModel(DnnModelDelta modelDelta){
		byte[] binaryData = jniUpdateModel();
		mModelDescriptor.setBinaryData(binaryData);
	}
	public void trainModel(){
		byte[] binaryData = jniTrainModel();
		mModelDescriptor.setBinaryData(binaryData);
	}
	public int loadTrainingData(){
		return jniLoadTrainingData();
	}
	public DnnTrainingData getTrainingData(DnnTrainingDescriptor trainingDescriptor){
		jniGetTraingData(trainingDescriptor.getBeginning(), trainingDescriptor.getEnd());
		return mTrainingData;
	}
	public DnnModelDescriptor getModelDescriptor(){
		return mModelDescriptor;
	}

	// Java Native Interface callback methods ======================================================
	
	private void setLayerWeights(float[] weights, int layerIndex){
		if(!mWeightsData.containsKey(layerIndex)){
			mWeightsData.put(layerIndex,new HashMap<W_Type,float[]>());
		}
		mWeightsData.get(layerIndex).put(W_Type.WEIGHT,weights);
	}
	
	private void setLayerBiases(float[] biases, int layerIndex){
		if(!mWeightsData.containsKey(layerIndex)){
			mWeightsData.put(layerIndex,new HashMap<W_Type,float[]>());
		}
		mWeightsData.get(layerIndex).put(W_Type.BIAS,biases);
	}
	
	private float[] getLayerWeights(int layerIndex){
		if(!mWeightsData.containsKey(layerIndex)){
			// in case the layer doesn't exist will create it and return null
			mWeightsData.put(layerIndex,new HashMap<W_Type,float[]>());
			mWeightsData.get(layerIndex).put(W_Type.WEIGHT,null);
		}
		return mWeightsData.get(layerIndex).get(W_Type.WEIGHT);
	}
	
	private float[] getLayerBiases(int layerIndex){
		if(!mWeightsData.containsKey(layerIndex)){
			// in case the layer doesn't exist will create it and return null
			mWeightsData.put(layerIndex,new HashMap<W_Type,float[]>());
			mWeightsData.get(layerIndex).put(W_Type.BIAS,null);
		}
		return mWeightsData.get(layerIndex).get(W_Type.BIAS);
	}

	
	private void initTrainingData(int numOfLabels, int numOfData){
		mTrainingData = new DnnTrainingData(numOfLabels, numOfData);
	}
	
	private void setIndexTrainingData(int index, int label, float[] data){
		mTrainingData.setIndexLabelData(index, label);
		mTrainingData.setIndexData(index, data);
	}
	
	private int getTrainingDataSize(){
		return mTrainingData.getNumOfData();
	}
	
	private float[] getIndexTrainingData(int index){
		return mTrainingData.getIndexData(index);
	}
	
	private int getIndexTrainingLabelData(int index){
		return mTrainingData.getIndexLabelData(index);
	}



	// Java Native Interface methods ===============================================================
	private native byte[] jniCreateModel();
	private native byte[] jniUpdateModel();

	private native void jniLoadModel(byte[] binaryData);
	private native byte[] jniTrainModel();

	private native int jniLoadTrainingData();
	private native void jniGetTraingData(int startIndex, int endIndex);

	public Integer getNumberOfTrainingObjects() {
		return mNumberOfTrainingObjects;
	}
}
