package streams.compiler;

import stream.*;

public class InlineCode${inline.id} implements Processor {

    /**
     * @see stream.Processor#process(stream.Data)
     */
    @Override
    public Data process(Data ${variable}) {
        ${inline.code}
        return ${variable};
    }
}