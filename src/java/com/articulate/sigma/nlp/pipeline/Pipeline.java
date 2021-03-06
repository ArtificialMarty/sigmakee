package com.articulate.sigma.nlp.pipeline;

/*
Copyright 2014-2015 IPsoft

Author: Andrei Holub andrei.holub@ipsoft.com

This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; either version 2 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program ; if not, write to the Free Software
Foundation, Inc., 59 Temple Place, Suite 330, Boston,
MA  02111-1307 USA 
*/

import com.articulate.sigma.KBmanager;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import edu.stanford.nlp.pipeline.*;
import edu.stanford.nlp.util.CoreMap;

import java.util.*;

public class Pipeline {

    final StanfordCoreNLP pipeline;

    /** ***************************************************************
     */
    public Pipeline() {

        this(false);
    }

    /** ***************************************************************
     */
    public Pipeline(boolean useDefaultPCFGModel) {

        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref, entitymentions");
        props.setProperty("parse.kbest", "2");

        if (!useDefaultPCFGModel && !Strings.isNullOrEmpty(KBmanager.getMgr().getPref("englishPCFG"))) {
            props.put("parse.model", KBmanager.getMgr().getPref("englishPCFG"));
            props.put("parser.model", KBmanager.getMgr().getPref("englishPCFG"));
            props.put("parse.flags", "");
        }
        // creates a StanfordCoreNLP object, with POS tagging, lemmatization, NER, parsing, and coreference resolution
        pipeline = new StanfordCoreNLP(props);
    }

    /** ***************************************************************
     */
    public Annotation annotate(String text) {
        
        // create an empty Annotation just with the given text
        Annotation document = new Annotation(text);
        // run all Annotators on this text
        pipeline.annotate(document);
        return document;
    }

    /** ***************************************************************
     */
    public static Annotation toAnnotation(String input) {

        Pipeline pipeline = new Pipeline();
        return pipeline.annotate(input);
    }

    /** ***************************************************************
     */
    public static Annotation toAnnotation(List<String> inputs) {

        return toAnnotation(String.join(" ", inputs));
    }

    /** ***************************************************************
     */
    public List<String> toDependencies(String input) {

        Annotation wholeDocument = annotate(input);
        CoreMap lastSentence = SentenceUtil.getLastSentence(wholeDocument);
        return SentenceUtil.toDependenciesList(ImmutableList.of(lastSentence));
    }

    /** ***************************************************************
     */
    public static void main(String[] args) {

        Annotation a = Pipeline.toAnnotation("Amelia also wrote books, most of them were about her flights.");
        SentenceUtil.printSentences(a);
    }
}