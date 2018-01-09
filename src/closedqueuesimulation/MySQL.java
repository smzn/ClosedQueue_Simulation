package closedqueuesimulation;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class MySQL {
	String driver;// JDBCドライバの登録
    String server, dbname, url, user, password;// データベースの指定
    Connection con;
    Statement stmt;
    ResultSet rs;
    private int simulation_id, node_index[];
    
	public MySQL(int[] node_index) {
		this.driver = "org.gjt.mm.mysql.Driver";
        this.server = "naisyo.sist.ac.jp";
        this.dbname = "naisyo";
        this.url = "jdbc:mysql://" + server + "/" + dbname + "?useUnicode=true&characterEncoding=UTF-8";
        this.user = "naisyo";
        this.password = "naisyo";
        this.node_index = node_index;
        try {
            this.con = DriverManager.getConnection(url, user, password);
            this.stmt = con.createStatement ();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            Class.forName (driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String sqlcount = "select COUNT(*) cnt FROM simulations";
        ResultSet rscount;
        try {
			rscount = stmt.executeQuery(sqlcount);
			rscount.next();
	        this.simulation_id = rscount.getInt("cnt") + 1;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}
    
	public void insertEventtime(ArrayList<Double> eventtime[]){
		System.out.println("Eventtime : Insert開始");
        try{
        		for(int i = 0; i < eventtime.length; i++){
        			StringBuffer buf = new StringBuffer();
        			buf.append("INSERT INTO eventtime_sims(simulation_id, facility_id, value) VALUES");
                		for(int j = 0; j < eventtime[i].size(); j++) {
                			if(j == eventtime[i].size() -1)
                				buf.append("("+simulation_id+","+node_index[i]+","+eventtime[i].get(j)+")");
                        else buf.append("("+simulation_id+","+node_index[i]+","+eventtime[i].get(j)+"),");
                		}
                String sql = buf.toString();
                stmt.execute (sql);
                System.out.println("Insert完了 : " + (i+1) +"/" +eventtime.length);
        		}
        }
        catch (SQLException e) {
                e.printStackTrace();
        }
	}
	
	public void insertTimequeue(ArrayList<Integer> timequeue[]){
		System.out.println("Timequeue : Insert開始");
        try{
        		for(int i = 0; i < timequeue.length; i++){
        			StringBuffer buf = new StringBuffer();
        			buf.append("INSERT INTO timequeue_sims(simulation_id, facility_id, value) VALUES");
                		for(int j = 0; j < timequeue[i].size(); j++) {
                			if(j == timequeue[i].size() -1)
                				buf.append("("+simulation_id+","+node_index[i]+","+timequeue[i].get(j)+")");
                        else buf.append("("+simulation_id+","+node_index[i]+","+timequeue[i].get(j)+"),");
                		}
                String sql = buf.toString();
                stmt.execute (sql);
                System.out.println("Insert完了 : " + (i+1) +"/" +timequeue.length);
        		}
        }
        catch (SQLException e) {
                e.printStackTrace();
        }
	}

	public void insertCorrelation(double correlation[][]) {
		System.out.println("Correlation : Insert開始");
		try{
    			for(int i = 0; i < correlation.length; i++){
    				StringBuffer buf = new StringBuffer();
    				buf.append("INSERT INTO correlation_sims(simulation_id, fromid, toid, value) VALUES");
            			for(int j = 0; j < correlation[i].length; j++) {
            				if(j == correlation[i].length -1)
            					buf.append("("+simulation_id+","+node_index[i]+","+node_index[j]+","+correlation[i][j]+")");
            				else buf.append("("+simulation_id+","+node_index[i]+","+node_index[j]+","+correlation[i][j]+"),");
            			}
            			String sql = buf.toString();
            			stmt.execute (sql);
            			System.out.println("Insert完了 : " + (i+1) +"/" +correlation.length);
    			}
		}
		catch (SQLException e) {
            	e.printStackTrace();
		}
	}
	
	public void insertQueue(double result[][]) {
		System.out.println("Queue : Insert開始");
		StringBuffer buf = new StringBuffer();
		buf.append("INSERT INTO queues(simulation_id, facility_id, l, q, u, var_u, max_l) VALUES");
		try{
    			for(int i = 0; i < result.length; i++){
            				if(i == result.length -1)
            					buf.append("("+simulation_id+","+node_index[i]+","+result[i][0]+","+result[i][1]+","+result[i][2]+","+result[i][3]+","+result[i][4]+")");
            				else buf.append("("+simulation_id+","+node_index[i]+","+result[i][0]+","+result[i][1]+","+result[i][2]+","+result[i][3]+","+result[i][4]+"),");
    			}
       		String sql = buf.toString();
    			stmt.execute (sql);
    			System.out.println("Queue Insert完了");

		}
		catch (SQLException e) {
            	e.printStackTrace();
		}
	}
	
	public void insertTimerate(double timerate[][]) {
		System.out.println("Timerate : Insert開始");
		try{
    			for(int i = 0; i < timerate.length; i++){
    				StringBuffer buf = new StringBuffer();
    				buf.append("INSERT INTO timerates(simulation_id, n, n_node, value) VALUES");
            			for(int j = 0; j < timerate[i].length; j++) {
            				if(j == timerate[i].length -1)
            					buf.append("("+simulation_id+","+i+","+j+","+timerate[i][j]+")");
            				else buf.append("("+simulation_id+","+i+","+j+","+timerate[i][j]+"),");
            			}
            			String sql = buf.toString();
            			stmt.execute (sql);
            			System.out.println("Insert完了 : " + (i+1) +"/" +timerate.length);
    			}
		}
		catch (SQLException e) {
            	e.printStackTrace();
		}
	}
	
	public void insertSimulation(int setting_id, int facility_num, int time, int way_id) {
		System.out.println("Simulation : Insert開始");
		StringBuffer buf = new StringBuffer();
		buf.append("INSERT INTO simulations(setting_id, facility_num, time, way_id) VALUES");
		try{
			buf.append("("+setting_id+","+facility_num+","+time+","+way_id+")");
       		String sql = buf.toString();
    			stmt.execute (sql);
    			System.out.println("Simulation Insert完了");
		}
		catch (SQLException e) {
            	e.printStackTrace();
		}
	}
}
