/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package config;


import java.io.InputStream;
import org.yaml.snakeyaml.Yaml;

/**
 *
 * @author oscar
 */
public class Configuration {
    
    private static Configuration config;
    private String pathBase;
    
    public static Configuration getInstance(InputStream in,String pathBase)
    {
        if (config == null)
        {
            Yaml yaml = new Yaml();
            config = (Configuration)yaml.loadAs(in,Configuration.class);
            config.pathBase = pathBase;
        }
        return config;
    }
    
    
    public static Configuration getInstance()
    {
        return config;
    }
    
    private String dburl;
  
  private Configuration()
  {
      
  }
    

    public String getDburl() {
        return "jdbc:sqlite:"+pathBase+"\\"+dburl;
    }

    public void setDburl(String dburl) {
        this.dburl = dburl;
    }

   
    
}
