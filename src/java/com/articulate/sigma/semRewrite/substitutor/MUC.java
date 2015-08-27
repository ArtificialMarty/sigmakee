package com.articulate.sigma.semRewrite.substitutor;

import TPTPWorld.SystemInfoParser;
import com.articulate.sigma.nlp.pipeline.Pipeline;
import com.articulate.sigma.nlp.pipeline.SentenceUtil;
import com.google.common.collect.Lists;
import edu.stanford.nlp.dcoref.CorefChain;
import edu.stanford.nlp.dcoref.CorefCoreAnnotations;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*
 * Copyright 2014-2015 IPsoft
 *
 * Author: Adam Pease adam.pease@ipsoft.com
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program ; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA  02111-1307 USA
 *
 * Processing to handle the MUC-6 dataset for coreference and compare
 * it to Stanford's CoreNLP coreference results
 */
public class MUC {

    public static Annotation document = null;
    public static HashSet<Coref> stanfordCorefs = new HashSet<>();

    /****************************************************************
     */
    public class Coref {

        int ID;
        String token;
        int ref = -1;
        HashMap<String, String> info = new HashMap<>();
        int sentNum;
        int firstToken;
        int lastToken;
        int chainID;

        public String toString() {
            return Integer.toString(ID) + ":" +
                    token + ":" +
                    info + ":" +
                    Integer.toString(sentNum) + ":" +
                    Integer.toString(firstToken) + ":" +
                    Integer.toString(lastToken);
        }
    }

    /****************************************************************
     */
    public static List<Coref> buildCorefList(String input) {

        ArrayList<Coref> corefs = new ArrayList<Coref>();
        return corefs;
    }

    /****************************************************************
     */
    public static String first100(StringBuffer input) {

        if (input.length() > 100)
            return input.toString().substring(0,100) + "...";
        else
            return input.toString();
    }

    /****************************************************************
     * convenience method to convert a set of corefs into a map that
     * can then be input to @see printCorefList()
     */
    public static HashMap<Integer,Coref> toMap(HashSet<Coref> cs) {

        HashMap<Integer,Coref> sorted = new HashMap<>();
        for (Coref c : cs) {
            sorted.put(c.ID, c);
        }
        return sorted;
    }

    /****************************************************************
     */
    public static void printCorefList(HashMap<Integer,Coref> cs) {

        TreeMap<Integer,Coref> corefs = new TreeMap<>();
        corefs.putAll(cs);
        for (Integer i : corefs.keySet()) {
            Coref c = corefs.get(i);
            System.out.println(c);
        }
    }

    /****************************************************************
     */
    public void printStanfordCorefList(Map<Integer, CorefChain> graph) {

        for (CorefChain cc : graph.values()) {
            List<CorefChain.CorefMention> mentions = cc.getMentionsInTextualOrder();
            if (mentions.size() > 1) {
                for (CorefChain.CorefMention ment : mentions) {
                    Coref c = new Coref();
                    c.ID = ment.mentionID;
                    c.token = ment.mentionSpan;
                    HashMap<String, String> info = new HashMap<>();
                    c.sentNum = ment.sentNum;
                    c.firstToken = ment.headIndex;
                    int lastToken;
                    System.out.println(ment.sentNum + " : " + ment.headIndex + " : " + ment.mentionSpan);
                }
                System.out.println();
            }
        }
    }

    /****************************************************************
     */
    public static String listToString(List<String> input) {

        StringBuffer sb = new StringBuffer();
        for (String s : input)
            sb.append(s + " ");
        return sb.toString();
    }

    /****************************************************************
     * Use the Stanford sentence tokenizer to convert the input to a list
     * of Strings with one sentence per string
     */
    public static List<String> toSentences(String input) {

        List<String> results = new ArrayList<String>();
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        document = new Annotation(input);
        pipeline.annotate(document);
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            //System.out.println(sentence);
            results.add(sentence.get(CoreAnnotations.TextAnnotation.class));
        }
        return results;
    }

    /****************************************************************
     * Convert Stanford corefs into MUC-style coreference chains.
     */
    public HashMap<Integer, HashSet<Coref>> stanfordToCoref(Annotation document) {

        HashMap<Integer, HashSet<Coref>> result = new HashMap<>();
        int ID = 0;
        Map<Integer, CorefChain> graph = document.get(CorefCoreAnnotations.CorefChainAnnotation.class);
        for (CorefChain cc : graph.values()) {
            List<CorefChain.CorefMention> mentions = cc.getMentionsInTextualOrder();
            if (mentions.size() > 1) {
                HashSet<Coref> newchain = new HashSet<>();
                for (CorefChain.CorefMention ment : mentions) {
                    Coref c = new Coref();
                    c.ID = ment.mentionID;
                    c.token = ment.mentionSpan;
                    HashMap<String, String> info = new HashMap<>();
                    c.sentNum = ment.sentNum;
                    c.firstToken = ment.headIndex;
                    int lastToken;
                    newchain.add(c);
                }
                result.put(ID,newchain);
                ID++;
            }
        }
        return result;
    }

    /****************************************************************
     * @return a list of sentences with tokens
     */
    public ArrayList<ArrayList<String>> toCoref(String input) {

        //System.out.println("INFO in MUC.toCoref(): " + input);
        List<Coref> corefs = buildCorefList(input);
        ArrayList<ArrayList<String>> results = new ArrayList<ArrayList<String>>();
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma, ner, parse, dcoref");
        props.setProperty("tokenize.options", "ptb3Escaping=false");
        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
        document = new Annotation(input);
        try {
            pipeline.annotate(document);
        }
        catch (Exception e) {
            System.out.println("input: " + input);
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        List<CoreMap> sentences = document.get(CoreAnnotations.SentencesAnnotation.class);
        //SentenceUtil.printCorefChain(document);
        System.out.println("Stanford corefs: ");
        Map<Integer, CorefChain> graph = document.get(CorefCoreAnnotations.CorefChainAnnotation.class);
        printStanfordCorefList(graph);

        for (CoreMap sentence : sentences) {
            //System.out.println(sentence);
            ArrayList<String> tokenList = new ArrayList<>();
            //results.add(sentence.get(CoreAnnotations.TextAnnotation.class));
            List<CoreLabel> tokens = sentence.get(CoreAnnotations.TokensAnnotation.class);
            for (CoreLabel t : tokens) {
                String t2 = t.toString();
                if (t2.startsWith("-LRB-"))
                    t2 = t2.replace("-LRB-", "(");
                if (t2.startsWith("-RRB-"))
                    t2 = t2.replace("-RRB-", ")");
                if (t2.startsWith("``"))
                    t2 = t2.replace("``", "\"");
                if (t2.startsWith("''"))
                    t2 = t2.replace("''", "\"");
                // -LCB-,  -RCB-, ???
                System.out.print(t2 + " ");
                tokenList.add(t2);
            }
            results.add(tokenList);
            System.out.println();
        }
        return results;
    }

    /****************************************************************
     */
    public static List<String> getDocuments(String filename) {

        List<String> lines = new ArrayList<String>();
        System.out.println("INFO in MUC.cleanSGML(): Reading files");
        LineNumberReader lr = null;
        try {
            String line;
            StringBuffer doc = new StringBuffer();
            File nounFile = new File(filename);
            if (nounFile == null) {
                System.out.println("Error in MUC.cleanSGML(): The file does not exist ");
                return lines;
            }
            long t1 = System.currentTimeMillis();
            FileReader r = new FileReader(nounFile);
            lr = new LineNumberReader(r);
            while ((line = lr.readLine()) != null) {
                if (lr.getLineNumber() % 1000 == 0)
                    System.out.print('.');
                line = line.trim();
                line = line.replace("<HL>", "");
                line = line.replace("</HL>", ".");
                line = line.replace("<IN>", "");
                line = line.replace("</IN>", ".");
                line = line.replace("<s>", "");
                line = line.replace("</s>", "");
                line = line.replace("----", ".");
                line = line.replaceAll("^\\@", "");
                if (line.contains("</DOC>")) {
                    lines.add(doc.toString());
                    doc = new StringBuffer();
                }
                else if (line.contains("<DOC>") ||
                        line.contains("<DOCID>") ||
                        line.contains("<DOCNO>") ||
                        line.contains("<p>") ||
                        line.contains("</p>") ||
                        line.contains("<DD>") ||
                        line.contains("<AN>") ||
                        line.contains("<CODER>") ||
                        line.contains("<SO>") ||
                        line.contains("<DATELINE>") ||
                        line.contains("<TXT>") ||
                        line.contains("<CO>") ||
                        line.contains("</TXT>") ||
                        line.contains("<CO>") ||
                        line.contains("<IN>") ||
                        line.contains("</IN>")) {
                }
                else
                    doc.append(line + " ");
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            try {
                if (lr != null) {
                    lr.close();
                }
            }
            catch (Exception ex) {
            }
        }
        return lines;
    }

    /****************************************************************
     */
    public static List<String> cleanSGML(String filename) {

        List<String> lines = new ArrayList<String>();
        System.out.println("INFO in MUC.cleanSGML(): Reading files");
        LineNumberReader lr = null;
        try {
            String line;
            File nounFile = new File(filename);
            if (nounFile == null) {
                System.out.println("Error in MUC.cleanSGML(): The file does not exist ");
                return lines;
            }
            long t1 = System.currentTimeMillis();
            FileReader r = new FileReader(nounFile);
            lr = new LineNumberReader(r);
            while ((line = lr.readLine()) != null) {
                if (lr.getLineNumber() % 1000 == 0)
                    System.out.print('.');
                line = line.trim();
                line = line.replaceAll("<[^>]+>", "");
                line = line.replaceAll("<[^>]+$", "");
                line = line.replaceAll("^[^>]+>", "");
                lines.add(line);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        finally {
            try {
                if (lr != null) {
                    lr.close();
                }
            }
            catch (Exception ex) {
            }
        }
        return lines;
    }

    /****************************************************************
     * Modify @param sb to remove the characters in @param token from
     * its starting characters.
     */
    private static void removeToken(StringBuffer sb, String token) {

        System.out.println("removeToken() remove '" + token + "'");
        System.out.println("removeToken() before: " + first100(sb));
        if (sb == null || sb.length() < 1) {
            System.out.println("Error in removeToken() - null string with token: " + token);
            return;
        }
        while (Character.isWhitespace(sb.toString().charAt(0)))
            sb.deleteCharAt(0);
        if (sb.toString().startsWith(token)) {
            sb.delete(0, token.length());
        }
        else {
            System.out.println("Error in removeToken() - no match for '" + token + "' in " + first100(sb));
        }
        System.out.println("after: " + first100(sb));
    }

    /***************************************************************
     */
    private static void expandCurrentToken(String token,
                                           Stack<Integer> currentCoref,
                                           HashMap<Integer, String> corefTokens) {

        Integer id = currentCoref.peek();
        String tok = corefTokens.get(id);
        if (tok.isEmpty())
            corefTokens.put(id, token);
        else
            corefTokens.put(id, tok + " " + token);
    }

    /****************************************************************
     */
    private static void leadingTrim(StringBuffer sb) {

        if (sb == null || sb.length() > 1)
            return;
        while (sb.length() > 0 && Character.isWhitespace(sb.toString().charAt(0)))
            sb.deleteCharAt(0);
    }

    /****************************************************************
     */
    private static String processOneParamString(String paramlist, Coref c) {

        //System.out.println("processOneParamString(): " + paramlist);
        int space = paramlist.indexOf(' ');
        int equals = paramlist.indexOf('=');
        int quote1 = paramlist.indexOf('"');
        int offset = quote1;
        int index = quote1 + 1;
        while (index < paramlist.length() &&
                (paramlist.charAt(index) != '"' || (index > 0 && paramlist.charAt(index - 1) == '\\'))) {
            index++;
        }
        int quote2 = paramlist.indexOf('"', index);
        String key = paramlist.substring(space + 1, equals);

        String value = paramlist.substring(quote1 + 1, quote2);
        //System.out.println(value);
        if (key.equals("REF"))
            c.ref = Integer.parseInt(value);
        c.info.put(key, value);
        paramlist = paramlist.substring(quote2 + 1);
        //System.out.println("result of processOneParamString(): " + c);
        return paramlist;
    }

    /****************************************************************
     */
    private static void processParams(Coref c, HashMap<Integer, String> corefParams) {

        String paramlist = corefParams.get(c.ID);
        //System.out.println("processParams(): " + paramlist);
        while (paramlist.indexOf('"') > -1) {
            paramlist = processOneParamString(paramlist, c);
        }
    }

    /***************************************************************
     * @return the String content of a COREF tag.
     * Destructively modifies sb to remove the tag
     */
    private static String getTag(StringBuffer sb, Matcher m) {

        if (sb.indexOf("<") > -1)
            sb.delete(0, sb.indexOf("<"));
        String tag = sb.toString().substring(0, sb.indexOf(">") + 1);
        sb.delete(0, sb.indexOf(">") + 1);
        return tag;
    }

    /****************************************************************
     * Build chains of coreferences based on their pairwise references
     */
    private static HashMap<Integer, HashSet<Coref>> buildChains(HashMap<Integer, Coref> corefs) {

        HashMap<Integer, Integer> chainMap = new HashMap<>(); // coref id to chain id
        HashMap<Integer, HashSet<Coref>> chains = new HashMap<>(); // chain id to members
        int chainNum = 0;
        boolean first = true;
        Coref firstC = null;
        for (Integer i : corefs.keySet()) {
            Coref c = corefs.get(i);
            if (first) {
                firstC = c;
                first = false;
            }
            if (!chainMap.keySet().contains(c.ID)) {
                if (c.ref == -1) {
                    chainMap.put(c.ID, chainNum);
                    HashSet<Coref> chain = new HashSet<>();
                    chain.add(c);
                    c.chainID = chainNum;
                    chains.put(chainNum, chain);
                    chainNum++;
                }
                else if (!chainMap.keySet().contains(c.ref)) {
                    chainMap.put(c.ID, chainNum);
                    chainMap.put(c.ref, chainNum);
                    HashSet<Coref> chain = new HashSet<>();
                    c.chainID = chainNum;
                    chain.add(c);
                    Coref cref = corefs.get(c.ref);
                    if (cref != null)
                        chain.add(cref);
                    else
                        System.out.println("Error in MUC.buildChains(): No coref for id: " + c.ref);
                    chains.put(chainNum, chain);
                    chainNum++;
                }
                else {
                    int ref = chainMap.get(c.ref);
                    chains.get(ref).add(c);
                    chainMap.put(c.ID, ref);
                }
            }
            else {
                if (c.ref == -1) {
                } // no reference so do nothing
                else if (!chainMap.keySet().contains(c.ref)) {
                    chainMap.put(c.ID, chainNum);
                    chainMap.put(c.ref, chainNum);
                    HashSet<Coref> chain = new HashSet<>();
                    c.chainID = chainNum;
                    chain.add(c);
                    Coref cref = corefs.get(c.ref);
                    if (cref != null)
                        chain.add(cref);
                    else
                        System.out.println("Error in MUC.buildChains(): No coref for id: " + c.ref);
                    chain.add(cref);
                    chains.put(chainNum, chain);
                    chainNum++;
                }
                else {
                    int ref = chainMap.get(c.ref);
                    chains.get(ref).add(c);
                }
            }
        }
        return chains;
    }

    /****************************************************************
     * Strip a Stanford token number suffix from the token
     */
    private static String stripTokenNum(String t) {

        if (t.lastIndexOf("-") < 0)
            return t;
        return t.substring(0,t.lastIndexOf("-"));
    }

    /****************************************************************
     */
    private static void printChains(HashMap<Integer, HashSet<Coref>> corefs) {

        for (Integer i : corefs.keySet()) {
            HashSet<Coref> cs = corefs.get(i);
            printCorefList(toMap(cs));
            System.out.println();
        }
    }

    /****************************************************************
     */
    private static boolean find(Coref c, HashMap<Integer,HashSet<Coref>> chains) {

        for (int i : chains.keySet()) {
            HashSet<Coref> chain = chains.get(i);
            for (Coref c2 : chain) {
                if (c2.sentNum == c.sentNum && c2.firstToken == c.firstToken)
                    return true;
            }
        }
        return false;
    }

    /** ***************************************************************
     * Compare Stanford and MUC coreference chains.  Create a map for
     * each token in MUC whether it is in Stanford and in which chain
     * ID and which Stanford token it corresponds to. Use that map to
     * score which tokens are not found (errors of omission, or false
     * negatives).  Mark the tokens that are found in both MUC and
     * Stanford in a separate map. Use that map to create a third map
     * of tokens that are in Stanford but not in MUC.
     * @param chains are the MUC chains
     * @param document contains the Stanford chains
     */
    public void compareChains(HashMap<Integer,HashSet<Coref>> chains,
                              Annotation document) {

        HashMap<Integer,Coref> stanfordNotMUC = new HashMap<>();
        HashMap<Integer,Coref> MUCNotStanford = new HashMap<>();

        HashMap<Integer,HashSet<Coref>> stanfordChains = stanfordToCoref(document);
        for (int i : stanfordChains.keySet()) {
            HashSet<Coref> chain = chains.get(i);
            if (chain != null) {
                for (Coref c : chain) {
                    boolean found = find(c, chains);
                    if (!found)
                        stanfordNotMUC.put(c.ID, c);
                }
            }
        }
        System.out.println("Stanford not MUC: ");
        printCorefList(stanfordNotMUC);

        for (int i : chains.keySet()) {
            HashSet<Coref> chain = chains.get(i);
            for (Coref c : chain) {
                boolean found = find(c, stanfordChains);
                if (!found)
                    MUCNotStanford.put(c.ID,c);
            }
        }
        System.out.println("MUC not Stanford  : ");
        printCorefList(MUCNotStanford);
    }

    /** ***************************************************************
     * Pick tokens off the input sentence string, capturing corefXML
     * when present and aligning the corefXML with token numbers
     */
    public void makeCorefList(String sentsDirty,
                              ArrayList<ArrayList<String>> tokenized) {

        StringBuffer sb = new StringBuffer(sentsDirty);
        HashMap<Integer,Coref> corefs = new HashMap<>();
        HashMap<Integer,String> corefTokens = new HashMap<>();
        HashMap<Integer,String> corefParams = new HashMap<>();
        HashMap<Integer,Integer> references = new HashMap<>();
        Stack<Integer> currentCoref = new Stack<>();
        Pattern p1 = Pattern.compile("^\\s*(<COREF[^>]+>)");
        Pattern p2 = Pattern.compile("^\\s*(</COREF>)");
        int sentNum = 0;
        int level = 0;
        int tokenNum = 0;
        int firstToken = 0;
        boolean openTag = false;
        String tag = "";
        while (sb.length() > 0) {
            if (sentNum > tokenized.size() - 1) {
                System.out.println("Error in MUC.makeCorefList(): no tokenized sentence for: " + sb);
                break;
            }
            ArrayList<String> tokens = tokenized.get(sentNum);
            System.out.println("Num tokens: " + tokens.size());
            sentNum++;
            String lastToken = "";
            for (String t : tokens) {
                System.out.println("Token: " + t + " Last token: " + lastToken);
                System.out.println("sb: " + first100(sb));
                // Stanford can insert an extra period if the last token in a sentence is an abbreviation
                boolean open = false;
                boolean close = false;
                do {
                    Matcher m1 = p1.matcher(sb.toString());
                    Matcher m2 = p2.matcher(sb.toString());
                    open = m1.find();
                    close = m2.find();
                    if (open) {
                        tag = getTag(sb, m1);
                        level++;
                        int quoteIndex = tag.indexOf("\"");
                        String id = tag.substring(quoteIndex + 1, tag.indexOf("\"", quoteIndex + 1));
                        currentCoref.push(Integer.parseInt(id));
                        corefTokens.put(Integer.parseInt(id), "");
                        corefParams.put(Integer.parseInt(id), tag);
                        int refIndex = tag.indexOf("REF=");
                        if (refIndex > -1) {
                            int refQuoteIndex = tag.indexOf("\"", refIndex + 1);
                            String ref = tag.substring(refQuoteIndex + 1, tag.indexOf("\"", refQuoteIndex + 1));
                            references.put(Integer.parseInt(id), Integer.parseInt(ref));
                        }
                        openTag = true;
                    }
                    if (close) {
                        if (sb.indexOf("<") > -1)
                            sb.delete(0, sb.indexOf("<"));
                        sb.delete(0, sb.indexOf(">") + 1);
                        if (currentCoref.size() < 1) {
                            System.out.println("Error in MUC.makeCorefList(): no open tag for close tag\n" + first100(sb));
                            return;
                        }
                        Integer cid = currentCoref.pop();
                        Coref c = new Coref();
                        c.ID = cid;
                        c.token = corefTokens.get(cid);
                        c.firstToken = firstToken + 1;
                        c.lastToken = tokenNum + 1;
                        processParams(c,corefParams);
                        c.sentNum = sentNum;
                        corefs.put(c.ID, c);
                        level--;
                    }
                } while (open || close);
                String tokenNumStr = t.substring(t.lastIndexOf("-") + 1, t.length());
                tokenNum = Integer.parseInt(tokenNumStr);
                if (openTag)
                    firstToken = tokenNum;
                String token = stripTokenNum(t);
                if (stripTokenNum(t).equals(".") && stripTokenNum(lastToken).endsWith(".") &&
                        !sb.toString().matches("^\\s*\\..*")) {
                    System.out.println("makeCorefList() Skipping token removal");
                    System.out.println(first100(sb));
                    continue;
                }
                lastToken = t;
                leadingTrim(sb);
                removeToken(sb, token);
                if (level > 0)
                    expandCurrentToken(token, currentCoref, corefTokens);
                openTag = false;
            }
        }
        HashMap<Integer,HashSet<Coref>> chains = buildChains(corefs);
        printChains(chains);
        compareChains(chains, document);
    }

    /** ***************************************************************
     */
    public static void testParamString() {

        MUC muc = new MUC();
        String paramstring = "<COREF ID=\"49\" TYPE=\"IDENT\" REF=\"43\" MIN=\"Robert S. \\\"Steve\\\" Miller\">";
        Coref c = muc.new Coref();
        paramstring = processOneParamString(paramstring,c);
        paramstring = processOneParamString(paramstring,c);
        paramstring = processOneParamString(paramstring,c);
        paramstring = processOneParamString(paramstring,c);
    }

    /** ***************************************************************
     */
    public static void testRemoveToken() {

        MUC muc = new MUC();
        String token = "Corp.";
        StringBuffer sb = new StringBuffer("Corp.</COREF> <COREF ID=\"13\" TYPE=\"IDENT\" REF=\"10\">He</COREF> also served for 10 years as <COREF ID=\"14\" TYPE=\"IDENT\" REF=\"13\" STATUS=\"OPT\">chairman</COREF> and <COREF ID=\"15\" TYPE=\"IDENT\" REF=\"13\" MIN=\"executive\" STATUS=\"OPT\">chief executive of Paramount Pictures Corp., a unit of Paramount Communications Inc.</COREF> Arrow Investments Inc., a corporation controlled by <COREF ID=\"16\" TYPE=\"IDENT\" REF=\"13\" MIN=\"Diller\">Mr. Diller</COREF>, in <COREF ID=\"19\">December</COREF> agreed to purchase $25 million of <COREF ID=\"17\" TYPE=\"IDENT\" REF=\"6\">QVC</COREF> stock in a privately negotiated transaction.");
        Coref c = muc.new Coref();
        removeToken(sb,token);
        System.out.println("MUC.testRemoveToken: " + sb);
    }

    /** ***************************************************************
     */
    public static void testWhitespace() {

        String paramstring = "    .   By <COREF ID=\"1\" MIN=\"Patrick M. Reilly\">Patrick M. Reilly   <COREF ID=\"0\" TYPE=\"IDENT\" REF=\"1\" MIN=\"Reporter\"";
        if (paramstring.matches("^\\s*\\..*"))
            System.out.println("Match!");
        else
            System.out.println("no match");
    }

    /** ***************************************************************
     */
    public void testParallelPipeline() {

        try (BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
            Properties preprocessprops = new Properties();
            preprocessprops.setProperty("annotators", "tokenize, ssplit,pos,lemma, ner, parse");
            preprocessprops.setProperty("tokenize.options", "ptb3Escaping=false");
            StanfordCoreNLP preprocesspipeline = new StanfordCoreNLP(preprocessprops);

            Properties corefprops = new Properties();
            corefprops.setProperty("annotators", "dcoref");
            corefprops.setProperty("tokenize.options", "ptb3Escaping=false");
            corefprops.setProperty("enforceRequirements","false");
            StanfordCoreNLP corefpipeline = new StanfordCoreNLP(corefprops);

            List<CoreMap> coreMaps= Lists.newArrayList();
            String input;
            while ((input = br.readLine()) != null) {
                Annotation document = new Annotation(input);
                preprocesspipeline.annotate(document);
                List<CoreMap> newcoreMaps = document.get(CoreAnnotations.SentencesAnnotation.class);
                coreMaps.addAll(newcoreMaps);
                System.out.println("Stanford corefs: ");
                Annotation wholeDocument=new Annotation(coreMaps);
                corefpipeline.annotate(wholeDocument);

                Map<Integer, CorefChain> graph = wholeDocument.get(CorefCoreAnnotations.CorefChainAnnotation.class);
                printStanfordCorefList(graph);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** ***************************************************************
     */
    public static void main(String[] args) {

        //testWhitespace();

        //String line = "@  To Be Strong Candidate to Head <COREF ID=\"3\">SEC</COREF>";
        //line = line.replaceAll("^\\@","");
        //System.out.println(line);
        //List<String> lines = cleanSGML("/home/apease/IPsoft/corpora/muc6/data/keys/formal-tst.CO.key.cleanup.09jul96");
        List<String> lines = getDocuments("/home/apease/IPsoft/corpora/muc6/data/keys/formal-tst.CO.key.cleanup.09jul96");
        //List<String> lines = getDocuments("/home/apease/IPsoft/corpora/muc6/data/keys/891101-0056.co.v0.sgm" + "");
        MUC muc = new MUC();
        for (String s : lines) {
            String cleanedInput = s.replaceAll("<COREF[^>]+>", "");
            cleanedInput = cleanedInput.replace("</COREF>","");
            List<String> sentsClean = toSentences(cleanedInput);
            List<String> sentsDirty = toSentences(s);
            System.out.println("MUC markup: " + sentsDirty);
            String allClean = listToString(sentsClean);
            ArrayList<ArrayList<String>> tokenized = muc.toCoref(listToString(sentsClean));
            muc.makeCorefList(s, tokenized);
        }

    }
}
