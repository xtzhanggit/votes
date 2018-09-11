package server;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import net.sf.json.JSONObject;
import java.sql.*;

public class Worker{
    public static void main(String[] args){
        try{
            Jedis redis = connectToRedis("redis");
            Connection dbConn = connectToDB("db");
            System.err.println("Watching vote quene");
            while(true){
                String voteJSON = redis.blpop(0,"votes").get(1);
                JSONObject jsonObj = JSONObject.fromObject(voteJSON);
                String voterID = jsonObj.getString("voter_id");
                String vote = jsonObj.getString("vote");
                System.err.println("Processing vote for " + vote + " by " + voterID); 
                updateVote(dbConn,voterID,vote);
            }
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }
    
    private static Jedis connectToRedis(String host){
        Jedis conn = new Jedis(host);
        while(true){
            try{
                conn.keys("*");
                break;
            }catch(JedisConnectionException e){
                System.out.println("Waiting for redis");
                sleep(1000);
            }
        }
        System.out.println("Connected to redis");
        return conn;
    }
    
    private static void sleep(long duration){
        try{
            Thread.sleep(duration);
        }catch(InterruptedException e){
            System.exit(1);
        }
    }
    
    private static Connection connectToDB(String host){
        Connection conn = null;
        try{
            Class.forName("org.postgresql.Driver");
            String url = "jdbc:postgresql://" + host + "/postgres";
            while(conn == null){
                try{
                    conn = DriverManager.getConnection(url,"postgres","");
                }catch(SQLException e){
                    System.out.println("Waiting for db");
                    sleep(1000);
                }
            }
            PreparedStatement st = conn.prepareStatement("CREATE TABLE IF NOT EXISTS votes (id VARCHAR(255) NOT NULL UNIQUE, vote VARCHAR(255) NOT NULL)");
            st.executeUpdate();
        }catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
        System.err.println("Connect to db");
        return conn;
    }
    
    private static void updateVote(Connection dbConn, String voterID, String vote) throws SQLException {
        PreparedStatement insert = dbConn.prepareStatement("INSERT INTO votes (id,vote) VALUES (?,?)");
        insert.setString(1,voterID);
        insert.setString(2,vote);
        try{
            insert.executeUpdate();
        }catch(SQLException e){
            PreparedStatement update = dbConn.prepareStatement("UPDATE votes SET vote = ? WHERE id = ?");
            update.setString(1,vote);
            update.setString(2,voterID);
            update.executeUpdate();
        }
    }
    
}
