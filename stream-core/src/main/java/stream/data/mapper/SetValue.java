package stream.data.mapper;

import stream.ConditionedProcessor;
import stream.annotations.Description;
import stream.data.Data;
import stream.expressions.MacroExpander;

@Description( group="Data Stream.Processing.Transformations.Data" )
public class SetValue
    extends ConditionedProcessor
{
    String key;
    String value;
    
    
    /**
     * 
     */
    @Override
    public Data processMatchingData(Data data) {
        if( key != null && value != null ){
        	String val = MacroExpander.expand( value, data );
            data.put( key, val );
        }
        
        return data;
    }

    /**
     * @return the key
     */
    public String getKey()
    {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key)
    {
        this.key = key;
    }

    /**
     * @return the value
     */
    public String getValue()
    {
        return value;
    }

    /**
     * @param value the value to set
     */
    public void setValue(String value)
    {
        this.value = value;
    }
}