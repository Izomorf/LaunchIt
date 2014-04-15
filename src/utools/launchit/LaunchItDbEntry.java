/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package utools.launchit;

import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author ruinmaxk
 */
public class LaunchItDbEntry {
    private HashMap<String, String> values = new HashMap<String, String>();
    
    public LaunchItDbEntry() {}
    
    public void setValue(String index, String value) {
        values.put(index, value);
    }
    
    public int getIntValue(String index) {
        if (index.equals(LaunchItConstants.COLUMN_ROWID)) {
            return Integer.valueOf(values.get(index));
        }
        return -1;
    }
    
    public String getValue (String index) {
        return values.get(index);
    }
    
    public Set<String> getKeys() {
        return values.keySet();
    }
    
    public String getAllKeysAndValues() {
        int size = values.keySet().size();
        int t = 1;
        StringBuilder sb  = new StringBuilder();
        for (String key : values.keySet()) {
            sb.append(key + "=" + values.get(key));
            if (t++ < size) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }
}
