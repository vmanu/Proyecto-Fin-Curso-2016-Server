/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config;


import java.io.InputStream;
import org.yaml.snakeyaml.Yaml;
//import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author oscar
 */
public class Configuration {
    
    private static Configuration config;
    private String USERNAME=System.getenv("OPENSHIFT_MYSQL_DB_USERNAME");
    private String PASSWORD=System.getenv("OPENSHIFT_MYSQL_DB_PASSWORD");
    private String HOST=System.getenv("OPENSHIFT_MYSQL_DB_HOST");
    private String PORT=System.getenv("OPENSHIFT_MYSQL_DB_PORT");
    private String DBNAME="PPTGAME";
    
    
    public static Configuration getInstance(InputStream in,String pathBase)
    {
        System.out.println("GET INSTANCE, COÑO");
        if (config == null)
        {
            Yaml yaml = new Yaml();
            config = (Configuration)yaml.loadAs(in,Configuration.class);
        }
        return config;
    }
    
    
    public static Configuration getInstance()
    {
        System.out.println("GET INSTANCE, COÑO SIN PARÁMETROS");
        return config;
    }
    
    private String dburl;
  
  private Configuration()
  {
      
  }
    

    public String getDburl() {
        
        return "jdbc:"+dburl+"/"+DBNAME;
    }

    public void setDburl(String dburl) {
        this.dburl = dburl;
    }

    public String getUSERNAME() {
        return USERNAME;
    }

    public void setUSERNAME(String USERNAME) {
        this.USERNAME = USERNAME;
    }

    public String getPASSWORD() {
        return PASSWORD;
    }

    public void setPASSWORD(String PASSWORD) {
        this.PASSWORD = PASSWORD;
    }

    public String getHOST() {
        return HOST;
    }

    public void setHOST(String HOST) {
        this.HOST = HOST;
    }

    public String getPORT() {
        return PORT;
    }

    public void setPORT(String PORT) {
        this.PORT = PORT;
    }

    public String getDBNAME() {
        return DBNAME;
    }

    public void setDBNAME(String DBNAME) {
        this.DBNAME = DBNAME;
    }

   
    
}
