package com.irm.parselog.parselog.vo;

public class PartFile {
	
	    private String partFileName;
	    private String firstRow;
	    private String endRow;
	    private boolean firstIsFull;
	  
	    public String getPartFileName() {
	      return partFileName;
	    }
	  
	    public void setPartFileName(String partFileName) {
	      this.partFileName = partFileName;
	    }
	  
	    public String getFirstRow() {
	      return firstRow;
	    }
	  
	    public void setFirstRow(String firstRow) {
	      this.firstRow = firstRow;
	    }
	  
	    public String getEndRow() {
	      return endRow;
	    }
	  
	    public void setEndRow(String endRow) {
	      this.endRow = endRow;
	    }
	  
	    public boolean getFirstIsFull() {
	      return firstIsFull;
	    }
	  
	    public void setFirstIsFull(boolean firstIsFull) {
	      this.firstIsFull = firstIsFull;
	    }
}
