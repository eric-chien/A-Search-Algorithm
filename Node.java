
/**
 * A node is considered to be every space on the 15*15 grid, this class holds information relating to these nodes
 * Given by Professor Jugan, with slight changes by Eric Chien
 */
public class Node implements Comparable<Node>{
    
    private int row, col, f, g, h, type;
    private Node parent;
   
    public Node(int r, int c, int t){
        row = r;
        col = c;
        type = t;
        parent = null;
        //type 0 is traverseable, 1 is not, 2 is the start, 3 is the goal, and 4 is the path
    }
    
    //mutator methods to set values
    public void setF(){
        f = g + h;
    }
    public void setG(int value){
        g = value;
    }
    public void setH(int value){
        h = value;
    }
    public void setParent(Node n){
        parent = n;
    }
    public void setType(int t){
        type = t;
    }
    
    //accessor methods to get values
    public int getF(){
        return f;
    }
    public int getG(){
        return g;
    }
    public int getH(){
        return h;
    }
    public Node getParent(){
        return parent;
    }
    public int getRow(){
        return row;
    }
    public int getCol(){
        return col;
    }
    public int getType(){
        return type;
    }
    public int compareTo(Node other){
        int difference = ((Node)other).getF();
        return this.getF()-difference;
    }
    public boolean equals(Object in){
        //typecast to Node
        Node n = (Node) in;
        
        return row == n.getRow() && col == n.getCol();
    }
    public String toString(){
        return "Node: " + row + "_" + col;
    }
    
}
