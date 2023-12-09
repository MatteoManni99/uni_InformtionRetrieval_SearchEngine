package it.unipi.mircv.Index;

public class PostingListBlock extends PostingList
{
    private int currentPosition;
    private int length;
    private int maxDocID;
    private int minDocID;
    public void setFields(int length){
        currentPosition = 0;
        this.length = length;
        maxDocID = getDocId(length - 1);
    }

    public void setDummyFields() {
        maxDocID = -1;
        minDocID = -1;
    }
    public int getPosition(){
        return this.currentPosition;
    }

    public void setPosition(int position) { currentPosition = position;}
    public void setPositionMinusOne(){
        currentPosition = currentPosition - 1;
    }

    public int next(){
        if(currentPosition < length - 1) currentPosition += 1;
        else return -1;
        return currentPosition;
    }

    public int getMaxDocID(){
        return maxDocID;
    }
    public int getMinDocID() { return minDocID; }

    public int getDocId(int position){
        if(position >= 0)
            return getPostingList().get(position).getDocId();
        else
            return -1;
    }
    public int getTf(int position){
        if(position >= 0)
            return getPostingList().get(position).getTermFreq();
        else
            return -1;
    }
    public int getCurrentDocId(){
        return getDocId(currentPosition);
    }
    public int getCurrentTf(){
        return getTf(currentPosition);
    }
}