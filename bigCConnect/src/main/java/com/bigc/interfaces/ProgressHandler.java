package com.bigc.interfaces;

public interface ProgressHandler {

	public void switchToProgressMode();
	public void updateProgress(int progress);
	public void updateLabelToProcessingMessage();
	public void onUploadComplete();
}
