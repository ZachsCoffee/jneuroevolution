/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package json_reader;

import org.json.simple.JSONObject;

/**
 *
 * @author arx-dev-3a-19
 */
public class JSONReader {
    private JSONObject jsonObject;
    
    public JSONReader(JSONObject jsonObject){
        this.jsonObject = jsonObject;
    }
    public JSONReader(Object jsonObject){
        this.jsonObject = (JSONObject) jsonObject;
    }
    
    /**
     * This method trying to get the JSON object you ask. The variable objectNames is the path of objects of JSON 
     * @param objectNames the path of objects of JSON. e.g ("object1", "object2"), the method search for jsonObject.object1.object2
     * @return Returns the value of object
     */
    public Object get(String... objectNames){
        
        Object object = jsonObject.get(objectNames[0]);
        for (int i=1; i<objectNames.length; i++){
            object = ((JSONObject) object).get(objectNames[i]);
        }
        
        return object;
    }
    
    public Object get(JSONObject jsonObject, String... objectNames){
        
        Object object = jsonObject;
        for (String fieldName : objectNames){
            object = ((JSONObject) object).get(fieldName);
        }
        
        return object;
    }
}
