package stream.annotations;

import stream.Data;
import stream.Processor;

/**
 * Test processor for xmlAnnotations
 * Created by bruegge on 3/5/14.
 */

public class ConflictingFlagsProcessor implements Processor {


    @Parameter
    private String nonoptional;

    @Parameter(required=false)
    private String conflicting;


    @Override
    public Data process(Data input) {
        conflicting = "Hello teh foo";
        System.out.println("Annotation test processor:  ");
        System.out.println("nonoptional: " + nonoptional);
        System.out.println("conflicting: " + conflicting);
        return input;
    }


    //getter and setter crap for our fields
    @Parameter(required = true)
    public String getNonoptional() {
        return nonoptional;
    }

    public String getConflicting() {
        return conflicting;
    }

    @Parameter(required=true)
    public void setConflicting(String conflicting) {
        this.conflicting = conflicting;
    }


    public void setNonoptional(String nonoptional) {
        this.nonoptional = nonoptional;
    }

}
