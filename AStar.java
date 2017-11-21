import java.util.*;
import java.io.*;
/**
 * Driver class to run A* algorithm to find path between two nodes
 * 
 * @author Eric Chien
 * @version 3/23/2017
 */
public class AStar
{
    //Global variables of boardsize, and the starting and ending node initialized
    public static int BSIZE = 15;
    public static Node startNode;
    public static Node endNode;
    
    public static void main(String [] args){
        //Generate a 15*15 tile-based environment and show it to the user
        System.out.println("Randomly Generated Environment:");
        System.out.println("NOTE: 0s are pathable, 1s are not.");
        Node [][] board = newBoard();
        showBoard(board);
        
        //Ask user to select a starting and ending node
        System.out.println();
        System.out.println("NOTE: The top left of the enviroment is (0,0)");
        System.out.println();
        setStart(board);
        setEnd(board);
        
        //Show the board with the starting and ending nodes
        System.out.println();
        System.out.println("Your Board: ");
        System.out.println("Note: S = Starting Node, E = Ending Node, 0s are pathable, 1s are not.");
        showBoard(board);
        
        //Begin A* searching
        searchP(board);
        
        //Show results of A* searching
        System.out.println("A* Path Results:");
        showBoard(board);
    }
    
    //SearchP method which performs parts of the A* algorithm
    public static Node[][] searchP(Node[][]  board){
        //Initialize needed items such as the open and closed list, etc.
        ArrayList<Node> openList = new ArrayList<Node>();
        ArrayList<Node> neighbors = new ArrayList<Node>();
        int [][] closedList = new int [BSIZE][BSIZE];
        Node currentNode;
        boolean isEnd = false;
        
        //Set the starting node, add it to the open list, and set the ending node
        for(int r = 0; r < BSIZE; r++){
            for(int c = 0; c < BSIZE; c++){
                if(board[r][c].getType() == 2){
                    startNode = board[r][c];
                    openList.add( board[r][c] );
                }
                else if(board[r][c].getType() == 3){
                    endNode = board[r][c];
                }
            }
        }
        //Set the currentNode as the starting node
        currentNode = openList.get(0);
        
        //While loop that performs primary functions of A* Algorithm
        while(isEnd == false){
            //First check if the current node is the ending node
            if(currentNode.getType() == 3){
                isEnd = true;
                continue;
            }
            //Generate neighbor nodes of the current node
            neighbors.clear();
            neighbors = generateNeighbors(board, currentNode, openList);
            //Add unique neighbors to the open list
            openList = addToOpenList(openList, neighbors, closedList);
            //Remove the current node from the open list
            for(int i = 0; i < openList.size(); i++){
                if(openList.get(i).getRow() == currentNode.getRow() && openList.get(i).getCol() == currentNode.getCol()){
                    openList.remove(i);
                }
            }
            //Add the current node to the closed list
            closedList[currentNode.getRow()][currentNode.getCol()] = 1;
            //If the open list is empty then there was no path, otherwise change the current node to the node with the lowest F cost
            if(openList.isEmpty() == true){
                System.out.println();
                System.out.println("A path could not be found from the starting to the ending node.");
                System.exit(0);
            }
            else{
                currentNode = openList.get(0);
            }
        }
        
        //Lists the path in coordinate and grid-board format
        generatePath(board, currentNode);
     
        return board;
    }
    
    //Generate path method which shows the path from starting to ending node
    public static Node[][] generatePath(Node[][] board, Node currentNode){
        //Keep track of the rows and cols of nodes on the path
        ArrayList<Integer> rows = new ArrayList<Integer>();
        ArrayList<Integer> cols = new ArrayList<Integer>();
        //Starting from the ending node, keep adding nodes to the path until you reach the starting node
        while(currentNode.getType() != 2){
            if(currentNode.getType() != 3){
                currentNode.setType(4);
            }
            rows.add(currentNode.getRow());
            cols.add(currentNode.getCol());
            currentNode = currentNode.getParent();
        }
        //Add starting node to the path
        rows.add(currentNode.getRow());
        cols.add(currentNode.getCol());
        
        //Display the coordinate format of path to the user
        System.out.println();
        System.out.println("List of nodes that are the path: ");
        System.out.println("NOTE: [row] [column] (Top left of board is [0][0])");
        for(int i = rows.size() - 1; i >= 0; i--){
            System.out.print("[" + rows.get(i) + ", " + cols.get(i) + "], ");
        }
        System.out.println();
        
        return board;
    }
    
    //This method adds only unique nodes to the open list
    public static ArrayList<Node> addToOpenList(ArrayList<Node> openList, ArrayList<Node> neighbors, int [][] closedList){
        int unique;
        for(int i = 0; i < neighbors.size(); i++){
            unique = 1;
            for(int j = 0; j < openList.size(); j++){
                //For each neighbor found, only add it to the open list if it is not already in the open list
                if(neighbors.get(i).getRow() == openList.get(j).getRow() && neighbors.get(i).getCol() == openList.get(j).getCol()){
                    unique = 0;
                    //If a neighbor is in the open list, replace it if the F cost is lower
                    if(neighbors.get(i).getF() < openList.get(j).getF()){
                        openList.remove(j);
                        openList.add(neighbors.get(i));
                    }
                }
            }
            if(closedList[neighbors.get(i).getRow()][neighbors.get(i).getCol()] == 1){
                unique = 0;
            }
            if(unique == 1){
                openList.add(neighbors.get(i));
            }
        }
        
        //Sort the open list, ordered by F cost in ascending order
        Collections.sort(openList, new Comparator<Node>()
        {
             public int compare(Node n1, Node n2){
                return n1.getF() - n2.getF();
             }
        }
        );
        return openList;
    }
    
    //This method generates the neighbors of a node by searching 1 node in 8 cardinal directions
    public static ArrayList<Node> generateNeighbors(Node[][] board, Node currentNode, ArrayList<Node> openList){
        ArrayList<Node> neighbors = new ArrayList<Node>();
        int currentR = currentNode.getRow();
        int currentC = currentNode.getCol();
        int gCost;
        int hVal;
        
        //Begin loop for adding neighbors
        for(int r = currentR - 1; r < currentR + 2; r++){
            for(int c = currentC - 1; c < currentC + 2; c++){
                //Skip this neighbor if it is out of bounds
                if(r < 0 || r > 14 || c < 0 || c > 14){
                    continue;
                }
                //Add neighbors to the list if its a pathable node
                if(board[r][c].getType() == 0 || board[r][c].getType() == 3){
                    gCost = findG(r, c, currentNode);
                    board[r][c].setG(gCost);
                    hVal = findH(r, c);
                    board[r][c].setH(hVal);
                    board[r][c].setF();
                    //Only change the parent if it doesn't already have one
                    if(board[r][c].getParent() == null){
                        board[r][c].setParent(currentNode);
                    }
                    neighbors.add(board[r][c]);
                }
            }
        }
        
        return neighbors;
    }
    
    //Determines the distance from the starting node
    public static int findG(int r, int c, Node currentNode){
        int gCost;
        //If the node is in the same column or row, then its a straight movement
        //If not, then it is a diagonal movement
        if(currentNode.getRow() == r || currentNode.getCol() == c){
            gCost = currentNode.getG() + 10;
        }
        else{
            gCost = currentNode.getG() + 14;
        }
        return gCost;
    }
    
    //Determines the heuristic using Manhattan method
    public static int findH (int r, int c){
        int hVal;
        int rows;
        int cols;
        
        //Since there is no diagonal movement for heuristic, row distance is simply the row of one node minus the row of the other node
        //Likewise for the column distance
        rows = Math.abs(endNode.getRow() - r);
        cols = Math.abs(endNode.getCol() - c);
        
        hVal = rows+cols;
        return hVal*10;
    }
    
    //This method sets the start node and doesn't allow for array out of bounds errors or setting the start node on an unpathable node
    public static Node[][] setStart(Node[][] board){
        Scanner user = new Scanner(System.in);
        int rStart = -1;
        int cStart = -1;
        int exitR = 0;
        int exitC = 0;
        int allowed = 0;
        while( allowed == 0 ){
            while( exitR == 0 ){
                System.out.println("Enter the row of the starting node(0-14): ");
                rStart = user.nextInt();
                //If row goes out of bounds just ask again
                if(rStart >= 0 && rStart < 15){
                    exitR = 1;
                }
            }
            while( exitC == 0 ){
                System.out.println("Enter the column of the starting node(0-14): ");
                cStart = user.nextInt();
                //If column goes out of bounds just ask again
                if(cStart >= 0 && cStart < 15){
                    exitC = 1;
                }
            }
            //Do not allow to set the start path as an unpathable node
            if(board[rStart][cStart].getType() == 1){
                System.out.println("You cannot select an unpathable node as a starting node. Please try again");
                exitR = 0;
                exitC = 0;
            }
            else{
                allowed = 1;
            }
        }
        Node start = new Node(rStart, cStart, 2);
        start.setG(0);
        board[rStart][cStart] = start;
        return board;
    }
    
    //Set end node method which is similar to the set start node method
    public static Node[][] setEnd(Node[][] board){
        Scanner user = new Scanner(System.in);
        int rEnd = -1;
        int cEnd = -1;
        int exitR = 0;
        int exitC = 0;
        int allowed = 0;
        int hVal;
        while( allowed == 0 ){
            while( exitR == 0){
                System.out.println("Enter the row of the ending node(0-14): ");
                rEnd = user.nextInt();
                //If row goes out of bounds just ask again
                if(rEnd >= 0 && rEnd < 15){
                    exitR = 1;
                }
            }
            while( exitC == 0 ){
                System.out.println("Enter the column of the ending node(0-14): ");
                cEnd = user.nextInt();
                //If column goes out of bounds just ask again
                if(cEnd >= 0 && cEnd < 15){
                    exitC = 1;
                }
            }
            //Don't allow unpathable node or starting node to be set as ending node
            if(board[rEnd][cEnd].getType() == 1 || board[rEnd][cEnd].getType() == 2){
                System.out.println();
                System.out.println("You cannot select an unpathable node or the starting node as an ending node. Please try again");
                exitR = 0;
                exitC = 0;
            }
            else{
                allowed = 1;
            }
        }
        Node end = new Node(rEnd, cEnd, 3);
        end.setH(0);
        board[rEnd][cEnd] = end;
        return board;
    }
    
    //This method creates a new 15*15 board
    public static Node[][] newBoard(){
        Node [][]board = new Node [BSIZE][BSIZE];
        Random rng = new Random();
        int bPath = 0;
        int t;
        
        //Each node on the board has a 10* chance of being unpathable individually
        for(int r = 0; r < BSIZE; r++){
            for(int c = 0; c < BSIZE; c++){
                t = 0;
                bPath = rng.nextInt(10);
                if(bPath == 0){
                    t = 1;
                }
                Node tile = new Node(r, c, t);
                board[r][c] = tile;
            }
        }
        return board;
    }
    
    //Basic method to display contents of an array list (for testing purposes)
    public static void showList(ArrayList<Node> list){
        for(int i = 0; i < list.size(); i++){
            System.out.println("(" + list.get(i).getRow() + ", " + list.get(i).getCol() + ") ");
        }
    }
    
    //Basic method to display the board in a grid-style format for users
    public static void showBoard(Node[][] board){
        for(int r = 0; r <BSIZE ; r++){
            for(int c = 0; c < BSIZE; c++){
                //Display the board with symbols that represent the pathable, unpathable, starting, ending, and path taken nodes
                if(board[r][c].getType() == 0 || board[r][c].getType() == 1){
                    System.out.print(board[r][c].getType() + ", ");
                }
                else if(board[r][c].getType() == 2){
                    System.out.print("S, ");
                }
                else if(board[r][c].getType() == 3){
                    System.out.print("E, ");
                }
                else{
                    System.out.print("P, ");
                }
            }
            System.out.println();
        }
    }
}
