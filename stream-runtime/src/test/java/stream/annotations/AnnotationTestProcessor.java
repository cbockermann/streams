package stream.annotations;

import stream.Data;
import stream.Processor;

/**
 * Test processor for xmlAnnotations
 * Created by bruegge on 3/5/14.
 */

public class AnnotationTestProcessor implements Processor {


    @XMLParameter
    private String nonoptional;

    @XMLParameter(optional=true)
    private String optional;

    private String unannotated;

    @Override
    public Data process(Data input) {
        System.out.println("Annotation test processor:  ");
        System.out.println("nonoptional: " + nonoptional);
        System.out.println("optional: " + optional);
        System.out.println("unannotated: " + unannotated);
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

    public String getUnannotated() {
        return unannotated;
    }

    public void setUnannotated(String unannotated) {
        this.unannotated = unannotated;
    }
}
