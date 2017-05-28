/**
 *
 * @author Niveditha Karmegam | 2015125
 */

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Scanner;

public class ShortestPathFinding {
    
    //public static final int DIAGONAL_COST = 14;
    //public static final int V_H_COST = 10;

    // draw the N-by-N boolean matrix to standard draw
    public static void show(boolean[][] a, boolean which) {
        int N = a.length;
        StdDraw.setXscale(-1, N);;
        StdDraw.setYscale(-1, N);
        StdDraw.setPenColor(StdDraw.BLACK);
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                if (a[i][j] == which)
                	StdDraw.square(j, N-i-1, .5);
                else StdDraw.filledSquare(j, N-i-1, .5);
    }

    //remove only the colored path
    public static void removePath(boolean[][] a) {
        int N = a.length;
        StdDraw.setXscale(-1, N);;
        StdDraw.setYscale(-1, N);
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                if (a[i][j] == true){ //if it percolates
                    StdDraw.setPenColor(StdDraw.WHITE);
                    StdDraw.filledSquare(j, N-i-1, .5);
                    StdDraw.setPenColor(StdDraw.BLACK);
                    StdDraw.square(j, N-i-1, .5);
                }
        showCell(a); 
    }
    
    //method to draw the starting and end points in the matrix
    public static void showCell(boolean [][]a){
        for(int i =0; i<a.length; i++){
            for(int j = 0; j< a.length; j++){
                if ((i == startI && j == startJ) || (i == endI && j == endJ) ) {
                                StdDraw.setPenColor(StdDraw.RED);
                		StdDraw.filledCircle(j, a.length-i-1, .5);
                                
                	}
            }
        }
    }
    
    //to show the path of the route taking
    public static void showPath (Cell [][] a, int x1, int y1){
         int N = a.length;
        StdDraw.setXscale(-1, N);;
        StdDraw.setYscale(-1, N);
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                if (a[i][j] != null)
                	if ((i == x1 && j == y1)) {
                            
                            StdDraw.setPenColor(StdDraw.CYAN); 
                            StdDraw.filledSquare(j, N-i-1, .5);//to show the path
                            StdDraw.setPenColor(StdDraw.BLACK);
                            StdDraw.square(j, N-i-1, .5); //to print the black border
                            if (i==endI &&j == endJ){
                                StdDraw.setPenColor(StdDraw.RED); //to print the end red circle
                                StdDraw.filledCircle(j, N-i-1, .5);
                            }
                	}
        
       StdDraw.setPenColor(StdDraw.CYAN);
       StdDraw.filledSquare(startJ, N-startI-1, .5);//green filled square for start cell
       StdDraw.setPenColor(StdDraw.RED);
       StdDraw.filledCircle(startJ, N-startI-1, .5); //red circle for start cell
       StdDraw.setPenColor(StdDraw.BLACK); 
       StdDraw.square(startJ, N-startI-1, .5);//border for start cell
       
    }
    
    // return a random N-by-N boolean matrix, where each entry is
    // true with probability p
    public static boolean[][] random(int N, double p) {
        boolean [][] a = new boolean[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                a[i][j] = StdRandom.bernoulli(p);
        return a;
    }
    
    //innerclass to represent each node
    public static class Cell{
        int heuristicCost = 0; //heuristic cost
        int finalCost = 0; //final cost
        int i,j; //column and row index
        Cell parent;
        
        
        //constructor initializing the cell row and column
        Cell(int i, int j){
            this.i = i;
            this.j = j;
        }
        
        //this method is used to print the path it takes
        @Override
        public String toString(){
            return "["+this.i+", "+this.j+"]";
        }
    }
    
    //grid matrix would store each nodes index, parent node and h/f costs as well
    //Blocked cells are just null Cell values in grid
    static Cell [][] grid ;
    public static void setGrid(int x){
        grid = new Cell[x][x];
    }
    
    //a queue which stores all the cells which are considered
    static PriorityQueue<Cell> open;
    
    static boolean closed[][]; //closed matrix storing all the cells already considered or not walkable
    static int startI, startJ; //index of starting cell
    static int endI, endJ;     //index of ending cell
    static int totalCost; //variable to store the total cost for travelling
    //static double timer =0; //variable to store the time taken for travelling
    static Stopwatch timerFlow;
    
    public static void setBlocked(int i, int j){
        grid[i][j] = null;
    }
    
    public static void setStartCell(int i, int j){
        startI = i;
        startJ = j;
    }
    
    public static void setEndCell(int i, int j){
        endI = i;
        endJ = j; 
    }
    
    
    static void checkAndUpdateCost(Cell current, Cell t, int cost){
        if(t == null || closed[t.i][t.j])return; //if cell is blocked or already on the closed list
        int t_final_cost = t.heuristicCost+cost; //calculate final cost (f = h+g)
        
        boolean inOpen = open.contains(t);  //check if node is already available in open priority queue
        if(!inOpen || t_final_cost<t.finalCost){ //if t node is not in priority queue or the calculated cost is smaller than the existing cost
            t.finalCost = t_final_cost;  //update cost
            t.parent = current;  //set the parent of this node as current node
            if(!inOpen)open.add(t); //add it to open queue if not their already
        }
    }
    
    public static void AStar(int type){ 
        
        //add the start location to open list.
        open.add(grid[startI][startJ]);
        
        Cell current;
        
        while(true){ 
            current = open.poll(); //returns and removes the head of the queue, null if its empty
            if(current==null)
                break;
            
            closed[current.i][current.j]=true; //add it to close matrix to show that the cell has already been considered

            if(current.equals(grid[endI][endJ])){ //check if current node is same as the destination node
                return;  //control is passed back to the calling method
            } 

            Cell t;  
            
            /*Move only vertical and horizontal for Manhattan*/
            /*Moves in all 8 directions for Euclidean and Chebyshev*/
            
                 //condtition to check check all three cells on TOP of the current node
                if(current.i-1>=0){  
                    t = grid[current.i-1][current.j]; //get the cell on top of current cell
                    checkAndUpdateCost(current, t, current.finalCost+1);

                    if(current.j-1>=0 && type != 1){                
                        t = grid[current.i-1][current.j-1]; //left top diagonal one
                        checkAndUpdateCost(current, t, current.finalCost+1); 
                    }

                    if(current.j+1<grid[0].length && type != 1){ //check if j+i is smaller than the matrix columns length
                        t = grid[current.i-1][current.j+1]; //top left diagonal cell
                        checkAndUpdateCost(current, t, current.finalCost+1); 
                    }
                } 

                if(current.j-1>=0){  //condition to check the column index is valid  
                    t = grid[current.i][current.j-1]; //get the adjacent LEFT node
                    checkAndUpdateCost(current, t, current.finalCost+1); 
                }

                if(current.j+1<grid[0].length){
                    t = grid[current.i][current.j+1]; //get the RIGHT adjacent node
                    checkAndUpdateCost(current, t, current.finalCost+1); 
                }

                //condtition to check check all three cells on BOTTOM of the current node
                if(current.i+1<grid.length){ 
                    t = grid[current.i+1][current.j]; //get bottom adjacent node
                    checkAndUpdateCost(current, t, current.finalCost+1); 

                    if(current.j-1>=0  && type != 1){
                        t = grid[current.i+1][current.j-1]; //bottom-left diagonal node
                        checkAndUpdateCost(current, t, current.finalCost+1); 
                    }

                    if(current.j+1<grid[0].length  && type != 1){
                       t = grid[current.i+1][current.j+1]; //get bottom-right diagonal 
                        checkAndUpdateCost(current, t, current.finalCost+1); 
                    }
                }
        } 
    }
    
    /*
    Params :
    x, y = Board's dimension
    si, sj = start location's x and y coordinates
    ei, ej = end location's x and y coordinates
    int[][] blocked = array containing inaccessible cell coordinates
    */
    public static void findPath(int x, int si, int sj, int ei, int ej, boolean[][] randomlyGenMatrix, int type){
           
            //Reset
           grid = new Cell[x][x];
           closed = new boolean[x][x];
           open = new PriorityQueue <>((Object o1, Object o2) -> {
               Cell c1 = (Cell)o1;
               Cell c2 = (Cell)o2;
               
               return c1.finalCost<c2.finalCost?-1:
                       c1.finalCost>c2.finalCost?1:0;
           });
           //Set start position
           //setStartCell(si, sj);  //Setting to 0,0 by default. Will be useful for the UI part
           
           //Set End Location
           //setEndCell(ei, ej);
           
           timerFlow = new Stopwatch();
           
           /* Initialize the index values of each cell in the grid
              calculate hCost of each cell in the grid */
           for(int i=0;i<x;++i){
              for(int j=0;j<x;++j){
                  grid[i][j] = new Cell(i, j); 
                  grid[i][j].heuristicCost = getHeuristic(type, i, j); // hCost = current to end node cost                                              
              }
           }
           grid[si][sj].finalCost = 0; //setting the start node final cost to zero
           
           /*
             Set blocked cells. Simply set the cell values to null
             for blocked cells.
           */
           for (int i =0; i<x; ++i){
               for(int j=0; j<x; ++j){
                   if(!randomlyGenMatrix[i][j]){
                       setBlocked(i,j);
                   }
               }
           }           
           
           AStar(type); //search algorithm
            
           if(closed[endI][endJ]){
               //Trace back the path 
                System.out.println("Path: ");
                Cell current = grid[endI][endJ];
                System.out.print(current);
                showPath (grid, current.i, current.j);
                while(current.parent!=null){
                    showPath (grid, current.i, current.j);
                    System.out.print(" -> "+current.parent);
                    current = current.parent; 
                    totalCost += grid[current.i][current.j].finalCost;
                } 
                
           }else System.out.println("No possible path");
    }
    
    public static int getHeuristic(int type, int i, int j){
        int hCost;
        if (type == 1){
            //Manhattan distance matrix
            hCost = Math.abs(i-endI) + Math.abs(j-endJ);
            
        }else if(type == 2){
            //Euclidean distance matrix
            double x = Math.pow(Math.abs(i-endI), 2.0);
            double y = Math.pow(Math.abs(j-endJ), 2.0);
            hCost = (int)Math.sqrt(x+y);
    
        }else {
            //Chebyshev distance matrix
            hCost = Math.max(Math.abs(i-endI), Math.abs(j-endJ));
        }
        return hCost;
    }

    public static void main(String[] args) {
    	
    	Scanner in = new Scanner(System.in);
        
        //enter grid size
        System.out.print("Enter grid size: ");
        int size = in.nextInt();
        
        // The following will generate a 10x10 squared grid with relatively few obstacles in it
    	// The lower the second parameter, the more obstacles (black cells) are generated
    	boolean [][] randomlyGenMatrix = random(size, 0.6);
        setGrid(size); //creates 2D array of type Cell
    	
    	StdArrayIO.print(randomlyGenMatrix);  //display the matrix in the console
    	show(randomlyGenMatrix, true);        //Display the GUI
    	
        // Reading the coordinates for points A and B on the input squared grid.
        System.out.print("Enter START node ROW(i) :  ");
        int Ai = in.nextInt();
        
        System.out.print("Enter START node COLUMN(j) : ");
        int Aj = in.nextInt();
        
        System.out.println();
        
        System.out.print("Enter END node ROW (i) : ");
        int Bi = in.nextInt();
        
        System.out.print("Enter END node COLUMN (j) : ");
        int Bj = in.nextInt();
        
        setStartCell(Ai, Aj); //to set the start and end cell values
        setEndCell(Bi, Bj);
        showCell(randomlyGenMatrix); //to display the points in the matrix
        
        //to run the code for all three distance matrix
        for (int type = 1; type<=3; type++){
            //reset timer and total cost each run
            totalCost = 0;
            if(type == 1){ //run it for manhattan matrix first
                System.out.println();
                System.out.println("MANHATTAN");
                System.out.println("----------");
            }else if(type == 2){
                System.out.println();
                System.out.println("EUCLIDEAN");
                System.out.println("----------");
            }else {
                System.out.println();
                System.out.println("CHEBYSHEV");
                System.out.println("----------");
            }
            in.nextLine();

            //method accepts random boolean matrix, size of the matrix, start and end cells and the type of distance matrix
            findPath(size, Ai, Aj, Bi, Bj, randomlyGenMatrix, type);
            System.out.println();
            System.out.println("Total cost is: "+ totalCost);
            StdOut.println("Elapsed time = " + timerFlow.elapsedTime());
            in.nextLine();
            removePath(randomlyGenMatrix); //clears the printed path and shows empty matrix for next run
        }
    }
}
