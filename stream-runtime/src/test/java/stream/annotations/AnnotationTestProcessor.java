package stream.annotations;

import stream.Data;
import stream.Processor;

/**
 * Test processor for xmlAnnotations
 * Created by bruegge on 3/5/14.
 */

public class AnnotationTestProcessor implements Processor {


    @Parameter
    private String nonoptional;

    @Parameter(required=false)
    private String optional;


    @Override
    public Data process(Data input) {
        System.out.println("Annotation test processor:  ");
        System.out.println("nonoptional: " + nonoptional);
        System.out.println("optional: " + optional);
        return input;
     }


    //getter and setter crap for our fields

    public String getNonoptional() {
        return nonoptional;
    }

    public void setNonoptional(String nonoptional) {
        this.nonoptional = nonoptional;
    }

    public String getOptional() {
        return optional;
    }

    public void setOptional(String optional) {
        this.optional = optional;
    }

}
