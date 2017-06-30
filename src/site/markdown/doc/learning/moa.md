#Integrating MOA


[MOA]() is a software package for online learning tasks. 
It provides a large set of clustering and classifier implementations suited for online and incremental learning. 
Its main intend is to serve as an environment for evaluating online algorithms.

The *streams* framework provides the `stream-analysis` artifact, which includes MOA and allows for integrating MOA classifiers directly into standard stream processes.
The following example XML snippet shows the use of the Naive Bayes
implementation of MOA within a *streams* process. The example defines
a standard test-then-train process.

      <container>
           <stream id="stream" class="stream.io.CsvStream"
                   url="classpath:/multi-golf.csv.gz" limit="100"/>

           <process input="stream">
                <RenameKey from="play" to="@label" />
        
                <stream.learner.Prediction learner="NB" />

                <stream.learner.evaluation.PredictionError />

                <moa.classifiers.bayes.NaiveBayes id="NB"/>

                <stream.statistics.Sum keys="@error:NB" />
           </process>
      </container>

The options used in MOA are directly mapped to XML element attributes.

``streams-analysis`` can be embedded into your own project:

		<dependency>
		    <groupId>de.sfb876</groupId>
		    <artifactId>streams-analysis</artifactId>
		    <version>1.0.0</version>
		</dependency>

The code can be found [here](https://bitbucket.org/cbockermann/stream-analysis).

## Overview of the algorithms

###todo 
add an overview of the available algorithms