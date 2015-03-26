
package cz.ctu.fit.ddw;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.CreoleRegister;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.Node;
import gate.ProcessingResource;
import gate.creole.SerialAnalyserController;
import gate.util.GateException;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Milan Dojchinovski
 * <milan (at) dojchinovski (dot) mk>
 * Twitter: @m1ci
 * www: http://dojchinovski.mk
 */
public class GateClient {
    
    // corpus pipeline
    private static SerialAnalyserController annotationPipeline = null;
    
    // whether the GATE is initialised
    private static boolean isGateInitilised = false;
    
    public void run(){
        
        if(!isGateInitilised){
            
            // initialise GATE
            initialiseGate();            
        }        

        try {                
            // create an instance of a Document Reset processing resource
            ProcessingResource documentResetPR = (ProcessingResource) Factory.createResource("gate.creole.annotdelete.AnnotationDeletePR");

            // create an instance of a English Tokeniser processing resource
            ProcessingResource tokenizerPR = (ProcessingResource) Factory.createResource("gate.creole.tokeniser.DefaultTokeniser");

            // create an instance of a Sentence Splitter processing resource
            ProcessingResource sentenceSplitterPR = (ProcessingResource) Factory.createResource("gate.creole.splitter.SentenceSplitter");
            
            // create an instance of a ANNIE Gazetter processing resource
            ProcessingResource gazetteerPR = (ProcessingResource) Factory.createResource("gate.creole.gazetteer.DefaultGazetteer");
            
            // locate the JAPE grammar file
            /* File japeOrigFile = new File("/home/churchyard/ownCloud/Škola/142/MI-DDW/country-comapny.jape");
            java.net.URI japeURI = japeOrigFile.toURI();
            
            // create feature map for the transducer
            FeatureMap transducerFeatureMap = Factory.newFeatureMap();
            try {
                // set the grammar location
                transducerFeatureMap.put("grammarURL", japeURI.toURL());
                // set the grammar encoding
                transducerFeatureMap.put("encoding", "UTF-8");
            } catch (MalformedURLException e) {
                System.out.println("Malformed URL of JAPE grammar");
                System.out.println(e.toString());
            }
            
            // create an instance of a JAPE Transducer processing resource
            ProcessingResource japeTransducerPR = (ProcessingResource) Factory.createResource("gate.creole.Transducer", transducerFeatureMap);

            */
            
            // create corpus pipeline
            annotationPipeline = (SerialAnalyserController) Factory.createResource("gate.creole.SerialAnalyserController");

            // add the processing resources (modules) to the pipeline
            annotationPipeline.add(documentResetPR);
            annotationPipeline.add(tokenizerPR);
            annotationPipeline.add(sentenceSplitterPR);
            annotationPipeline.add(gazetteerPR);
            //annotationPipeline.add(japeTransducerPR);
            
            Corpus corpus = Factory.newCorpus("");
            // create a document
            try {
                File ny = new File("/home/churchyard/ownCloud/Škola/142/MI-DDW/ny.txt");
                Document document1 = Factory.newDocument(ny.toURL());
                corpus.add(document1);
            } catch (MalformedURLException ex) {
                Logger.getLogger(GateClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            
            // create a corpus and add the document
            
            

            // set the corpus to the pipeline
            annotationPipeline.setCorpus(corpus);

            //run the pipeline
            annotationPipeline.execute();

            // loop through the documents in the corpus
            for(int i=0; i< corpus.size(); i++){

                Document doc = corpus.get(i);

                // get the default annotation set
                AnnotationSet as_default = doc.getAnnotations();

                FeatureMap futureMap = null;
                // get all Token annotations
                AnnotationSet annSetTokens = as_default.get("Token",futureMap);
                System.out.println("Number of Tokens annotations: " + annSetTokens.size());

                ArrayList tokenAnnotations = new ArrayList(annSetTokens);

                // looop through the Token annotations
                for(int j = 0; j < tokenAnnotations.size(); ++j) {

                    // get a token annotation
                    Annotation token = (Annotation)tokenAnnotations.get(j);

                    // get the underlying string for the Token
                    Node isaStart = token.getStartNode();
                    Node isaEnd = token.getEndNode();
                    String underlyingString = doc.getContent().getContent(isaStart.getOffset(), isaEnd.getOffset()).toString();
                    System.out.println("Token: " + underlyingString);
                    
                    // get the features of the token
                    FeatureMap annFM = token.getFeatures();
                    
                    // get the value of the "string" feature
                    String value = (String)annFM.get((Object)"string");
                    System.out.println("Feature: " + value);
                }
            }
        } catch (GateException ex) {
            Logger.getLogger(GateClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initialiseGate() {
        
        try {
            // set GATE home folder
            // Eg. /Applications/GATE_Developer_7.0
            File gateHomeFile = new File("/home/churchyard/GATE/");
            Gate.setGateHome(gateHomeFile);
            
            // set GATE plugins folder
            // Eg. /Applications/GATE_Developer_7.0/plugins            
            File pluginsHome = new File("/home/churchyard/GATE/plugins");
            Gate.setPluginsHome(pluginsHome);            
            
            // set user config file (optional)
            // Eg. /Applications/GATE_Developer_7.0/user.xml
            Gate.setUserConfigFile(new File("/home/churchyard/GATE/", "user.xml"));            
            
            // initialise the GATE library
            Gate.init();
            
            // load ANNIE plugin
            CreoleRegister register = Gate.getCreoleRegister();
            URL annieHome = new File(pluginsHome, "ANNIE").toURL();
            register.registerDirectories(annieHome);
            
            // flag that GATE was successfuly initialised
            isGateInitilised = true;
            
        } catch (MalformedURLException ex) {
            Logger.getLogger(GateClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GateException ex) {
            Logger.getLogger(GateClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
}